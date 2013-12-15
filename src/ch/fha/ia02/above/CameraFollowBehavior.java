package ch.fha.ia02.above;

import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.*;

/**
 * Behavior for the following camera.
 *
 * @author Michael Muehlebach
 */
public class CameraFollowBehavior extends WorldBehavior {
	protected Camera cam;

	public CameraFollowBehavior(Camera c) {
		super(new WakeupOnElapsedFrames(0));
		cam = c;
	}

	public void processStimulus(java.util.Enumeration criteria){
		if (cam.getStatus() == Camera.FOLLOWING) {
			wakeupOn(w);
		}
		refresh();
	}

	public void refresh() {
		Model m = Application.getMainFrame().universe.getModel();
		double a, c;

		Vector3f center = m.center;
		Vector3f p = new Vector3f();
		p.sub(center, cam.locationVector);

		// Calculation for "nose" height
		c = Math.sqrt(p.y*p.y + p.z*p.z);
		a = (c != 0) ? (Math.asin(Math.abs(p.y)/c)) : 0;
		if (p.y < 0) //check if the nose looks up or down
			a = -a;;

		cam.cb.b = (float)a;


		// Calculation for the direction around the Y-axis
		c = Math.sqrt(p.z*p.z + p.x*p.x);
		a = (c != 0) ? -Math.asin(Math.abs(p.x)/c) : 0;
		if (p.z > 0) //check if the orientation goes not into the std dir (-z)
			a = Math.PI - a;
		if (p.x < 0) //check if the rotation should go clockwise
			a = -a;

		cam.cb.a = (float)a;

		cam.cb.refresh();
	}
}
