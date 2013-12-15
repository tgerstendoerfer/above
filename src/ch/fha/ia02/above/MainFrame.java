package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.j3d.*;

/**
 * The main window for the application.
 *
 * @author Thomas Gerstendoerfer
 */
public class MainFrame extends ViewerFrame
	implements ActionListener
{
	/** The last used model factory. */
	private ModelFactory modelFactory = new SimpleModelFactory();

	// access to menu items (for efficient event handling)
	private JMenuItem menuRestart;
	private JMenuItem menuResetCamera;
	private JMenuItem menuTogglePause;
	private JMenuItem menuModelInspector;
	private JMenuItem menuCameraFollow;


	/**
	 * Constructs a main window for the simulation.
	 */
	public MainFrame() {
		super();

		Settings settings = Application.getSettings();
		setSize(settings.getMainFrameSize());
		setExtendedState(settings.getMainFrameExtendedState());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			/** Saves window size/state before closing. */
			public void windowClosing(WindowEvent e) {
				int state = getExtendedState();
				if (state == Frame.NORMAL) {
					Application.getSettings().setMainFrameSize(getSize());
				}
				if (state != Frame.ICONIFIED) {
					Application.getSettings().setMainFrameExtendedState(state);
				}
			}
		});

		universe.setModel(modelFactory);
		universe.makeLive();
	}

	/**
	 * Factory method that creates the <em>Simulation</em> menu,
	 * called by <tt>createMenuBar()</tt>.
	 */
	protected JMenu createSimulationMenu() {
		JMenu m = super.createSimulationMenu();

		menuRestart = new JMenuItem("Restart", 'R');
		menuRestart.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_R, menuShortcutKey));
		menuRestart.addActionListener(this);
		m.add(menuRestart);

		menuResetCamera = new JMenuItem("Reset Camera", 'R');
		menuResetCamera.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_R, menuShortcutKey|KeyEvent.SHIFT_MASK));
		menuResetCamera.addActionListener(this);
		m.add(menuResetCamera);

		menuTogglePause = new JMenuItem("Start/Pause", 'P');
		menuTogglePause.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_P, menuShortcutKey));
		menuTogglePause.addActionListener(this);
		m.add(menuTogglePause);

		menuModelInspector = new JMenuItem("Model Inspector", 'M');
		menuModelInspector.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_M, menuShortcutKey));
		menuModelInspector.addActionListener(this);
		m.add(menuModelInspector);

		menuCameraFollow = new JMenuItem("Follow Fighters", 'F');
		menuCameraFollow.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, menuShortcutKey));
		menuCameraFollow.addActionListener(this);
		m.add(menuCameraFollow);

		return m;
	}



	/** Processes menu events. */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == menuRestart) {
			universe.setModel(modelFactory);
		} else if (src == menuModelInspector) {
			ModelInspector.showWindow(this, "Model Inspector", universe.getModel());
		} else if (src == menuTogglePause) {
			Model m = universe.getModel();
			switch (m.getStatus()) {
				case Model.STATUS_RUNNING:	m.stop();	break;
				case Model.STATUS_READY:	m.start();	break;
			}
		} else if (src == menuResetCamera) {
			universe.resetCameraPosition();
		} else if (src == menuCameraFollow) {
			Camera c = universe.getCamera();
			switch (c.getStatus()) {
				case Camera.FOLLOWING:	c.stopFollow();		break;
				case Camera.READY:		c.startFollow();	break;
			}
		} else {
			super.actionPerformed(e);
		}
	}

}
