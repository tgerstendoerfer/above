package ch.fha.ia02.above;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Splash Screen for the application.
 *
 * @author Thomas Gerstendoerfer
 */
public class SplashScreen extends AboveFrame {

	private Image image;
	private Font versionFont = new Font("Dialog", Font.BOLD, 7);
	private String version;
	private String msg; // progress message

	/** Creates a splash screen for the application. */
	public SplashScreen(String title) {
		super(title);
		setUndecorated(true);
		setResizable(false);

		version = Application.getVersion();
		image = new ImageIcon(getClass().getResource(Application.SPLASH_IMAGE)).getImage();

		int w = image.getWidth(this);
		int h = image.getHeight(this);
		setSize(w, h);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - w)/2, (screen.height - h)/2);

		setForeground(Color.gray);
		setVisible(true);
	}

	/** Displays a progress message to the user. */
	public void displayMessage(String msg) {
		this.msg = msg;
		repaint();
	}


	/** Just calls <tt>paint()</tt> to avoid flickering. */
	public void update(Graphics g) {
		paint(g);
	}

	/** Paints the splash screen image. */
	public void paint(Graphics g) {
		Dimension w = getSize();
		g.drawImage(image, 0, 0, this);
		g.drawString(version, 23, 17);
		if (msg != null) {
			g.drawString(msg, 23, w.height - 10);
		}
	}
}
