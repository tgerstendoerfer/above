package ch.fha.ia02.above;

/**
 * Interface for factories that build up the simulation's internal model.
 *
 * @author Thomas Gerstendoerfer
 *
 * @see Model
 */
public interface ModelFactory
{
	/**
	 * Creates and initializes the <code>Model</code>.
	 * representing all elements in the system.
	 */
	public Model createModel();
}