package ch.fha.ia02.above;

import java.awt.*;
import javax.swing.*;

/**
 * Abstract base class for all top-level windows in the ABOVE application.
 * Provides an icon and a default title, and sets the default close operation
 * to <tt>EXIT_ON_CLOSE</tt>
 *
 * @author Thomas Gerstendoerfer
 */
public abstract class AboveFrame extends JFrame {

	/**
	 * Creates an initially invisible frame with
	 * an icon image and a default title.
	 */
	public AboveFrame() {
		this(Application.getTitle());
	}

	/**
	 * Creates an initially invisible frame with
	 * an icon image and the specified title.
	 *
	 * @param title the title for the window.
	 */
	public AboveFrame(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("rbl-16.gif"));
			if (icon != null) setIconImage(icon.getImage());
		}
		catch (Exception e) {
			System.err.println("Could not load the application icon.");
		}
	}
}
