package ch.fha.ia02.above;

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import javax.vecmath.*;

/**
 * A 3d cube shape.
 *
 * @author Michael Muehlebach
 */
public class Cube extends QuadArray {
	// The 8 vertices p1, p2, ..., p8 of the cube.
	private static final Point3f p1 = new Point3f( 1.0f, 1.0f, 1.0f);
	private static final Point3f p2 = new Point3f( 1.0f,-1.0f, 1.0f);
	private static final Point3f p3 = new Point3f(-1.0f,-1.0f, 1.0f);
	private static final Point3f p4 = new Point3f(-1.0f, 1.0f, 1.0f);
	private static final Point3f p5 = new Point3f(-1.0f, 1.0f,-1.0f);
	private static final Point3f p6 = new Point3f(-1.0f,-1.0f,-1.0f);
	private static final Point3f p7 = new Point3f( 1.0f,-1.0f,-1.0f);
	private static final Point3f p8 = new Point3f( 1.0f, 1.0f,-1.0f);

	// The 6 faces of the cube.
	private static final Point3f cubeFaces[] = { // internal front face
												p5, p6, p7, p8,

												// internal right face
												p1, p8, p7, p2,

												// internal back face
												p1, p2, p3, p4,

												// internal left face
												p4, p3, p6, p5,

												// internal top face
												p1, p4, p5, p8,

												// internal bottom face
												p3, p2, p7, p6 };

	/**
	 * Default constructor with x-, y-, z-dimension 1.
	 */
	public Cube() {
		this(1.0f);
	}

	/**
	 * Creates a cube with the given dimension.
	 *
	 * @param dim the dimension in each direction.
	 */
	public Cube(float dim) {
		super(cubeFaces.length, QuadArray.COORDINATES |
											 QuadArray.TEXTURE_COORDINATE_2);

		// Coordinates in the texture space. Each cube's face has the
		// same texture coordinates.
		TexCoord2f textCoord[] = {
							new TexCoord2f(0.0f, 0.0f),
							new TexCoord2f(0.0f, dim/10f),
							new TexCoord2f(dim/10f, dim/10f),
							new TexCoord2f(dim/10f, 0.0f) };

		// Scaling of the faces.
		for (int i = 0; i <  cubeFaces.length; i++) {
			cubeFaces[i].scale(dim);
		}

		this.setCoordinates(0, cubeFaces);

		for (int i = 0; i < cubeFaces.length; i++) {
		 // With i mod 4 ==> 0 1 2 3  0 1 2 3  0 1 2 3  0 1 2 3 for
		 // the 4 vertices of the 6 faces, thus each vertex has
		 // a point in the texture space. In this case, each cube's
		 // face has the same texture coordinates.
			this.setTextureCoordinate(0, i, textCoord[i%4]);
		}
	}
}
