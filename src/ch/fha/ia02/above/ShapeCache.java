package ch.fha.ia02.above;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.image.*;
import com.sun.j3d.loaders.*;
import com.mnstarfire.loaders3d.*;

/**
 * Utility class that loads and caches the various 3D shapes defined
 * for each vessel.
 * The shapes are stored as shared groups and can be accessed via the
 * corresponding <tt>Vessel</tt> object.
 * <p>
 * Currently only the 3DS file format is supported.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 *
 * @see SharedGroup
 * @see Vessel
 */
public class ShapeCache implements SettingsChangeListener
{
	/** The singleton reference. */
	private static ShapeCache instance = new ShapeCache();

	/** Stores the actual vessels and shapes. */
	private Map map = new HashMap();

	/**
	 * Specifies if textures should be loaded.
	 * Required to detect changes to the global configuration settings.
	 */
	private boolean loadTextures = Application.getSettings().loadTextures();

	/** Array of standard texture coordinates. */
	private static final float[] TEXCOORDS = {
		0, 0,
		1, 0,
		1, 1,
		0, 1,
	};

	/**
	 * Private to avoid instantiation.
	 * Registers itself as settings change listener.
	 */
	private ShapeCache() {
		Application.getSettings().addSettingsChangeListener(this);
	}


	/**
	 * Adds a vessel to the shape cache without actually loading its shape.
	 *
	 * @param v vessel to cache the shape for.
	 */
	public static void add(Vessel v) {
		instance.map.put(v, null);
	}


	/**
	 * Removes a vessel from the shape cache.
	 */
	public static void remove(Vessel v) {
		instance.map.remove(v);
	}


	/**
	 * Returns a shared group of the shape associated to this model.
	 * In case the 3D-mesh was not loaded before, this method attempts
	 * load it from the specified file, and stores it for further
	 * reference.
	 *
	 * @param v the vessel to load the shape for.
	 * @return a shared group containing the 3D-model,
	 *         or <tt>null</tt> if no shape is available.
	 */
	public static SharedGroup getShape(Vessel v) {
		Object o = instance.map.get(v);
		return o == null ? instance.loadShape(v) : (SharedGroup)o;
	}


	/**
	 * Loads all shapes for all vessels.
	 */
	public static void loadShapes() {
		Iterator it = instance.map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			if (entry.getValue() == null) {
				instance.loadShape((Vessel)entry.getKey());
			}
		}
	}

	/**
	 * Loads the shape for a vessel and adds it to the map.
	 *
	 * @param v vessel to load the shape for.
	 * @return the freshly loaded shape or <tt>null</tt> if loading failed.
	 */
	private SharedGroup loadShape(Vessel v) {
		if (v.getFile() == null) return null;
		try {
			URL file = getClass().getResource(v.getFile());
			if (file == null) throw new FileNotFoundException(v.getFile());
			Application.progress("Loading " + v.getName());
			String ext = Application.getExtension(file.toString());
			Node shapenode = null;
			if ("3ds".equals(ext)) {
				shapenode = loadShape3DS(file);
			} else if(
				"gif".equals(ext) ||
				"jpg".equals(ext) ||
				"png".equals(ext))
			{
				shapenode = loadSprite(file, v.isAutoOriented());
			}
			if (shapenode == null) return null;

			Transform3D t = new Transform3D();
			t.set(v.getScale() * v.getLength());
			TransformGroup model = new TransformGroup(t);
			model.addChild(shapenode);
			model.setCollidable(false); // FIXME: remove this again

			SharedGroup shape = new SharedGroup();
			shape.addChild(model);
			Bounds bounds = v.getCollisionBounds();
			if (bounds != null) {
				shape.setCollisionBounds(bounds);
			} else {
				System.err.println("No collision bounds defined for " + v.getName());
			}
			shape.compile();

			map.put(v, shape);
			return shape;
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			Application.progressHide();
		}
		return null;
	}


	/**
	 * Loads the shape for a vessel from a 3D Studio (3DS) file.
	 * @param file URL of the 3ds file,
	 *        textures are expected to be in the same directory.
	 * @return a BranchGroup
	 * @throw FileNotFoundException if the 3ds file could not be found.
	 */
	private BranchGroup loadShape3DS(URL file) throws FileNotFoundException {
		String basedir = file.toString();
		basedir = basedir.substring(0, 1+basedir.lastIndexOf("/"));

		Loader3DS loader = new Loader3DS();
		loader.setURLBase(basedir);
		loader.setTextureLightingOn();
		if (!loadTextures) loader.noTextures();
		return loader.load(file).getSceneGroup();
	}


	private Node loadSprite(URL file, boolean oriented) {
		if (!loadTextures) return null;
		TextureLoader tl = new TextureLoader(file, null);
		//Texture tex = tl.getTexture();
		//tex.setMagFilter(Texture.NICEST);
		//System.out.println(tex);

		ImageComponent2D ic = tl.getImage();
		float x = 0.5f;
		float y = 0.5f * ic.getHeight() / ic.getWidth();
		float[] vertices = {
			-x, -y, 0,
			 x, -y, 0,
			 x,  y, 0,
			-x,  y, 0,
		};

		QuadArray sprite = new QuadArray(4,
			GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		sprite.setCoordinates(0, vertices);
		sprite.setTextureCoordinates(0, 0, TEXCOORDS);

		Appearance app = new Appearance();
		app.setTexture(tl.getTexture());
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(TransparencyAttributes.BLENDED);
		ta.setTransparency(0.5f);
		app.setTransparencyAttributes(ta);

		if (oriented) {
			OrientedShape3D shape = new OrientedShape3D();
			shape.setGeometry(sprite);
			shape.setAppearance(app);
			return shape;
		} else {
			return new Shape3D(sprite, app);
		}
	}


	/**
	 * Removes all prevoiusly loaded shapes from the cache.
	 */
	public static void invalidateShapes() {
		Iterator it = instance.map.entrySet().iterator();
		while (it.hasNext()) {
			((Map.Entry)it.next()).setValue(null);
		}
	}

	/**
	 * This method gets called after a configuration setting was modified.
	 *
	 * @param settings the settings object on which the change happened.
	 */
	public void settingsChange(Settings settings) {
		if (settings.loadTextures() != loadTextures) {
			loadTextures = settings.loadTextures();
			invalidateShapes();
		}
	}
}