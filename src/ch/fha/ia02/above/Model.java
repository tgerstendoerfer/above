package ch.fha.ia02.above;

import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * The Model is a model of our World/Universe. It is used to display the Object
 * in this world and hold all information of this abstract World.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class Model {

	/** Constants for determing the Models state. */
	public static final int STATUS_NOT_INITIALIZED = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_READY = 3;


	/** Minimal duration of a calculation step, in milliseconds. */
	private static final int dtMin = 10;

	/** Maximal duration of a calculation step, in milliseconds. */
	private static final int dtMax = 80;

	/** Subscenegraph with all the Vessels. */
	private ModelGroup modelGroup = new ModelGroup();

	/** Point in time at which the model was computed. */
	private long t = 0;

	/** ModelTrigger which wake the model up for compute. */
	private ModelTrigger modelTrigger;

	/** The average position of all agents in the model. */
	Vector3f center = new Vector3f();

	/**
	 * The step number.
	 * This number starts at zero and is incremented whenever
	 * a step has been computed for all agents in this model.
	 * Please note that this number is not incremented if
	 * <tt>compute()</tt> returned <tt>false</false>.
	 */
	private long stepnum = 0;

	/** Ring buffer to store performance data. */
	private long[] perfdata;

	/** Average time to compute one step, in milliseconds. */
	int avgCompTime = 0;


	private Agent[] agents;

	/** Returns a reference to the array of all agents in this model. */
	Agent[] getAgents() {
		return agents;
	}

	/**
	 * Returns the number of agents in this model.
	 * @see Agent
	 */
	public int numAgents() {
		return agents.length;
	}

	/** Returns the status of this model. */
	public int getStatus() {
		if (modelTrigger == null) {
			return STATUS_NOT_INITIALIZED;
		} else if (!modelTrigger.isRunning()) {
			return STATUS_READY;
		} else {
			return STATUS_RUNNING;
		}
	}

	/**
	 * Creates a new model that contains the supplied agents.
	 * The triggering scheme used depends on the current settings.
	 *
	 * @param viewObjects a set of {@link ViewObject} instances
	 *        with each representing an agent in the model.
	 *
	 * @throws ClassCastException if <tt>viewObjects</tt> contains an
	 *         object that cannot be casted to <tt>ViewObject</tt>.
	 * @throws IllegalArgumentException if a consistency check fails.
	 */
	public Model(Set viewObjects) {
		init(viewObjects);

		if (Application.getSettings().useTimeTrigger()) {
			int dt = Application.getSettings().getTriggerStepDuration();
			if (dt > dtMax) dt = dtMax;
			if (dt < dtMin) dt = dtMin;
			modelTrigger = new TriggerThread(dt);
		} else {
			modelTrigger = new TriggerBehavior();
			modelGroup.addChild((Behavior)modelTrigger);
		}

		if (Application.getSettings().collectPerformanceData()) {
			perfdata = new long[100];
		}
	}


	/**
	 * Initializes this model. Called by the constructors.
	 *
	 * @param viewObjects a set of {@link ViewObject} instances
	 *        with each representing an agent in the model.
	 */
	private void init(Set viewObjects) {
		// copy the objects from the list to
		// an array for performance reasons
		agents = new Agent[viewObjects.size()];
		Iterator it = viewObjects.iterator();
		for (int i=0; i<agents.length; i++) {
			ViewObject vObj = (ViewObject)it.next();
			agents[i] = vObj.getAgent();;
			agents[i].validate();
			modelGroup.addChild(vObj);
		}
	}


	/**
	 * Computes the state of the model after <var>dt</var> seconds.
	 * Calls {ModelObject#compute(float, Agent[])} on all objects
	 * in this model.
	 *
	 * @param dt time in milliseconds since the last step.
	 * @return <tt>true</tt> if the model was recalculated.
	 */
	public boolean compute(long dt) {
		if (dt < dtMin) return false;
		center.set(0,0,0);
		float n = 0;
		float ft = (float)dt/1000;
		long starttime = System.currentTimeMillis();
		for (int i=0; i<agents.length; i++) {
			agents[i].compute(ft, agents);
			if (agents[i].stats.isMoveable() && agents[i].health > 0) {
				center.add(agents[i].position);
				center.add(agents[i].velocity);
				n++;
			}
		}
		if (n > 0) center.scale(1/n);
		stepnum++;
		t += dt;
		if (perfdata != null) {
			recordPerformance(System.currentTimeMillis() - starttime);
		}
		return true;
	}


	/**
	 * Records performance data in a ring buffer.
	 */
	private void recordPerformance(long millis) {
		int pos = (int)(stepnum % perfdata.length);
		perfdata[pos] = millis;
	}

	/**
	 * Computes the average wall-clock time used to compute each
	 * step for all agents in this model.
	 * Note that the <tt>CollectPerformanceData</tt>
	 * configuration setting must be enabled or this method
	 * will always return zero.
	 *
	 * @return the average time used for the computation,
	 *         or <tt>0</var> if no performance data is available.
	 *
	 * @see Settings#collectPerformanceData()
	 */
	public int getAverageCompTime() {
		int t = 0;
		int n = 0;
		if (perfdata != null) {
			for (int i=0; i<perfdata.length; i++) {
				if (perfdata[i] > 0) {
					t += perfdata[i];
					n++;
				}
			}
		}
		return (n > 1) ? t/n : t;
	}


	/** Returns a string prepresentation of this model. */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Model: t=").append(t).append("ms");
		sb.append(", ").append(stepnum).append(" steps");
		sb.append(", ").append(agents.length).append(" agents");
		if (perfdata != null) {
			sb.append(", avg ").append(getAverageCompTime()).append("ms");
		}
		return sb.toString();
	}

	/** Returns the ModelGroup. */
	public ModelGroup getModelGroup() {
		return modelGroup;
	}

	/** Start the Simulation. */
	public void start() {
		if (getStatus() == STATUS_READY)
			modelTrigger.startSimulation();
	}

	/** Stop the Simulation. */
	public void stop() {
		if (getStatus() == STATUS_RUNNING)
			modelTrigger.stopSimulation();
	}



	/**
	 * Provides triggering the next step in steps of fixed duration.
	 */
	private class TriggerThread extends Thread
		implements ModelTrigger
	{
		private boolean stop = true;
		private int dt;
		private Thread oneShot;

		TriggerThread(int dt) {
			this.dt = dt;
		}

		public void run() {
			while(!stop) {
				oneShot = new Thread(new OneShot());
				oneShot.start();

				compute(dt);

				if (oneShot != null) {
					try{
						oneShot.join();
					} catch(InterruptedException e) {
						System.err.println(e);
					}
				}
			}
		}

		public void startSimulation() {
			stop = false;
			start();
		}

		public void stopSimulation() {
			stop = true;
		}

		public boolean isRunning() {
			return isAlive();
		}

		private class OneShot implements Runnable {
			public void run() {
				try{
					Thread.sleep(dt);
				} catch(InterruptedException e) {
					System.err.println(e);
				}
			}
		}
	}


	/**
	 * Triggers computing the next step after each frame
	 * was successfully rendered by the 3D engine.
	 */
	private class TriggerBehavior extends WorldBehavior
		implements ModelTrigger
	{
		private long time = System.currentTimeMillis();

		TriggerBehavior(){
			super(new WakeupOnElapsedFrames(0));
			setEnable(false);
		}

		/** Invokes recomputing the model. */
		public void processStimulus(java.util.Enumeration criteria) {
			long start = System.currentTimeMillis();
			long dt = (start - time);
			if (dt < dtMin) dt = dtMin;
			if (dt > dtMax) dt = dtMax;
			if (compute(dt)) {
				time = start;
			}

			wakeupOn(w);
		}

		public void startSimulation() {
			setEnable(true);
		}

		public void stopSimulation() {
			setEnable(false);
		}

		public boolean isRunning() {
			return getEnable();
		}
	}
}
