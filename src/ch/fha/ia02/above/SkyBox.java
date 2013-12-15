package ch.fha.ia02.above;

import javax.media.j3d.*;
import com.sun.j3d.utils.image.*;
import javax.vecmath.*;

/**
 * The ABOVE universe sky box. It is for the star background.
 *
 * @author Michael Muehlebach
 */
public class SkyBox extends Shape3D {

	/**
	 * Default constructor: set default side length of the cube to 1.
	 *
	 * @param bgImage URL to the background image.
	 */
	public SkyBox(java.net.URL bgImage) {
		this(bgImage, 1.0f);
	}

	/**
	 * Constructor which creates a sky box with the given length.
	 *
	 * @param bgImage URL to the background image.
	 * @param sideLength length of the side of the cube.
	 */
	public SkyBox(java.net.URL bgImage, float sideLength) {
		super();
		this.setGeometry(new Cube(sideLength));

		Texture tex;
		TextureAttributes textAttr;
		Appearance appearance = new Appearance();

		// This code block is only necessary to insure, in all cases, the correct
		// rendering of the 6 faces of the cube (bug in Java3D version 1.2.0 !).
		// Set up the polygon's rendering-mode
		PolygonAttributes polygonAttributes = new PolygonAttributes();
		polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		appearance.setPolygonAttributes(polygonAttributes);

		// Loading the texture for the 6 cube's faces.
		TextureLoader texLoad = new TextureLoader(bgImage, null);
		tex = texLoad.getTexture();
		appearance.setTexture(tex);

		// Application modes of the texture
		textAttr = new TextureAttributes();
		textAttr.setTextureMode(TextureAttributes.REPLACE); // there still are: BLEND, DECAL,
														  //                  and MODULATE
		appearance.setTextureAttributes(textAttr);

		// The appearance is passed to the instance this of the cube.
		this.setAppearance(appearance);
	}
}
