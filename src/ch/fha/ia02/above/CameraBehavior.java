package ch.fha.ia02.above;

import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.*;

/**
 * Behavior for the camera.
 *
 * @author Michael Muehlebach
 */
public class CameraBehavior{
	private TransformGroup tg;
	private Transform3D told = new Transform3D();
	private Transform3D t = new Transform3D();
	private Transform3D ot = new Transform3D();
	private Transform3D nt = new Transform3D();
	private Transform3D zt = new Transform3D();
	private int old_x, old_y, old_z;
	private int x, y, z;

	/** Angle for left/right rotation. */
	protected float a;

	/** Angle for up/down rotation. */
	protected float b;

	/** Distance for foreward/backward (zoom) translation. */
	protected float zoom;

	/** Add's the Mouse Listeners to the given Component. */
	public CameraBehavior(final java.awt.Component comp, TransformGroup transformGroup) {
		tg = transformGroup;

		comp.addMouseListener(
			new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						((Camera)tg).stopFollow();
						x = e.getX();
						y = e.getY();
					}

					if (e.getButton() == MouseEvent.BUTTON2) {
						z = e.getY();
					}
				}
		});

		//direction (up/down and left/right)
		comp.addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
						float zo;
						old_x = x;
						old_y = y;

						x = e.getX();
						y = e.getY();


						zo = Math.abs(zoom)/400f;

						if (zo == 0)
							zo = 1;
						else if (zoom > 0)
							zo = 1/zo;

						a += (Math.PI/comp.getHeight()*(x-old_x))/5f/zo;
						b += (Math.PI/comp.getWidth()*(y-old_y))/5f/zo;
					}
					else if (e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK) {
						old_z = z;
						z = e.getY();

						zoom += (z-old_z)*2f;
					}
					refresh();
				}
		});

		//zoom (foreward/backward)
		comp.addMouseWheelListener(
			new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					zoom += e.getWheelRotation()*100f;

					refresh();
				}
		});
	}

	/** Change the transformation to the defined values a, b and zoom. */
	protected void refresh() {
		zt.setTranslation(new Vector3f(0, 0, zoom));
		ot.rotY(a);
		nt.rotX(b);

		t.setIdentity();
		t.mul(ot);
		t.mul(nt);
		t.mul(zt);
		tg.setTransform(t);
	}

	/** Reset the values back to the start and refreshes the transformation. */
	public void reset(){
		a = 0;
		b = 0;
		zoom = 0;

		refresh();
	}
}
