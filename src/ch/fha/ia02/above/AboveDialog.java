package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The main class for creating a dialog window in the ABOVE application.
 * <p>
 * Unlike the original <tt>JDialog</tt>, this dialog window is actually
 * closed when the user presses the <em>Esc</em> key.
 *
 * @author Thomas Gerstendoerfer
 */
public class AboveDialog extends JDialog {

	/**
	 * Creates a non-modal dialog without a title and without a specified Frame owner.
	 * A shared, hidden frame will be set as the owner of the dialog.
	 *
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
	 */
	public AboveDialog() throws HeadlessException {
		super();
	}

	/**
	 * Creates a modal or non-modal dialog with the specified title
	 * and the specified owner frame.
	 * If <tt>owner</tt> is <tt>null</tt>, a shared, hidden frame will
	 * be set as the owner of this dialog.
	 *
	 * @param owner the non-null <tt>Dialog</tt> from which the dialog is displayed.
	 * @param title the String to display in the dialog's title bar.
	 * @param modal true for a modal dialog, false for one that allows
	 *        other windows to be active at the same time.
	 *
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
	 */
	public AboveDialog(Frame owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
	}

	/**
	 * Creates a modal or non-modal dialog with the specified title
	 * and the specified owner dialog.
	 *
	 * @param owner the non-null <tt>Dialog</tt> from which the dialog is displayed.
	 * @param title the String to display in the dialog's title bar.
	 * @param modal true for a modal dialog, false for one that allows
	 *        other windows to be active at the same time.
	 *
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
	 */
	public AboveDialog(Dialog owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
	}




	/**
	 * Overridden to register an action so that pressing the <em>Esc</em>
	 * key will dismiss the dialog.
	 * <p>
	 * The idea to iverride this method to make <em>esc</em> really work
	 * was taken from a post to Sun's Java Developer Forum, available at:
	 * <br>
	 * <a href="http://forum.java.sun.com/thread.jsp?thread=332929&forum=57"
		>http://forum.java.sun.com/thread.jsp?thread=332929&forum=57</a>.
	 *
	 * @return a <code>JRootPane</code> with the appropiate action registered.
	 * @see #onEscPressed()
	 */
	protected JRootPane createRootPane() {
		/*
		 * The idea to override this
		 * http://forum.java.sun.com/thread.jsp?thread=332929&forum=57&message=2019874
		 */

		JRootPane rootPane = new JRootPane();
		rootPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).
		put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), ESC_PRESSED );

		rootPane.getActionMap().
		put(ESC_PRESSED, new AbstractAction(ESC_PRESSED) {
			public void actionPerformed(ActionEvent e) {
				onEscPressed();
			}
		});
		return rootPane;
	}

	/** Names the event created when the esc key is pressed. */
	private static String ESC_PRESSED = "esc-presssed";

	/**
	 * Dismisses the dialog.
	 * This method gets called when the user presses the <em>Esc</em>-key.
	 * Override this method to change the behavior in your dialog window.
	 */
	protected void onEscPressed() {
		dispose();
	}
}