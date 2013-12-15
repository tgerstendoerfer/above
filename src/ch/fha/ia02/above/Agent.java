package ch.fha.ia02.above;

import javax.vecmath.*;

/**
 * A single agent that does not move at all.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 *
 * @see ViewObject
 */
public class Agent {

	/** A name/title for this agent. */
	protected String name = "";

	/** This agent's position in 3D-space. */
	protected Vector3f position;

	/** This agent's current velocity and orientation. */
	protected Vector3f velocity;

	/** This agent's current orientation and is initialised in y-direction. */
	protected Vector3f up;

	/** Describes this object's performance characteristics. */
	protected AgentStats stats;


	/**
	 * This agent's collision state.
	 * <tt>true</tt> after this object collided with another one.
	 */
	protected boolean collided = false;

	/**
	 * Specifies how much damage this agent can take before it dies.
	 * A negative value means this agent is already dead and should
	 * be removed from the model if appropriate (e.g. if it can explode).
	 */
	protected float health = 1;


	/**
	 * Specifies the faction this agent belongs to, default is neutral.
	 *
	 * This information is used to determine if an object should be
	 * considered neutral, friendly or hostile.
	 */
	protected Faction faction = Faction.NEUTRAL;

	/**
	 * Specifies the group this agent belongs to.
	 * Two agent with identical group code belong to the same squadron/group.
	 */
	protected int group;


	/**
	 * Creates a new agent with the specified position and velocity.
	 * The position and velocity vectors are copied, and can safely reused
	 * for the creation of multiple objects.
	 * <p>
	 * This constructor is particularly suited for the construction of
	 * essentially static agents, that are in the model purely for collision
	 * avoidance reasons.
	 *
	 * @param position this object's initial position in 3D-space.
	 * @param velocity this object's initial velocity vector, also
	 *         used to compute this object's heading/orientation in space.
	 */
	protected Agent(Vector3f position, Vector3f velocity) {
		this(new AgentStats(1, 1, AgentStats.MIN_VMIN, velocity.length()), position, velocity);
	}

	/**
	 * Creates a new agent with the specified position and velocity.
	 * The position and velocity vectors are copied, and can safely reused
	 * for the creation of multiple objects.
	 * <p>
	 * If the specified velocity is outside the range limited by
	 * <tt>vmin</tt> and <tt>vmax</tt>, it will be scaled to fit.
	 *
	 * @param stats describes this agen't performace characteristics.
	 * @param position this object's initial position in 3D-space.
	 * @param velocity this object's initial velocity vector, also
	 *         used to compute this object's heading/orientation in space.
	 *
	 * @throws IllegalArgumentException if at least one argument is either
	 *         <tt>null</tt>, or {@link AgentStats#validate()} failed.
	 */
	protected Agent(AgentStats stats, Vector3f position, Vector3f velocity) {
		if (position == null) throw new IllegalArgumentException("Position must be set!");
		if (velocity == null) throw new IllegalArgumentException("Velocity must be set!");
		if (stats == null) throw new IllegalArgumentException("AgentStats must be set!");

		this.up = new Vector3f(0,1,0); // FIXME: should be orthogonal to velocity
		this.position = new Vector3f(position);
		this.velocity = new Vector3f(velocity);
		this.stats = (AgentStats)stats.clone();
		health = stats.health;
		stats.validate();
		if (limitVelocity()) System.err.println("Velocity had to be adjusted!");
	}


	/**
	 * Performs various consistency checks on this object.
	 * <p>
	 * Beside just checking the values for cosistency, this
	 * method also ensures that the velocity is in the range
	 * bounded by <tt>vMin</tt> and <tt>vMax</tt>.
	 *
	 * @throws IllegalArgumentException on the first check that fails.
	 */
	public void validate() {
		if (position == null) throw new IllegalArgumentException("Position must be set!");
		if (velocity == null) throw new IllegalArgumentException("Velocity must be set!");
		float upangle = velocity.angle(up);
		if (upangle < 0.01f || upangle > 3.135f) throw new IllegalArgumentException("Up must not point into the same direction as velocity.");
		if (limitVelocity()) System.err.println("Velocity had to be adjusted!");
	}

	/**
	 * Ensures that the specified velocity is within the range
	 * bounded by <em>vmin</em> and <em>vmax</em>.
	 *
	 * @return <tt>true</tt> if the velocity had to be adjusted
	 *         to fit in the range, <tt>false</tt> otherwise.
	 *
	 * @throws IllegalArgumentException if the velocity has zero length.
	 */
	protected boolean limitVelocity() {
		float vl = velocity.length();
		if (vl == 0) throw new IllegalArgumentException("Velocity (vector) must not be zero!");
		if (!stats.isMoveable()) return false;
		if (vl > stats.vmax) {
			velocity.scale(stats.vmax/vl);
			return true;
		}
		if (vl < stats.vmin) {
			velocity.scale(stats.vmin/vl);
			return true;
		}
		return false;
	}


	/**
	 * Returns this agent's position vector.
	 * The returned vector object must not be modified.
	 *
	 * @return the {#position} field.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Specifies this agent's <em>forward direction</em> in 3D-space.
	 * The vector's length is an artifact of the calculation and must not
	 * be used by the caller.
	 * <p>
	 * The returned vector object must not be modified.
	 *
	 * @return the {#velocity} field.
	 */
	public Vector3f getOrientation() {
		return velocity;
	}

	/**
	 * Returns a vector specifying this agent's <em>up</em>
	 * direction in 3D-space.
	 * The returned vector must not be modified.
	 *
	 * @return the {#up} field.
	 */
	public Vector3f getUp() {
		return up;
	}


	/**
	 * Called when a collision (or near-collision) with another
	 * agent is detected.
	 * Sets the {@link #collided} flag to <tt>true</tt>.
	 *
	 * @param o the agent colliding with this one.
	 */
	public void collision(Agent o) {
		collided = true;
	}


	/**
	 * Called by an attacker if this agent gets hit.
	 * If the dama causes this agent to reach negative health,
	 * it dies and its faction is set to <tt>NEUTRAL</tt> to
	 * preempt further participation in the battle.
	 *
	 * @param damage the amount of damage taken by this agent.
	 * @return <code>true</code> if this agent died now.
	 */
	public boolean hit(float damage) {
		if (damage < 0) {
			System.err.println("Ignored negative damage!");
			return false;
		}
		boolean died = health > 0 && damage >= health;
		health -= damage;
		if (died) {
			System.out.println(name + " has died.");
			faction = Faction.NEUTRAL;
			health = 0;
		}
		return died;
	}


	/**
	 * Computes position and orientation of this object after
	 * an elapsed time period of <var>dt</var> seconds.
	 * <p>
	 * This implementation does nothing at all.
	 *
	 * @param dt time in seconds since the last step.
	 * @param agents array of all agents in the model.
	 */
	public void compute(float dt, Agent[] agents) {}


	/** Returns a string representation of this agent. */
	public String toString() {
		return getClass().getName()
			+ "[" + (int)velocity.length() + "m/s" + ", pos=" + intString(position) + "]";
	}

	private String intString(Vector3f v) {
		return "(" + (int)v.x + ", " + (int)v.y + ", " + (int)v.z + ")";
	}


	/**
	 * Returns information about this agent's current state.
	 * For example, a starfighter might be reporting that it
	 * is attacking fighter <var>XY</var>.
	 * @return information about this agent's current state.
	 */
	public String statusText() {
		if (health <= 0) {
			return "dead";
		} else {
			return "";
		}
	}
}
