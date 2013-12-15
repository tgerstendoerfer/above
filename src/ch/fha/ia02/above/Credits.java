package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import java.nio.charset.Charset;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

/**
 * Provide access to credits and the creditscreen.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class Credits {
	private static final String TITLE = "About ABOVE";
	private static final String CREDITS = "/credits.dat";
	private static Credits my;
	private java.util.List credits = new java.util.LinkedList();

	/**
	 * Stand for a credit category which has a name and
	 * contain several people who earns this credits.
	 */
	private static class Category {
		public String name;
		public java.util.List members = new java.util.LinkedList();
	}

	/**
	 * This methode provide access to the credits singleton.
	 */
	public static Credits getCredits() {
		return (my == null) ? new Credits() : my;
	}

	private Credits() {
		credits.add(Application.getTitle());

		InputStream cred = this.getClass().getResourceAsStream(CREDITS);

		try {
			Charset cs = Charset.forName("UTF-8");
			BufferedReader reader = new BufferedReader(new InputStreamReader(cred, cs));
			String line;

			try {
				while ((line = reader.readLine()) != null) {
					Category cat = new Category();
					int bis = line.indexOf(";");
					cat.name = line.substring(0, bis).trim();

					for (int von = ++bis; von < line.length(); von = ++bis) {
						bis = line.indexOf(";", von);
						cat.members.add(line.substring(von, bis).trim());
					}
					credits.add(cat);
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		} catch (NullPointerException e) {
			System.err.println(this.getClass().getName() + ": Couldn't read file: " + CREDITS);
		}
	}

	/**
	 * This class is the real creditscreen with animation and all.
	 */
	public static class CreditScreen extends JPanel implements Runnable {
		private float curr;
		private Credits c;
		private static final int FONTHEIGHT = 20;
		private Font font = new Font(null, Font.BOLD, FONTHEIGHT);
		private JPanel text;
		private boolean closed;

		public CreditScreen() {
			c = Credits.getCredits();

			setLayout(new BorderLayout());
			setBackground(Color.BLACK);

			add(new JLabel(
				new ImageIcon(this.getClass().getResource(Application.SPLASH_IMAGE))), BorderLayout.NORTH);

			text = new JPanel() {
				public void paint(Graphics g) {
					int lastTop = 0;
					int top = this.getHeight();
					int m = this.getWidth()/2;			// horizontal middle
					int a = top - (int)curr;			// displacement to the top line of
														// the first line
					int linespace = FONTHEIGHT + 2;		// vertical space between two lines

					g.setColor(Color.BLACK);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					//g.setColor(Color.BLUE);
					g.setColor(new Color(47, 125, 255));
					g.setFont(font);

					java.util.Iterator it = c.credits.iterator();
					while (it.hasNext()) {
						Object element = it.next();

						if (element instanceof String) {
							String s = (String)element;
							g.drawString(s, m - g.getFontMetrics().stringWidth(s)/2, a);
							a += 2*linespace;
						} else if (element instanceof Category) {
							Category el = (Category)element;
							int x = m - g.getFontMetrics().stringWidth(el.name);
							g.drawString(el.name, x - 10, a);

							java.util.Iterator itt = el.members.iterator();
							while (itt.hasNext()) {
								String subname = (String)itt.next();
								g.drawString(subname, m + 10, a);

								a += linespace;
							}
						}
						a += linespace;
						lastTop = a;
					}

					if ((lastTop + FONTHEIGHT) < 0) {
						curr = 0;
					}
				}
			};

			text.setBackground(Color.BLACK);
			add(text, BorderLayout.CENTER);

			text.setPreferredSize(new Dimension(550, 300));

			new Thread(this).start();
		}

		public void run() {
			while (!closed) {
				curr += 1.0;
				text.repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
	}

	/**
	 * Creates a credit dialog window/panel.
	 */
	public static JDialog showDialog(Frame parent) {
		JDialog dlg = new AboveDialog(parent, TITLE, true);
		dlg.getContentPane().add(new Credits.CreditScreen());
		dlg.setResizable(false);
		dlg.pack();
		dlg.setVisible(true);
		return dlg;
	}
}
