package ch.fha.ia02.above;

import javax.media.j3d.*;
import javax.vecmath.Point3d;

/**
 * Standard Behavior for behaviors which are always active.
 *
 * @author Michael Muehlebach
 */

public abstract class WorldBehavior extends Behavior{
	public static final Bounds INFINIT_BOUNDS = new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE);
	protected WakeupCondition w;

	public void initialize(){
		wakeupOn(w);
	}

	/**
	 * Constructor with the WakeupCondition
	 *
	 * @param wuc the WakeupCriterion which will be set in the initialize methode.
	 */
	public WorldBehavior(WakeupCondition wuc){
		this(wuc, INFINIT_BOUNDS);
	}

	/**
	 * Constructor with the WakeupCondition
	 *
	 * @param wuc the WakeupCondition which will be set in the initialize methode.
	 * @param bound the bound for the WakeupCondition.
	 */
	public WorldBehavior(WakeupCondition wuc, Bounds bound){
		setSchedulingBounds(bound);
		w = wuc;
	}
}
