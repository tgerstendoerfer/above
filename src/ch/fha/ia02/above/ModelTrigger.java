package ch.fha.ia02.above;

/**
 * Trigger interface for the Model.
 *
 * @author Michael Muehlebach
 */
public interface ModelTrigger{

	/** Start's the simulation. */
	public void startSimulation();

	/** Stop's the simulation. */
	public void stopSimulation();

	/** Return true if the simulation is running. */
	public boolean isRunning();
}
