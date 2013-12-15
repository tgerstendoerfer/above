package ch.fha.ia02.above;

import java.net.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;

/**
 * Eye candy that represents an explosion in the 3D-world.
 * The explosion needs to be added to the scene graph but will
 * remove itself after the specified time.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class Explosion extends BranchGroup {

	/** Texture filenames. */
	private static final String[] texfilenames = {
		"/media/exp02_1.png",
		"/media/exp02_2.png",
		"/media/exp02_3.png",
	};

	/** The texture. */
	private static Texture[] textures;

	/** Array of standard texture coordinates. */
	private static final float[] TEXCOORDS = {
		0, 0,
		1, 0,
		1, 1,
		0, 1,
	};

	/** Specifies if textures are to be loaded. */
	private static boolean loadTextures = Application.getSettings().loadTextures();

	/**
	 * Register a listener to update the <tt>loadTextures</tt> flag
	 * whenever the settings are modified.
	 */
	private static SettingsChangeListener settingslistener = attachSettingsListener();
	private static SettingsChangeListener attachSettingsListener() {
		SettingsChangeListener scl = new SettingsChangeListener() {
			public void settingsChange(Settings settings) {
				loadTextures = settings.loadTextures();
			}
		};
		Application.getSettings().addSettingsChangeListener(scl);
		return scl;
	}

	/** Appearance for simplified explosions. */
	private static final Appearance EXPLOSION_APPEARANCE
		= createExplosionAppearance();

	/**
	 * Creates a new explosion object ready for insertion
	 * into the scene graph.
	 *
	 * @param position position at which the explosion occurs.
	 * @param size diameter of the fireball, in m.
	 */
	public Explosion(Vector3f position, float size) {
		this(position, size, 0.3f);
	}

	/**
	 * Creates a new explosion object ready for insertion
	 * into the scene graph.
	 *
	 * @param position position at which the explosion occurs.
	 * @param size diameter of the exploding object, in m.
	 * @param duration duration of the explosion, in seconds.
	 */
	public Explosion(Vector3f position, float size, final float duration) {
		if (size <= 0) throw new IllegalArgumentException("size must be > 0.");
		if (duration <= 0) throw new IllegalArgumentException("duration must be > 0.");

		TransformGroup tg = new TransformGroup();
		Transform3D location = new Transform3D();
		location.setTranslation(position);
		tg.setTransform(location);

		setCapability(BranchGroup.ALLOW_DETACH);
		addChild(tg);

		size *= 3; // adjust size for a nice effect
		float[] vertices = {
			-size, -size, 0,
			 size, -size, 0,
			 size,  size, 0,
			-size,  size, 0,
		};
		QuadArray sprite = new QuadArray(4,
			GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		sprite.setCoordinates(0, vertices);
		sprite.setTextureCoordinates(0, 0, TEXCOORDS);

		final OrientedShape3D shape = new OrientedShape3D();
		shape.setGeometry(sprite);
		if (loadTextures &&
			((textures == null && loadTextureImages(texfilenames))
				|| textures.length > 0))
		{
			Appearance ap = new Appearance();
			ap.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
			ap.setTexture(textures[0]);
			TransparencyAttributes ta = new TransparencyAttributes();
			ta.setTransparencyMode(TransparencyAttributes.BLENDED);
			ta.setTransparency(0.5f);
			ap.setTransparencyAttributes(ta);
			shape.setAppearance(ap);
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			if (textures.length > 1) {
				tg.addChild(new WorldBehavior(new WakeupOnElapsedTime((long)(duration*1000/textures.length))) {
					int n = 1;
					public void processStimulus(java.util.Enumeration criteria) {
						Appearance ap = shape.getAppearance();
						ap.setTexture(textures[n]);
						if (++n < textures.length) wakeupOn(w);
					}
				});
			}
		} else {
			shape.setAppearance(EXPLOSION_APPEARANCE);
		}

		tg.addChild(shape);

		// Add behavior that removes this projectile from the
		// scene graph as soon as the projectile expires.
		tg.addChild(new WorldBehavior(new WakeupOnElapsedTime((long)(duration*1000))) {
			public void processStimulus(java.util.Enumeration criteria) {
				detach();
			}
		});
	}

	/** Returns the appearance for simplified explosion objects. */
	protected static Appearance createExplosionAppearance() {
		Color3f c = new Color3f(1, 0.8f, 0.2f);
		Appearance ap = new Appearance();
		ap.setMaterial(new Material(c, c, c, c, 17));
		ap.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.5f));
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		return ap;
	}

	/**
	 * Loads all explosion textures.
	 * @return <tt>true</tt> if at least one image could be loaded.
	 */
	protected boolean loadTextureImages(String[] files) {
		textures = new Texture[(files == null) ? 0 :  files.length];
		for (int i=0; i<files.length; i++) {
			Application.progress("Loading " + files[i]);
			URL url = getClass().getResource(files[i]);
			if (url == null) {
				System.out.println("File not found: " + files[i]);
			} else {
				TextureLoader tl = new TextureLoader(url, null);
				textures[i] = tl.getTexture();
			}
		}
		Application.progressHide();
		return textures.length > 0;
	}

	/**
	 * Helper Method creates an invisible explosion to preload
	 * appearance, textures and the like.
	 */
	public static void preload() {
		new Explosion(new Vector3f(0, 0, 0), 1, 1);
	}

}
