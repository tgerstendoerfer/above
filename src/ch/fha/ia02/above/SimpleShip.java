package ch.fha.ia02.above;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * A wedge-like shape representing a starship.
 * Its central axis is aligned along the Y-axis.
 *
 * @author Thomas Gerstendoerfer
 */
public class SimpleShip extends Shape3D {
	/** Color of this ship */
	private static final Color3f COLOR = new Color3f(0.5f, 0.5f, 0.5f);


	/**
	 * Creates a new simple ship of the specified size.
	 *
	 * @param size specifies the ship's size.
	 */
    public SimpleShip(float size) {
		Point3f tip = new Point3f(			 0,  0.5f*size, -0.1f*size); // front point
		Point3f top = new Point3f(			 0, -0.5f*size,  0.1f*size); // the top peak point
		Point3f left = new Point3f(-0.25f*size, -0.5f*size, -0.1f*size); // left point
		Point3f right = new Point3f(0.25f*size, -0.5f*size, -0.1f*size); // right point

		Point3f[] verts = {
			tip, right, left,	// bottom
			top, right, tip,	// front right face
			top, tip,   left, 	// front left face
			top, left,  right, 	// back
		};


		int i, face;

		TriangleArray tetra = new TriangleArray(12, TriangleArray.COORDINATES | TriangleArray.NORMALS);

		tetra.setCoordinates(0, verts);

		Vector3f normal = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Point3f [] pts = new Point3f[3];

		for (i = 0; i < 3; i++)
			pts[i] = new Point3f();

		for (face = 0; face < 4; face++) {
			tetra.getCoordinates(face*3, pts);
			v1.sub(pts[1], pts[0]);
			v2.sub(pts[2], pts[0]);
			normal.cross(v1, v2);
			normal.normalize();
			for (i = 0; i < 3; i++) {
				tetra.setNormal((face * 3 + i), normal);
			}
		}
		this.setGeometry(tetra);

		Appearance app = new Appearance();
		Color3f objColor = COLOR;
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
		Material m = new Material(objColor, black, objColor, white, 100.0f);
		m.setLightingEnable(true);
		app.setMaterial(m);

		this.setAppearance(app);
    }
}
