package ch.fha.ia02.above;

import javax.vecmath.*;

/**
 * Describes the performace characteristics of a class of agents.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 *
 * @see Agent
 * @see Vessel
 */
public class AgentStats implements Cloneable {

	/**
	 * Absolute lower limit of an object's velocity.
	 * Velocity must never be zero because it is also used to determine
	 * the orientation of an object.
	 * To actually implement objects that do not move at all,
	 * just use the default <tt>Agent</tt> behavior.
	 */
	public static final float MIN_VMIN = 0.01f;

	/** Absolute lower limit of an agent's mass. */
	public static final float MIN_MASS = 1;


	/** The agent's length, in meters. */
	protected float length;

	/**
	 * Radius of the bounding sphere used for collision avoidance.
	 * Typically <tt>length/2</tt>.
	 */
	protected float bounds;

	/** The agent's mass, in kilograms. */
	protected float mass;

	/**
	 * Lower limit for an agent's velocity, in m/s.
	 * This value can only be zero if {@link #vmax} is zero as well,
	 * and in such cases denotes an agent that is unable to move.
	 * Otherwise it must be greater or equal to {@link #MIN_VMIN}.
	 */
	protected float vmin;

	/**
	 * Upper limit for an agent's velocity, in m/s.
	 * This value must be greater or equal to {@link #vmin}.
	 */
	protected float vmax;

	/**
	 * Specifies how much damage an agent can take before it dies.
	 *
	 * Typically, as long as you don't have more advanced models
	 * with mutiple shields covering different areas, you'll just
	 * just want to factor-in eventually existing shields.
	 */
	protected float health;


	/**
	 * Creates a new performance characteristics descriptor
	 * for immovable agents.
	 * <p>
	 *
	 * @param length the agent's length, in meters.
	 */
	public AgentStats(float length) {
		this(length, length*5, 0, 0);
	}


	/**
	 * Creates a new performace characteristic descriptor.
	 *
	 * @param length the agent's length, in meters.
	 * @param mass the agent's mass, in kilograms.
	 * @param vmin lower limit for an agent's velocity.
	 * @param vmax upper limit for an agent's velocity.
	 */
	public AgentStats(float length, float mass, float vmin, float vmax) {
		this.length = length;
		this.mass = mass;
		this.vmin = vmin;
		this.vmax = vmax;
		this.bounds = length/2;
		this.health = mass; // approximation: 1kg -> 1 HP
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
		if (isMoveable()) {
			if (vmax <= 0) throw new IllegalArgumentException("Max. velocity must be positive!");
			if (vmin < MIN_VMIN) { vmin = MIN_VMIN; System.err.println("Set vmin to " + vmin); }
			if (vmin > vmax) throw new IllegalArgumentException("vmax must be greater than vmin!");
		}
		if (mass < MIN_MASS) throw new IllegalArgumentException("Object mass must at least 1 kg!");
		if (length <= 0) throw new IllegalArgumentException("Length must be a positive number!");
		if (health <= 0) throw new IllegalArgumentException("Health must be a positive number!");
	}


	/**
	 * Tests if agents of this kind are able to move.
	 * Immovable objects allow for much simpler processing.
	 *
	 * @return <tt>true</tt> if agents of this kind can move around,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean isMoveable() {
		return vmin != 0 && vmax != 0;
	}


	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @return <tt>true</tt> if <var>o</var> has identical stats to this one.
	 */
	public boolean equals(Object o) {
		return o instanceof AgentStats && equals((AgentStats)o);
	}

	/**
	 * Indicates whether some other stats object is "equal to" this one.
	 * @return <tt>true</tt> if <var>o</var> has identical stats to this one.
	 */
	public boolean equals(AgentStats o) {
		return this == o || (
			o != null &&
			length == o.length &&
			mass == o.mass &&
			vmin == o.vmin &&
			vmax == o.vmax &&
			bounds == o.bounds &&
			health == o.health);
	}

	/** Returns a hash code value for the object. */
	public int hashCode() {
		int h = (int)length;
		h = h*17 + (int)mass;
		h = h*17 + (int)vmin;
		h = h*17 + (int)vmax;
		h = h*17 + (int)bounds;
		h = h*17 + (int)health;
		return h;
	}


	/** Returns a string representation of this object's settings. */
	public String toString() {
			return "length=" + length
			+ ", mass=" + mass
			+ ", vmin=" + vmin
			+ ", vmax=" + vmax;
	}

	/**
	 * Creates and returns a copy of this agent characteristics descriptor.
	 * @return a clone of this instance.
	 * @throws a <tt>RuntimeException</tt> with a
	 *         <tt>CloneNotSupportedException</tt> cause if this instance
	 *         could not be cloned.
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
