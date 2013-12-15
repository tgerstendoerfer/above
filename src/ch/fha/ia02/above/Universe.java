/*
 * Based on work copyright (c) 1999 Justin Couch. http://www.vlc.com.au/~justin/
 */
package ch.fha.ia02.above;

import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;

/**
 * The ABOVE universe.
 *
 * @author Justin Couch
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class Universe extends VirtualUniverse {
	private Model model;
	private Locale locale;
	private BranchGroup viewGroup;
	private BranchGroup utilGroup;
	private ModelGroup worldObjectGroup;
	private BranchGroup worldLight;
	private Camera camera;
	private static final Color3f AMBIENT_LIGHT_COLOR = new Color3f(0.3f, 0.3f, 0.3f);
	private static final Color3f POINT_LIGHT_COLOR = new Color3f(1.0f, 1.0f, 1.0f);
	private static final Point3f POINT_LIGHT_POSITION = new Point3f(999f, 100f, 999f);
	private static final Point3f POINT_LIGHT_ATTENUATION = new Point3f(1.0f, 0.0f, 0.0f);
	private static final String BG_IMAGE = "/media/stars.jpg";


	/** Creates a new universe. */
	public Universe() {
		locale = new Locale(this);
		viewGroup = new BranchGroup();
		worldLight = new BranchGroup();

		if (Application.getSettings().backgroundImageEnabled()) {
			viewGroup.addChild(new SkyBox(getClass().getResource(BG_IMAGE), 100f));
		}

		Light aL = new AmbientLight(AMBIENT_LIGHT_COLOR);
		PointLight pL = new PointLight(POINT_LIGHT_COLOR, POINT_LIGHT_POSITION, POINT_LIGHT_ATTENUATION);
		aL.setInfluencingBounds(WorldBehavior.INFINIT_BOUNDS);
		pL.setInfluencingBounds(WorldBehavior.INFINIT_BOUNDS);

		worldLight.addChild(aL);
		worldLight.addChild(pL);
	}

	/** Adds an object to this universe. */
	public void addWorldObject(Node node) {
		worldObjectGroup.addChild(node);
	}

	/** Adds a BranchGroup directly to the current locale. */
	public void addToCurrentLocale(BranchGroup bg) {
		if (bg != null) {
			bg.compile();
			locale.addBranchGraph(bg);
		}
	}

	/** Sets the model group for this universe. */
	public void setModel(ModelFactory modelFactory) {
		if (model != null) {
			model.stop();
			locale.removeBranchGraph(model.getModelGroup());
			if (Application.getSettings().resetCamreaOnRestart()) {
				resetCameraPosition();
			}
		}
		model = modelFactory.createModel();
		model.getModelGroup().compile();
		locale.addBranchGraph(model.getModelGroup());

		model.start();
	}

	/** Sets the util group for this universe. This Group is for additional stuff like the VesselViewer */
	public void setUtilGroup(BranchGroup ug) {
		if (utilGroup != null) {
			locale.removeBranchGraph(utilGroup);
		}
		utilGroup = ug;
		utilGroup.compile();
		locale.addBranchGraph(utilGroup);
	}


	/** Adds a camera to this universe. */
	public void addCamera(Camera cam) {
		camera = cam;
		viewGroup.addChild(cam);
	}

	/** Returns the befor set camera or null. */
	public Camera getCamera() {
		return camera;
	}

	/** Resets the camera's position to its default value. */
	public void resetCameraPosition() {
		if (camera != null) {
			camera.resetBehavior();
		}
	}

	/**
	 * Activates this universe.
	 * Compiles the scene graph.
	 */
	public void makeLive() {
		viewGroup.compile();
		worldLight.compile();

		locale.addBranchGraph(viewGroup);
		locale.addBranchGraph(worldLight);
	}

	/**
	 * Returns a reference to the current model.
	 */
	public Model getModel() {
		return model;
	}
}
