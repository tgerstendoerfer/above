package ch.fha.ia02.above;

import java.awt.*;
import javax.swing.*;

/**
 * Singleton, provides acccess to global settings and resources.
 *
 * @author Thomas Gerstendoerfer
 */
public class Application
{
	/** Singleton reference. */
	private static Application singleton;

	/** Stores configuration settings. */
	private Settings settings;

	/** The application's main frame. */
	private ViewerFrame mainFrame;

	/** Reference to the splash creen while it exists. */
	private static SplashScreen splash;

	/** Set to <tt>true</tt> if we're runnig on Mac OS X. */
	public static final boolean onMacOSX
		= "Mac OS X".equals(System.getProperty("os.name"));

	/** Relative path to the application image. */
	public static final String SPLASH_IMAGE = "/media/splash.jpg";


	/** Specifies the application title. */
	private String apptitle = "ABOVE";

	/** The version string. */
	private String versionstring = null;

	/** Prevent instantiation. */
	private Application () {
		Package p = getClass().getPackage();
		if (p != null) {
			String title = p.getSpecificationTitle();
			versionstring = p.getImplementationVersion();
			if (title != null) apptitle = title;
			if (versionstring != null) apptitle = apptitle + " " + versionstring;
		}
		settings = new Settings();

		// perform platform-specific initialization things
		if (onMacOSX) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		// load the appropriate look and feel
		// for the platform we're running on
		try {
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Returns a reference to the singleton instance.
	 * @return a reference to the singleton instance of this class.
	 */
	private static Application instance() {
		if (singleton == null) {
			singleton = new Application();
		}
		return singleton;
	}

	/** Returns the title of this application. */
	public static String getTitle() {
		return instance().apptitle;
	}

	/** Returns the application version in human-readable form. */
	public static String getVersion() {
		return instance().versionstring;
	}

	/** Provides access to configuration settings. */
	public static Settings getSettings() {
		return instance().settings;
	}


	/**
	 * Provides access to the main frame.
	 * @return a reference to the main frame,
	 *         or <tt>null</tt> if there is none.
	 */
	public static ViewerFrame getMainFrame() {
		return instance().mainFrame;
	}


	/**
	 * Presents a progress message to the user.
	 * @param msg the message to display.
	 */
	public static void progress(String msg) {
		instance().progressImpl(msg);
	}

	/**
	 * Hides an eventually existing proress dialog.
	 */
	public static void progressHide() {
		if (splash != null) {
			splash.displayMessage(null);
		}
	}

	/**
	 * Actually implements displaying a progress message to the user.
	 *
	 * @param msg the message to display, if <tt>null</tt>.
	 */
	private void progressImpl(String msg) {
		if (splash != null) {
			splash.displayMessage(msg);
		} else if (msg != null) {
			System.out.println(msg + "...");
		}
	}


	/**
	 * Get the extension of a file, converted to lower case.
	 * <p>
	 * From <a target="_blank" href="http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html#filters">Sun's Java Tutorial</a>.
	 */
    public static String getExtension(String f) {
        String ext = null;
        int i = f.lastIndexOf('.');
        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i+1).toLowerCase();
        }
        return ext;
    }


	/**
	 * Main entry point for the ABOVE application
	 * if run from the command line.
	 */
	public static void main(String[] args) {
		try {
			splash = new SplashScreen("Loading ABOVE...");
		}
		catch (Exception e) {
			System.err.println("Could not display splash screen: " + e.getMessage());
		}
		try {
			instance().mainFrame = new MainFrame();
			instance().mainFrame.setVisible(true);
			if (splash != null) splash.dispose();
			splash = null;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1); // really shut down if an exception gets here
		}
	}
}
