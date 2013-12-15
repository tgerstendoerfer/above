package ch.fha.ia02.above;

import javax.vecmath.*;

/**
 * Template for the steering behavior of capital ships in the model.
 * <p>
 * Currently a captital ship behaves rather simplistic:
 * it just continues on its selected course.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class CapitalShip extends Agent {

	// create a couple of vectors to reduce the number
	// of heap operations when calculating each step
	private Vector3f step = new Vector3f();


	/**
	 * Creates a new capital ship with specified initial position and velocity.
	 *
	 * @param stats describes this agen't performace characteristics.
	 * @param position this object's initial position in 3D-space.
	 * @param velocity this object's initial velocity vector, also
	 *        used to compute this object's heading/orientation in space.
	 */
	public CapitalShip(AgentStats stats, Vector3f position, Vector3f velocity) {
		super(stats, position, velocity);
	}

	/**
	 * Computes the position and orientation of this object after
	 * an elapsed time period of <var>dt</var> seconds.
	 *
	 * @param dt time in seconds since the last step.
	 * @param agents array of all agents in the model.
	 */
	public void compute(float dt, Agent[] agents) {
		step.set(velocity);
		step.scale(dt);
		position.add(step);
	}
}
