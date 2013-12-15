package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;

/**
 * Separate Application to display a single 3d model.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class VesselViewer extends ViewerFrame {

	private Action[] vesselSelectors = {
		new VesselSelectorAction(Vessel.TIEB),
		new VesselSelectorAction(Vessel.TIED),
		new VesselSelectorAction(Vessel.TIEF),
		new VesselSelectorAction(Vessel.TIEI),
		new VesselSelectorAction(Vessel.ISD),
		new VesselSelectorAction(Vessel.SSSD),

		new VesselSelectorAction(Vessel.AWING),
		new VesselSelectorAction(Vessel.BWING),
		new VesselSelectorAction(Vessel.EWING),
		new VesselSelectorAction(Vessel.XWING),
		new VesselSelectorAction(Vessel.YWING),

		new VesselSelectorAction(Vessel.GUNBOAT),
		new VesselSelectorAction(Vessel.LAMBDASHUTTLE),
	};


	// 3D stuff
	private BranchGroup root;
	private TransformGroup objTrans;
	private BranchGroup old;

	/**
	 * Constructs a main window for the simulation.
	 */
	VesselViewer() {
		super();
		setSize(650, 550);

		/*
		 * Set-up the button panel.
		 */
		JPanel sidePanel = new JPanel(new BorderLayout());
		getContentPane().add(sidePanel, BorderLayout.EAST);

		JPanel buttonPanel = new JPanel(new GridLayout(vesselSelectors.length, 1));
		for (int i=0; i<vesselSelectors.length; i++) {
			buttonPanel.add(new JButton(vesselSelectors[i]));
		}
		sidePanel.add(buttonPanel, BorderLayout.NORTH);


		/*
		 * Set-up 3D environment
		 */
		objTrans = new TransformGroup();
		root = new BranchGroup();
		root.addChild(objTrans);

		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);


		LineArray la = new LineArray(6, LineArray.COORDINATES);
		Point3f[] ps = {
				new Point3f(), new Point3f(1, 0, 0),
				new Point3f(), new Point3f(0, 1, 0),
				new Point3f(), new Point3f(0, 0, 1),};
		la.setCoordinates(0, ps);
		objTrans.addChild(new Shape3D(la));

		ColorCube b = new ColorCube(0.5);
		Appearance app = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		//pa.setCullFace(PolygonAttributes.CULL_NONE);
		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		app.setPolygonAttributes(pa);

		b.setAppearance(app);

		objTrans.addChild(b);

		MouseRotate behavior = new MouseRotate();
		behavior.setTransformGroup(objTrans);
		objTrans.addChild(behavior);
		behavior.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

		universe.setUtilGroup(root);
		universe.makeLive();
	}


	/** Shows the specified vessel by loading it into the universe. */
	protected void showVessel(Vessel vessel) {
		Link l = new Link();
		l.setSharedGroup(ShapeCache.getShape(vessel));

		Transform3D t = new Transform3D();
		t.set(1/vessel.getLength());
		TransformGroup s = new TransformGroup(t);

		if (old != null) old.detach();
		old = new BranchGroup();
		old.setCapability(BranchGroup.ALLOW_DETACH);

		s.addChild(l);
		old.addChild(s);
		objTrans.addChild(old);
	}


	/**
	 * Main entry point for the VesselViewer application
	 */
	public static void main(String[] args) {
		new VesselViewer().setVisible(true);
	}



	/** Selects and displays a vessel when invoked. */
	public class VesselSelectorAction extends AbstractAction {

		/** The vessel to be selected when this action is invoked. */
		private Vessel vessel;

		/**
		 * Creates an action that selects the specified vessel.
		 * @param vessel the vessel to be selected.
		 */
		public VesselSelectorAction(Vessel vessel) {
			super(vessel.getName());
			this.vessel = vessel;
		}

		/** Called when this action is invoked. */
		public void actionPerformed(ActionEvent e) {
			showVessel(vessel);
		}
	}
}
