package ch.fha.ia02.above;

import java.io.*;
import java.awt.Color;
import java.net.URL;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.*;
import com.mnstarfire.loaders3d.*;

/**
 * Holder for a Vessel Type like TIE-Figher or X-Wing.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class Vessel{
	private String name;
	private float scale;
	private Bounds collisionBound;
	private String file;
	private AgentStats stats;

	/**
	 * Set to <tt>true</tt> if the shape for this vessel should always
	 * be automagically oriented towards the camera.
	 * This only makes sense for sprites.
	 */
	private boolean autoOriented = false;


	public static Vessel TIEB = new Vessel(
			"TIE-Bomber",
			new Starfighter.Stats(6, 5000, 200, 320),
			null,
			"/media/3d/TIEB/tie bomber.3ds", 0.0025f);
	public static Vessel TIED = new Vessel(
			"TIE-Defender",
			new Starfighter.Stats(7, 5000, 200, 620),
			null,
			"/media/3d/TIED/tie defender.3ds", 0.0012f);
	public static Vessel TIEF = new Vessel(
			"TIE-Fighter",
			new Starfighter.Stats(6, 10000, 200, 400),
			null,
			"/media/3d/TIEF/tie fighter.3ds", 0.015f);
	public static Vessel TIEI = new Vessel(
			"TIE-Interceptor",
			new Starfighter.Stats(6.23f, 5000, 200, 500),
			null,
			"/media/3d/TIEI/tiei.3ds", 0.018f);
	public static Vessel GUNBOAT = new Vessel(
			"Imperial Gunboat",
			new Starfighter.Stats(16, 5000, 200, 360),
			null,
			"/media/3d/GUNBOAT/imperial gunboat.3ds", 0.0018f);
	public static Vessel LAMBDASHUTTLE = new Vessel(
			"Lambda Shuttle",
			new Starfighter.Stats(20, 5000, 200, 260),
			null,
			"/media/3d/LAMBDASHUTTLE/lambda.3ds", 0.005f);
	public static Vessel SSSD = new Vessel(
			"Sovereign Super Star Destroyer",
			new AgentStats(15000, 1000000, 1, 20),
			null,
			"/media/3d/SSSD/sssd.3ds", 0.004f);
	public static Vessel ISD = new Vessel(
			"Imperial Star Destroyer",
			new AgentStats(1600, 1000000, 1, 40),
			null,
			"/media/3d/ISD/isd.3ds", 0.02f);



	public static Vessel AWING = new Vessel(
			"A-Wing",
			new Starfighter.Stats(9.5f, 10000, 100, 480),
			null,
			"/media/3d/AWING/awing.3ds", 0.12f);
	public static Vessel BWING = new Vessel(
			"B-Wing",
			new Starfighter.Stats(17, 10000, 100, 360),
			null,
			"/media/3d/BWING/bwing.3ds", 0.007f);
	public static Vessel EWING = new Vessel(
			"E-Wing",
			new Starfighter.Stats(11, 10000, 100, 380),
			null,
			"/media/3d/EWING/ewing.3ds", 0.0011f);
	public static Vessel XWING = new Vessel(
			"X-Wing",
			new Starfighter.Stats(12.5f, 10000, 100, 400, Color.RED),
			null,
			"/media/3d/XWING/xwing.3ds", 0.00225f);
	public static Vessel YWING = new Vessel(
			"Y-Wing",
			new Starfighter.Stats(16, 10000, 100, 320),
			null,
			"/media/3d/YWING/ywing.3ds", 0.007f);



	/**
	 * Creates a new vessel object that does not automatically orient itself
	 * towards the camera.
	 *
	 * @param name of this class of vessels.
	 * @param stats describes this vessel's characteristics.
	 * @param bounds simplified shape that defines the bounds
	 *        of this object, used in collision detection.
	 * @param file denotes the 3DS file to load the mesh and textures from.
	 *        Texture files must be in the same directory.
	 * @param scale use this factor to scale the mesh down to unit length (1 meter).
	 */
	public Vessel(
		String name,
		AgentStats stats,
		Bounds bounds,
		String file,
		float scale)
	{
		this(name, stats, bounds, file, scale, false);
	}

	/**
	 * Creates a new vessel object.
	 *
	 * @param name of this class of vessels.
	 * @param stats describes this vessel's characteristics.
	 * @param bounds simplified shape that defines the bounds
	 *        of this object, used in collision detection.
	 * @param file denotes the 3DS file to load the mesh and textures from.
	 *        Texture files must be in the same directory.
	 * @param scale use this factor to scale the mesh down to unit length (1 meter).
	 * @param autoOriented specifies if the generated shape should
	 *        automatically orient itself towards the camera.
	 */
	public Vessel(
		String name,
		AgentStats stats,
		Bounds bounds,
		String file,
		float scale,
		boolean autoOriented)
	{
		this.name = name;
		this.stats = stats;
		this.collisionBound = bounds;
		this.file = file;
		this.scale = scale;
		this.autoOriented = autoOriented;
	}


	/** Returns this vessel's name field. */
	public String getName() {
		return name;
	}

	/** Returns this vessel's length, in meters. */
	public float getLength() {
		return stats.length;
	}

	/** Returns the scaling factor. */
	public float getScale() {
		return scale;
	}

	/** Returns the name of the shape file for this vessel. */
	public String getFile() {
		return file;
	}

	/** Returns the collision bounds for this vessel. */
	public Bounds getCollisionBounds() {
		return collisionBound;
	}

	/** Returns the performace descriptor. */
	public AgentStats getStats() {
		return stats;
	}

	/**
	 * Should this vessel's shape always be automagically
	 * oriented towards the camera?
	 */
	public boolean isAutoOriented() {
		return autoOriented;
	}


	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @return <tt>true</tt> if <var>o</var> has the same values as this one.
	 */
	public boolean equals(Object o) {
		return o instanceof Vessel && equals((Vessel)o);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @return <tt>true</tt> if <var>v</var> has the same values as this one.
	 */
	public boolean equals(Vessel v) {
		return this == v || (
			v != null &&
			eq(name, v.name) &&
			eq(stats, v.stats) &&
			eq(collisionBound, v.collisionBound) &&
			eq(file, v.file) &&
			autoOriented == v.autoOriented &&
			scale == v.scale);
	}
	private static boolean eq(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	/** Returns a hash code value for the object. */
	public int hashCode() {
		int h = safehash(name);
		h = h*17 + safehash(stats);
		h = h*17 + safehash(collisionBound);
		h = h*17 + safehash(file);
		h = h*17 + (int)(scale*100);
		if (autoOriented) h = h * 17;
		return h;
	}
	private static int safehash(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}

	/** Returns a string representation of this object. */
	public String toString() {
		return getClass().getName()
			+ "[name=" + name
			+ ", file=" + file
			+ ", scale=" + scale
			+ ", autoOriented=" + autoOriented
			+ "],";
	}

}
