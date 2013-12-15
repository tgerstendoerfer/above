package ch.fha.ia02.above;

import java.awt.Color;
import javax.vecmath.*;

/**
 * Represents an agent that behaves very much like a starfighter.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 * @author Lukas Kellenberger
 */
public class Starfighter extends Agent {

	// create a couple of vectors to reduce the number
	// of heap operations when calculating each step
	private static final Vector3f NULL_VECTOR = new Vector3f();
	private Vector3f force = new Vector3f();
	private Vector3f step = new Vector3f();
	private Vector3f heading = new Vector3f();
	private Vector3f d = new Vector3f();
	private Vector3f t = new Vector3f();
	private Vector3f separationForce = new Vector3f();
	private Vector3f flockCenter = new Vector3f();
	private Vector3f flockVelocity = new Vector3f();
	private Vector3f avoid = new Vector3f();
	private Vector3f enemySeekDirection = new Vector3f();

	// status flags
	private Agent target = null;
	private int nEnemiesOnRadar = 0;
	private float waitBeforeNextShot = 0;
	private float projectileHitIn = 0;
	private Cannon.Projectile projectileFired = null;
	private Stats stats; // hack: shadow field of parent type

	/** The minimum distance that still yields useful result values. */
	private static final float MIN_CALC_DIST = 1f;


	/**
	 * Creates a new starfighter with specified initial position and velocity.
	 *
	 * @param stats describes this agen't performace characteristics.
	 * @param position this object's initial position in 3D-space.
	 * @param velocity this object's initial velocity vector, also
	 *        used to compute this object's heading/orientation in space.
	 */
	public Starfighter(Stats stats, Vector3f position, Vector3f velocity) {
		super(stats, position, velocity);
		this.stats = (Stats)super.stats; // hack but working
	}

	/**
	 * Computes the position and orientation of this object after
	 * an elapsed time period of <var>dt</var> seconds.
	 * <p>
	 * Currently, this results in some kind of flocking behavior
	 * of all fighters that belong to the same {@link #group}.
	 *
	 * @param dt time in seconds since the last step.
	 * @param agents array of all agents in the model.
	 */
	public void compute(float dt, Agent[] agents) {

		// initialize values
		float vl = velocity.length();
		heading.normalize(velocity); // heading = normalized velocity
		force.set(NULL_VECTOR);
		separationForce.set(NULL_VECTOR);
		flockCenter.set(position);
		flockVelocity.set(velocity);
		enemySeekDirection.set(NULL_VECTOR);
		nEnemiesOnRadar = 0;

		float nCohesive = 1;
		float avoidThres = (vl + stats.length)*stats.avoidDistFactor; // only consider closer objects
		float avoidDist = avoidThres;
		float avoidRho = avoidThres*avoidThres / stats.avoidAngle;
		float avoidDanger = Float.MAX_VALUE;
		float targetRating = stats.attackRange + stats.attackAngle*stats.attackFrontalPriority;
		for (int i=0; i<agents.length; i++) {
			Agent o = agents[i];
			if (o != this) {
				d.sub(o.position, position);
				float bothbounds = stats.bounds + o.stats.bounds;
				float dist = d.length() - bothbounds;
				if (dist < MIN_CALC_DIST) dist = MIN_CALC_DIST;
				float angle = d.angle(velocity);
				boolean enemy = faction.isEnemy(o.faction);
				if (o.group == group && o.health > 0) { // same group -> flockmates
					if (dist < stats.separationDistance && angle < stats.separationAngle) {
						t.scale((-1)/(dist*dist*dist), d);
						separationForce.add(t);
					}
					if (dist < stats.cohesionDistance && angle < stats.cohesionAngle) {
						nCohesive++;
						flockCenter.add(o.position);
					}
					if (dist < stats.alignmentDistance && angle < stats.alignmentAngle) {
						flockVelocity.add(o.velocity);
					}
				} else if (dist < avoidDist && angle < stats.avoidAngle) {
					// collision avoidance
					float psi = angle - bothbounds/(dist + bothbounds);
					if (psi < stats.avoidAngle2) {
						avoidDist = dist;
						avoidDanger = psi*psi + dist * dist / avoidRho + 1;
						avoid.set(d);
					}
				} else if (target == null && enemy) {
					// try to select a new target to attack
					if (dist < stats.attackRange && angle < stats.attackAngle) {
						float r = dist + angle * stats.attackFrontalPriority;
						if (r < targetRating) {
							targetRating = r;
							target = o;
						}
					}
					// or at least locate the enemy somewhere
					if (target == null && dist < stats.radarRange && angle < stats.radarAngle) {
						// compute average position of enemies in
						// range, weighted by distance and angle.
						t.set(o.velocity);
						t.scale(dist/stats.radarRange);
						enemySeekDirection.add(t);
						enemySeekDirection.add(o.position);						
						nEnemiesOnRadar++;
					}
				}
			}
		}


		// attack target
		waitBeforeNextShot -= dt;
		projectileHitIn -= dt;
		if (target != null) {
			t.sub(target.position, position);
			float dist = t.length() - (stats.bounds + target.stats.bounds)-1;
			float angle = t.angle(velocity);
			if (dist > stats.attackRange || angle > stats.attackAngle || target.health <= 0) {
				target = null;
			} else {
				// 1.) fly into the direction of the target
				t.normalize();
				t.scale(stats.attackWeight);
				force.set(t);

				// 2.) align with target
				t.normalize(target.velocity);
				t.scale(stats.attackAlignmentWeight);
				force.add(t);

				// 3.) shoot
				if (angle < stats.fireAngle && waitBeforeNextShot <= 0) {
					projectileFired = stats.cannon.fire(position, velocity, target);
					projectileHitIn = dist/stats.cannon.projectileVelocity;
					waitBeforeNextShot = Math.max(stats.cannon.getSpeed(), projectileHitIn);
				}
			}
		}
		// test if projectile hit
		if (projectileFired != null && projectileHitIn <= 0) {
			if (projectileFired.target != null) {
				projectileFired.target.hit(stats.cannon.getDamage());
			}
			projectileFired = null;
		}

		if (target == null) { // else won't work, we might've lost target

			// flock cohesion
			if (nCohesive > 1) {
				flockCenter.scale(1.0f/nCohesive);
				t.sub(flockCenter, position);
				t.normalize();
				t.scale(stats.cohesionWeight);
				force.add(t);
			}

			// flock alignment
			flockVelocity.normalize(); // only interested in the direction
			t.sub(flockVelocity, heading);
			t.scale(stats.alignmentWeight);
			force.add(t);
		}

		// flock separation (aligned collision avoidance)
		separationForce.scale(stats.separationWeight);
		force.add(separationForce);

		// collision avoidance (unaligned)
		if (avoidDist < avoidThres) {
			avoid.normalize();
			avoid.sub(heading, avoid);
			avoid.normalize(); // determine direction in which to move
			avoid.scale(stats.avoidWeight/avoidDanger);
			//force.scale(0.7f); // give collision avoidance priority
			force.add(avoid);
		} else if (nEnemiesOnRadar > 0 && target == null) {
			// look for enemy fighters
			enemySeekDirection.scale(1.0f/nEnemiesOnRadar);
			enemySeekDirection.sub(position);
			enemySeekDirection.normalize();
			enemySeekDirection.scale(stats.enemySeekWeight);
			force.add(enemySeekDirection);
		}

		// simulate inertia and obey min/max range
		force.scale(stats.agility); // FIXME: simulate inertia
		if (health > 0) velocity.add(force); // only do something if alive...
		vl = velocity.length();
		if (vl > stats.vmax) velocity.scale(stats.vmax/vl);
		else if (vl < stats.vmin) velocity.scale(stats.vmin/vl);

		// compute the direction of the up vector
		if (!(force.x == 0 && force.y == 0 && force.z == 0)) {
			force.normalize();
			t.normalize(up);
			t.sub(t, force);
			t.scale(stats.rollAgility);
			up.add(t);
		}

		// enable the following line to produce debug output
		//if (this == agents[0]) System.out.println(this);

		step.set(velocity);
		step.scale(dt);
		position.add(step);
	}


	/**
	 * Returns information about this fighter's current state.
	 * For example, a starfighter might be reporting that it
	 * is attacking fighter <var>XY</var>.
	 */
	public String statusText() {
		if (health <= 0) {
			return "dead";
		} else if (target != null) {
			return "attacking " + target.name;
		} else if (nEnemiesOnRadar > 0) {
			return "pursuit";
		} else {
			return super.statusText();
		}
	}


	/**
	 * Performance descriptor for starfighter.
	 */
	public static class Stats extends AgentStats {

		/** A general rating on how agile a starfighter is. */
		protected float agility = 2f;

		/** Specifies a starfighter's ability to roll. */
		protected float rollAgility = 0.6f;

		/**
		 * Threshold value to sort out objects we don't have to
		 * consider for collision avoidance.
		 * Should be around PI/2, see {@link #avoidAngle2}.
		 */
		protected float avoidAngle = 1.5f;

		/**
		 * Threshold value that defines a cone ahead of the agent that
		 * should be empty space or the agent will start collision avoidance.
		 * Called <var>ps<sub>thres</sub></var> in the documentation.
		 */
		protected float avoidAngle2 = 0.8f;

		/** Specifies the importance of collision avoidance. */
		protected float avoidWeight = 0.5f;

		/**
		 * This factor is used to compute how much ahead (depending
		 * the velocity) an agent should look for collision avoidance.
		 */
		protected float avoidDistFactor = 1;

		/**
		 * Bounds the environment in which flockmates
		 * are considered for separation.
		 */
		protected float separationDistance = 40;

		/**
		 * Bounds the environment in which flockmates
		 * are considered for cohesion.
		 */
		protected float cohesionDistance = 150;

		/**
		 * Bounds the environment in which flockmates
		 * are considered for alignment.
		 */
		protected float alignmentDistance = 180;

		/**
		 * Delimits the environment in which flockmates
		 * are considered for separation.
		 */
		protected float separationAngle = 3;

		/**
		 * Delimits the environment in which flockmates
		 * are considered for cohesion.
		 */
		protected float cohesionAngle = (float)(Math.PI*2/3);

		/**
		 * Delimits the environment in which flockmates
		 * are considered for alignment.
		 */
		protected float alignmentAngle = (float)(Math.PI*5/8);

		/** Weight for the separation component of flocking. */
		protected float separationWeight = 0.2f;

		/** Weight for the cohesion component of flocking. */
		protected float cohesionWeight = 0.2f;

		/** Weight for the alignment component of flocking. */
		protected float alignmentWeight = 0.2f;

		/** Delimits the range to detect enemies. */
		protected float radarRange = 1800;

		/** Delimits the area where enemies can be seen. */
		protected float radarAngle = 2.8f;

		/** Specifies how strong the desire to find and attack enemies is. */
		protected float enemySeekWeight = 4f;

		/** Limits the environment in which an enemy is actively attacked. */
		protected float attackRange = 300;

		/** Limits the environment in which an enemy is actively attacked. */
		protected float attackAngle = 0.5f;

		/**
		 * Weight for the attacking behavior.
		 * This weight controls the desire of flying directly into the
		 * current direction of the enemy, necessary to shoot him.
		 */
		protected float attackWeight = 0.1f;

		/**
		 * Weight for the alignment component in the attacking behavior.
		 * Alignment lets the fighter fly into the direction his target does,
		 * thereby increasing the possibility of hitting the target later on.
		 */
		protected float attackAlignmentWeight = 0.3f;

		/**
		 * Specifies how much the starfighter favors enemies
		 * directly in front of him over those along the sides.
		 */
		protected float attackFrontalPriority = 20;

		/** Delimits the area in which enemies can be shot down. */
		protected float fireAngle = 0.4f;


		/** The main cannon mounted to this fighter. */
		protected Cannon cannon = new Cannon(1, 300, 3400, 2);



		/**
		 * Creates a new starfighter performace characteristic descriptor.
		 *
		 * @param length the starfighter's length, in meters.
		 * @param mass the starfighter's mass, in kilograms.
		 * @param vmin lower limit for a starfighter's velocity.
		 * @param vmax upper limit for a starfighter's velocity.
		 * @param projectileColor color of the projectiles fired by this agent.
		 */
		public Stats(float length, float mass, float vmin, float vmax, Color projectileColor) {
			this(length, mass, vmin, vmax);
			cannon.setProjectileMaterial(new Color3f(projectileColor));
		}

		/**
		 * Creates a new starfighter performace characteristic descriptor.
		 *
		 * @param length the starfighter's length, in meters.
		 * @param mass the starfighter's mass, in kilograms.
		 * @param vmin lower limit for a starfighter's velocity.
		 * @param vmax upper limit for a starfighter's velocity.
		 */
		public Stats(float length, float mass, float vmin, float vmax) {
			super(length, mass, vmin, vmax);
		}

		/**
		 * Performs various consistency checks on this object.
		 * @throws IllegalArgumentException on the first check that fails.
		 */
		public void validate() {
			validateAngle(separationAngle, "separationAngle");
			validateAngle(cohesionAngle, "cohesionAngle");
			validateAngle(alignmentAngle, "alignmentAngle");
			validateAngle(radarAngle, "radarAngle");
			validateAngle(attackAngle, "attackAngle");
			if (attackAngle > radarAngle) throw new IllegalArgumentException("fireAngle must not be greater than radarAngle!");
			if (fireAngle > attackAngle) throw new IllegalArgumentException("attackAngle must not be greater than attackAngle!");
		}

		/**
		 * Ensures that the specified angle is between 0 and Pi.
		 * @param angle the angle, in radians
		 * @param name name of the value, used in the error message.
		 */
		private void validateAngle(float angle, String name) {
			validate(angle, 0, (float)Math.PI, name);
		}

		/**
		 * Ensures that value is within the specified range.
		 * An <tt>IllegalArgumentException</tt> is thrown if value is outside the range.
		 * @param value the value to test.
		 * @param min lower bound
		 * @param min upper bound
		 * @param name name of the value, used in the error message.
		 */
		private void validate(float value, float min, float max, String name) {
			if (min > max) throw new InternalError("illegal range: min must not be grater than max!");
			if (value < min || value > max) throw new IllegalArgumentException(
				name + " was " + value +  ", must be between " + min + " and " + max + "!");
		}

		/**
		 * Creates and returns a copy of this agent characteristics descriptor.
		 * @return a clone of this instance.
		 */
		public Object clone() {
			Stats stats = (Stats)super.clone();
			stats.cannon = (Cannon)cannon.clone();
			return stats;
		}

		/**
		 * Indicates whether some other object is "equal to" this one.
		 * @return <tt>true</tt> if <var>o</var> has identical stats to this one.
		 */
		public boolean equals(Object o) {
			if (o instanceof Stats) return equals((Stats)o);
			if (o instanceof AgentStats) return equals((AgentStats)o);
			return false;
		}

		/**
		 * Indicates whether some other stats object is "equal to" this one.
		 * @return <tt>true</tt> if <var>o</var> has identical stats to this one.
		 */
		public boolean equals(Stats o) {
			// ugly but required to make the cache work
			return super.equals(o) &&
				agility == o.agility &&
				rollAgility == o.rollAgility &&
				avoidAngle == o.avoidAngle &&
				avoidAngle2 == o.avoidAngle2 &&
				avoidWeight == o.avoidWeight &&
				avoidDistFactor == o.avoidDistFactor &&
				separationDistance == o.separationDistance &&
				cohesionDistance == o.cohesionDistance &&
				alignmentDistance == o.alignmentDistance &&
				separationAngle == o.separationAngle &&
				cohesionAngle == o.cohesionAngle &&
				alignmentAngle == o.alignmentAngle &&
				separationWeight == o.separationWeight &&
				cohesionWeight == o.cohesionWeight &&
				alignmentWeight == o.alignmentWeight &&
				radarRange == o.radarRange &&
				radarAngle == o.radarAngle &&
				enemySeekWeight == o.enemySeekWeight &&
				attackRange == o.attackRange &&
				attackAngle == o.attackAngle &&
				attackWeight == o.attackWeight &&
				attackAlignmentWeight == o.attackAlignmentWeight &&
				attackFrontalPriority == o.attackFrontalPriority &&
				fireAngle == o.fireAngle &&
				cannon.equals(o.cannon);
		}

		/** Returns a hash code value for the object. */
		public int hashCode() {
			int h = super.hashCode();
			h = h*17 + (int) agility * 1000;
			h = h*17 + (int) rollAgility * 1000;
			h = h*17 + (int) avoidAngle * 1000;
			h = h*17 + (int) avoidAngle2 * 1000;
			h = h*17 + (int) avoidWeight * 1000;
			h = h*17 + (int) avoidDistFactor * 1000;
			h = h*17 + (int) separationDistance * 1000;
			h = h*17 + (int) cohesionDistance * 1000;
			h = h*17 + (int) alignmentDistance * 1000;
			h = h*17 + (int) separationAngle * 1000;
			h = h*17 + (int) cohesionAngle * 1000;
			h = h*17 + (int) alignmentAngle * 1000;
			h = h*17 + (int) separationWeight * 1000;
			h = h*17 + (int) cohesionWeight * 1000;
			h = h*17 + (int) alignmentWeight * 1000;
			h = h*17 + (int) radarRange * 1000;
			h = h*17 + (int) radarAngle * 1000;
			h = h*17 + (int) enemySeekWeight * 1000;
			h = h*17 + (int) attackRange * 1000;
			h = h*17 + (int) attackAngle * 1000;
			h = h*17 + (int) attackWeight * 1000;
			h = h*17 + (int) attackAlignmentWeight * 1000;
			h = h*17 + (int) attackFrontalPriority * 1000;
			h = h*17 + (int) fireAngle * 1000;
			h = h*17 + cannon.hashCode();
			return h;
		}

		/** Returns a string representation of this object's settings. */
		public String toString() {
			// could/should this be avoided using reflection?
			return super.toString()
				+ ", agility=" + agility
				+ ", rollAgility=" + rollAgility
				+ ", avoidAngle=" + avoidAngle
				+ ", avoidAngle2=" + avoidAngle2
				+ ", avoidWeight=" + avoidWeight
				+ ", avoidDistFactor=" + avoidDistFactor
				+ ", separationDistance=" + separationDistance
				+ ", cohesionDistance=" + cohesionDistance
				+ ", alignmentDistance=" + alignmentDistance
				+ ", separationAngle=" + separationAngle
				+ ", cohesionAngle=" + cohesionAngle
				+ ", alignmentAngle=" + alignmentAngle
				+ ", separationWeight=" + separationWeight
				+ ", cohesionWeight=" + cohesionWeight
				+ ", alignmentWeight=" + alignmentWeight
				+ ", radarRange=" + radarRange
				+ ", radarAngle=" + radarAngle
				+ ", enemySeekWeight=" + enemySeekWeight
				+ ", attackRange=" + attackRange
				+ ", attackAngle=" + attackAngle
				+ ", attackWeight=" + attackWeight
				+ ", attackAlignmentWeight=" + attackAlignmentWeight
				+ ", attackFrontalPriority=" + attackFrontalPriority
				+ ", fireAngle=" + fireAngle
				+ ", cannon=" + cannon;
		}
	}
}
