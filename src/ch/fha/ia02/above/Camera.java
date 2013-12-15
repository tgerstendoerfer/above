package ch.fha.ia02.above;

import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Simple camera with headlight that can be toggled.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class Camera extends TransformGroup
	implements SettingsChangeListener
{
	public static final int READY = 0;
	public static final int FOLLOWING = 1;

	private static final double BACK_CLIP_DISTANCE = 1E9;

	private Transform3D location = new Transform3D();
	private ViewPlatform platform = new ViewPlatform();
	private View view = new View();
	private Canvas3D canvas;
	private boolean following = false;
	private BranchGroup followGroup;
	CameraBehavior cb;
	Vector3f locationVector;

	/** Creates a new camera. */
	public Camera() {
		Settings settings = Application.getSettings();
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		setTransform(location);
		addChild(platform);

		view.setBackClipDistance(BACK_CLIP_DISTANCE);
		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(new PhysicalEnvironment());
		view.attachViewPlatform(platform);
		settingsChange(settings);
		settings.addSettingsChangeListener(this);
		cb = new CameraBehavior(getCanvas(), this);
	}

	/** Sets the canvas for this camera to paint onto. */
	public void setCanvas(Canvas3D canvas) {
		view.addCanvas3D(canvas);
	}

	/**
	 * Creates
	 */
	public Canvas3D getCanvas() {
		if (canvas == null){
			GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice device = env.getDefaultScreenDevice();
			GraphicsConfiguration config = device.getBestConfiguration(template);
			canvas = new Canvas3D(config);
			view.addCanvas3D(canvas);
		}
		return canvas;
	}

	/**
	 * Return the status of the camera.
	 */
	public int getStatus() {
		if (following) {
			return FOLLOWING;
		} else {
			return READY;
		}
	}

	public void startFollow() {
		following = true;
		followGroup = new BranchGroup();
		followGroup.setCapability(BranchGroup.ALLOW_DETACH);
		followGroup.addChild(new CameraFollowBehavior(this));
		this.addChild(followGroup);
	}

	public void stopFollow() {
		following = false;

		if (followGroup != null) {
			followGroup.detach();
			followGroup = null;
		}
	}

	/** Sets this camera's location. */
	public void setLocation(Vector3f loc) {
		locationVector = loc;
		location.setTranslation(loc);
		setTransform(location);
	}

	/** Enables or disables the headlight. */
	public void setHeadlight(boolean enable) {
	}

	/** Resets the CameraBehavior. */
	public void resetBehavior() {
		cb.reset();
	}

	/**
	 * Applies the current configuration.
	 * @param settings the settings object on which the change happened.
	 */
	public void settingsChange(Settings settings) {
		view.setMinimumFrameCycleTime(settings.getMinimumFrameCycleTime());
		view.setFieldOfView(settings.getFieldOfView());
	}
}
