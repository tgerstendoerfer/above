package ch.fha.ia02.above;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.objectfile.*;
import com.sun.j3d.loaders.*;
import com.sun.j3d.utils.geometry.*;


/**
 * Represent a 3d Object in the Universe.
 * This class is the 'glue' between an agent in the abstract
 * model and it's three-dimensional visual representation.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class ViewObject extends BranchGroup {
	// The position is in this object but the orientation is in a own TG
	private Agent agent;

	private TransformGroup tg = new TransformGroup();

	// The next references are only in the global scope for better performance
	private Transform3D t = new Transform3D();
	private Matrix3f m1 = new Matrix3f();
	private Matrix3f m2 = new Matrix3f();
	private Matrix3f m3 = new Matrix3f();
	private Matrix3f mr = new Matrix3f();
	private double c;
	private double a;
	private Vector3f vv = new Vector3f();

	private Vector3f vo, vp, vu;
	private Appearance app = new Appearance();
	private Node shape;
	private Vessel vessel;

	private static TransparencyAttributes boundsTransparency =
		new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.7f);

	/** Mark colliding objects if set to true. */
	private static boolean showCollisionColor =
		Application.getSettings().showCollisionColor();


	/**
	 * Creates a new ViewObject for the 3D representation.
	 *
	 * @param agent the <tt>Agent</tt> represented by this <tt>ViewObject</tt>.
	 * @param vessel specifies how this object should be drawn.
	 */
	public ViewObject(Agent agent, Vessel vessel) {
		super();
		this.addChild(tg);
		this.agent = agent;
		this.vessel = vessel;
		SharedGroup sg;
		if (Application.getSettings().detailedShapes() &&
			(sg = ShapeCache.getShape(vessel)) != null) {
			shape = new Link(sg);
		} else {
			SimpleShip simpleShip = new SimpleShip(vessel.getLength());
			Appearance simpleAppearance = new Appearance();
			Color3f lightcolor = new Color3f(agent.faction.getColor());
			lightcolor.scale(0.6f);
			Material m = new Material(
				agent.faction.getColor(),
				lightcolor,
				agent.faction.getColor(),
				lightcolor,
				128);
			m.setLightingEnable(true);
			simpleAppearance.setMaterial(m);
			simpleShip.setAppearance(simpleAppearance);
			simpleShip.setCollisionBounds(vessel.getCollisionBounds());

			shape = simpleShip;
		}

		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setCapability(Node.ENABLE_COLLISION_REPORTING);
		setCapability(BranchGroup.ALLOW_DETACH);

		tg.addChild(shape);

		if (Application.getSettings().showCollisionSpheres()) {
			app.setTransparencyAttributes(boundsTransparency);
			Material mat = new Material(
				agent.faction.getColor(),
				new Color3f(0, 0, 0),
				agent.faction.getColor(),
				new Color3f(0.5f, 0.5f, 0.5f),
				128);
			app.setMaterial(mat);
			Node n = new Sphere(agent.stats.length/2, app);
			n.setCollidable(false);
			tg.addChild(n);
		}

		vo = agent.getOrientation();
		vu = agent.getUp();
		vp = agent.getPosition();

		// Add the behavior for the ModelObject movement
		// if this is not an immovable object
		if (vessel.isAutoOriented()) {
			t.setTranslation(vp);
			tg.setTransform(t);
		} else {
			update();
		}
		if (vessel.getStats().isMoveable()) {
			tg.addChild(new Mover(this));
		}

		//setCollisionBounds(new BoundingSphere(new Point3d(), vessel.getLength()));
		//addChild(new CollisionDetector(this));
	}

	/**
	 * Sets the objects position, orientation and collisioncolor (in a
	 * further version also the "up" vector.
	 */
	public void update() {

		if (agent.health <= 0) {
			explode();
			return;
		}

		// Calucation for rotation
		c = vu.length();
		a = (c != 0) ? Math.acos(Math.abs(vu.y)/c) : 0;

		// checks if the up vector is left or right
		vv.cross(vu, vo);
		if (vv.y > 0){
			if (vu.y < 0)
				a = Math.PI - a;
		}
		else{
			if (vu.y < 0)
				a = a - Math.PI;
		}
		m1.rotY((float)a);


		// Calculation for "nose" height
		c = vo.length();
		a = (c != 0) ? -Math.PI/2+Math.asin(Math.abs(vo.y)/c) : 0;
		if (vo.y < 0) //check if the nose looks up or down
			a = Math.PI - a;
		m2.rotX((float)a);


		// Calculation for the direction around the Y-axis
		c = Math.sqrt(Math.abs(vo.z*vo.z) + Math.abs(vo.x*vo.x));
		a = (c != 0) ? -Math.asin(Math.abs(vo.x)/c) : 0;
		if (vo.z > 0) //check if the orientation goes not into the std dir (-z)
			a = Math.PI - a;
		if (vo.x < 0) //check if the rotation should go clockwise
			a = -a;
		m3.rotY((float)a);


		// stick the three calculations togeather
		mr.setIdentity();
		mr.mul(m3);
		mr.mul(m2);
		mr.mul(m1);
		t.set(mr, vp, 1);
		tg.setTransform(t);
	}

	/** Returns his ModelObject root node */
	public Node getNode() {
		return this;
	}

	/** Returns the Agent */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * Detaches this object from the scene graph and
	 * invokes the explosion animation.
	 */
	public void explode() {
		this.detach();
		Explosion e = new Explosion(vp, vessel.getLength());
		Application.getMainFrame().universe.addToCurrentLocale(e);
	}

	/* Preload the explosion textures during static initialization. */
	{ Explosion.preload(); }


	/**
	 * Nested Class for the Collision Behavior.
	 */
	static class CollisionDetector extends WorldBehavior {
		ViewObject objectTransform;

		// After the Wakeup Criteria is satisfied this methode will be called
		public void processStimulus(java.util.Enumeration criteria) {
			ViewObject voCol = (ViewObject)((WakeupOnCollisionEntry)criteria.nextElement()).getTriggeringPath().getNode(0);
			objectTransform.agent.collision(voCol.getAgent());
			wakeupOn(w);
		}

		public CollisionDetector(ViewObject tg) {
			// Set the WakeupCriterion
			super(new WakeupOnCollisionEntry(tg));
			objectTransform = tg;
		}
	}


	/**
	 * Nested Class for the Move Behavior: it just update the position and orientation
	 * after another behavior posted the id 1
	 */
	static class Mover extends WorldBehavior{
		ViewObject viewObject;

		// After the Wakeup Criteria is satisfied this methode will be called
		public void processStimulus(java.util.Enumeration criteria) {
			viewObject.update();
			wakeupOn(w);
		}

		public Mover(ViewObject o) {
			super(new WakeupOnElapsedFrames(0));
			viewObject = o;
		}
	}
}
