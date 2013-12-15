package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Abstract base class for top-level windows that display
 * a view on the 3D-universe.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public abstract class ViewerFrame extends AboveFrame
	implements ActionListener
{
	/** The global universe. */
	protected Universe universe;

	// access to menu items (for efficient event handling)
	private JMenuItem menuClose;
	private JMenuItem menuViewSettings;
	private JMenuItem menuCredits;

	public static final int menuShortcutKey =
		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();


	/**
	 * Creates an initially invisible viewer frame with a menu bar,
	 * a new universe, a default camera and a default title.
	 * @see #ViewerFrame(String)
	 */
	public ViewerFrame() {
		this(Application.getTitle());
	}

	/**
	 * Creates an initially invisible viewer frame with a
	 * menu bar, a new universe, and a default camera.
	 *
	 * @param title the title for the window.
	 *
	 * @see #createMenuBar()
	 * @see #initUniverse
	 */
	public ViewerFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setJMenuBar(createMenuBar());
		initUniverse();
	}

	/**
	 * Initializes the universe and adds a default camera
	 * that uses this frame's content pane for display.
	 */
	protected void initUniverse() {
		universe = new Universe();
		Camera cam = new Camera();
		cam.setLocation(new Vector3f(0, 0, 10.0f));
		cam.setHeadlight(false);
		universe.addCamera(cam);
		getContentPane().add(cam.getCanvas(), BorderLayout.CENTER);
	}

	/**
	 * Factory method that creates the menu bar.
	 * Creates the <em>File</em> and <em>Simulation</em> menus.
	 *
	 * @see #createFileMenu
	 * @see #createSimulationMenu
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();

		if (!Application.onMacOSX) menubar.add(createFileMenu());
		menubar.add(createSimulationMenu());
		menubar.add(createHelpMenu());

		return menubar;
	}

	/**
	 * Factory method that creates the <em>File</em> menu,
	 * called by <tt>createMenuBar()</tt>.
	 * This method is not called on Mac OS X.
	 */
	protected JMenu createFileMenu() {
		JMenu m = new JMenu("File");
		m.setMnemonic('F');

		menuClose = new JMenuItem("Exit", 'x');
		menuClose.addActionListener(this);
		m.add(menuClose);

		return m;
	}

	/**
	 * Factory method that creates the <em>Simulation</em> menu,
	 * called by <tt>createMenuBar()</tt>.
	 */
	protected JMenu createSimulationMenu() {
		JMenu m = new JMenu("Simulation");
		m.setMnemonic('S');

		menuViewSettings = new JMenuItem("View Settings...", 'S');
		menuViewSettings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcutKey));
		menuViewSettings.addActionListener(this);
		m.add(menuViewSettings);

		return m;
	}

	/**
	 * Factory methode which creates the <em>Help</em> menu,
	 * called by <tt>createMenuBar()</tt>.
	 */
	protected JMenu createHelpMenu() {
		JMenu m = new JMenu("Help");
		m.setMnemonic('H');

		menuCredits = new JMenuItem("About...", 'A');
		menuCredits.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_A, menuShortcutKey));
		menuCredits.addActionListener(this);
		m.add(menuCredits);

		return m;
	}

	/** Processes menu events. */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == menuClose) {
			System.exit(0);
		} else if (src == menuViewSettings) {
			Settings.showDialog(this, "View Settings");
		} else if (src == menuCredits) {
			Credits.showDialog(this);
		}
	}
}
