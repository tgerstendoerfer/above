package ch.fha.ia02.above;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

/**
 * Abstract base class for something that creates/shoots projectiles.
 * Speaking in patterns, this classs could be called
 * <tt>AbstractProjectileFactory</tt>.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class Cannon implements Cloneable {

	/** The minimum time to wait between two shots, in seconds. */
	protected float speed;

	/** Specifies the amount of damage a full hit causes. */
	protected float damage;

	/** Number of seconds before a projectile expires after being fired. */
	protected float ttl;

	/** Velocity of a projectile fired by this cannon. */
	protected float projectileVelocity;

	/** Default material to be used for [simple] projectiles. */
	public static final Material DEFAULT_PROJECTILE_MATERIAL
		= createProjectileMaterial(new Color3f(0,1,0));

	/** Projectile material. Used for simple projectiles. */
	protected Material projectileMaterial = DEFAULT_PROJECTILE_MATERIAL;

	/** Projectile size. */
	protected float projectileSize = 8.0f;


	/**
	 * Creates a new Cannon with the specified characteristics.
	 *
	 * @param speed minimum time in seconds between shots.
	 * @param projectileVelocity velocity of a projectile fired by this cannon.
	 * @param damage the amount of damage a full hit may cause.
	 * @param ttl number of seconds before a projectile expires.
	 *
	 * @throws IllegalArgumentException if either speed or damage
	 *        are negative.
	 */
	public Cannon(float speed, float projectileVelocity, float damage, float ttl) {
		if (speed < 0) throw new IllegalArgumentException("Speed cannot be negative.");
		if (projectileVelocity <= 0) throw new IllegalArgumentException("Projectile velocity must be > 0.");
		if (damage <= 0) throw new IllegalArgumentException("Damage must be > 0.");
		if (ttl <= 0) throw new IllegalArgumentException("TTL must be > 0");
		this.speed = speed;
		this.projectileVelocity = projectileVelocity;
		this.damage = damage;
		this.ttl = ttl;
	}

	/** Returns a string representation of this cannon. */
	public String toString() {
		return "speed=" + speed + "s, damage=" + damage + ", ttl=" + ttl + "s";
	}

	/**
	 * Returns a Node object that represents the 3D-shape for projectiles
	 * that are fired by this cannon.
	 * This implementation alwayas returns <tt>null</tt>.
	 * @return a node representing the 3D-shape,
	 *         or <tt>null</tt> if no shape is available.
	 */
	public Node getProjectileShape() {
		return null;
	}


	/** Returns the minimum time to wait between two shots. */
	public float getSpeed() {
		return speed;
	}

	/** Returns the amount of damage a full hit causes. */
	public float getDamage() {
		return damage;
	}

	/**
	 * Sets the amount of damage a full hit causes.
	 * @param damage the new amount
	 */
	public void setDamage(float damage) {
		if (damage < 0) throw new IllegalArgumentException("Damage must be positive.");
		this.damage = damage;
	}

	/**
	 * Sets the material to be used in simple projectiles.
	 * @param color color of the projectile.
	 */
	public void setProjectileMaterial(Color3f color) {
		projectileMaterial = createProjectileMaterial(color);
	}
	/** Creates a material from a color. */
	private static Material createProjectileMaterial(Color3f color) {
		return new Material(color, color, color, color, 128);
	}


	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @return <tt>true</tt> if <var>o</var> is a cannon with
	 *         identical parameters to this one.
	 */
	public boolean equals(Object o) {
		return o instanceof Cannon && equals((Cannon)o);
	}

	/**
	 * Indicates whether some other cannon is equal to this one.
	 * @return <tt>true</tt> if <var>o</var> has identical parameters
	 *         to this one.
	 */
	public boolean equals(Cannon o) {
		return this == o || (
			o != null &&
			speed == o.speed &&
			damage == o.damage &&
			ttl == o.ttl &&
			projectileVelocity == o.projectileVelocity &&
			projectileSize == o.projectileSize &&
			projectileMaterial.equals(o.projectileMaterial));
	}

	/** Returns a hash code for this cannon. */
	public int hashCode() {
		int h = (int)speed;
		h = h*17 + (int)damage;
		h = h*17 + (int)ttl;
		h = h*17 + (int)projectileVelocity;
		h = h*17 + (int)projectileSize;
		h = h*17 + projectileMaterial.hashCode();
		return h;
	}


	/**
	 * Creates and returns a copy of this cannon.
	 * @return a clone of this instance.
	 * @throws a <tt>RuntimeException</tt> if cloning failed.
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates and 'fires' a projectile.
	 *
	 * @param position initial position for the projectile.
	 * @param direction direction the projectile is fired.
	 */
	public Projectile fire(Vector3f position, Vector3f direction) {
		return fire(position, direction, null);
	}

	/**
	 * Creates and 'fires' a projectile at the specified target.
	 *
	 * @param position initial position for the projectile.
	 * @param direction direction the projectile is fired.
	 * @param target the agent to shoot at.
	 */
	public Projectile fire(Vector3f position, Vector3f direction, Agent target) {
		Projectile p = new Projectile(position, direction, target);
		ViewerFrame vf = Application.getMainFrame();
		if (vf != null) vf.universe.addToCurrentLocale(p);
		return p;
	}


	/**
	 * Represents a projectile in the 3D world.
	 *
	 * @author Thomas Gerstendoerfer
	 * @author Michael Muehlebach
	 */
	public class Projectile extends BranchGroup {

		/** Starting position of this projectile. */
		public Vector3f p = new Vector3f();

		/** Velocity of this projectile (direction and absolute value). */
		public Vector3f v = new Vector3f();

		/** Agent, this projectile was aimed at. */
		public Agent target = null;

		/** Transformation Group for the Projectile. */
		private TransformGroup tg = new TransformGroup();

		/** Update the position and orientation of the projectile. */
		protected void update() {
			Transform3D t = new Transform3D();
			double a, c;
			Matrix3f mr = new Matrix3f();
			Matrix3f m1 = new Matrix3f();
			Matrix3f m2 = new Matrix3f();

			// Calculation for rotation round the X-axis
			c = v.length();
			a = (c != 0) ? -Math.PI/2+Math.asin(Math.abs(v.y)/c) : 0;
			if (v.y < 0) //check if the nose looks up or down
				a = Math.PI - a;
			m1.rotX((float)a);

			// Calculation for the rotation around the Y-axis
			c = Math.sqrt(Math.abs(v.z*v.z) + Math.abs(v.x*v.x));
			a = (c != 0) ? -Math.asin(Math.abs(v.x)/c) : 0;
			if (v.z > 0) //check if the orientation goes not into the std dir (-z)
				a = Math.PI - a;
			if (v.x < 0) //check if the rotation should go clockwise
				a = -a;
			m2.rotY((float)a);

			// stick the two calculations together
			mr.setIdentity();
			mr.mul(m2);
			mr.mul(m1);
			t.set(mr, p, 1);
			tg.setTransform(t);
		}

		/**
		 * Creates a new projectile with the specified parameters.
		 *
		 * @param position initial position of this projectile.
		 * @param direction direction the projectile is fired into.
		 */
		protected Projectile(Vector3f position, Vector3f direction, Agent target) {
			p.set(position);
			v.normalize(direction);
			v.scale(projectileVelocity);
			this.target = target;

			setCapability(BranchGroup.ALLOW_DETACH);

			Node shape = null;
			if (Application.getSettings().detailedShapes()) {
				// FIXME: load textured shape using ShapeCache
			}
			if (shape == null) {
				Appearance ap = new Appearance();
				ap.setMaterial(projectileMaterial);
				ap.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.3f));
				shape = new Box(projectileSize/8, projectileSize, projectileSize/8, ap);
			}

			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			update();
			tg.addChild(shape);

			// Add behavior that removes this projectile from the
			// scene graph as soon as the projectile expires.
			tg.addChild(new WorldBehavior(new WakeupOnElapsedFrames(0)) {
				private long start = System.currentTimeMillis();
				private Vector3f step = new Vector3f();
				public void processStimulus(java.util.Enumeration criteria) {
					long dt = System.currentTimeMillis() - start;
					if (dt >= ttl*1000) {
						detach();
						return;
					}

					// Set projectile position and orientation
					step.set(v);
					step.scale(dt/1000f);
					p.add(step);

					update();

					wakeupOn(w);
				}
			});

			// This is after the addition of the behavior because the calculation must
			// be at least once be done befor the projectile can be shown
			addChild(tg);
		}
	}
}
