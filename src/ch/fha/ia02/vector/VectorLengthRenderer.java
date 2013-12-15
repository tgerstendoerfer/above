package ch.fha.ia02.vector;

import javax.swing.*;
import javax.swing.table.*;
import javax.vecmath.*;

/**
 * A cell renderer that displays the length of a <tt>Vector3f</tt>.
 *
 * @author Thomas Gerstendoerfer
 */
public class VectorLengthRenderer extends DefaultTableCellRenderer {

	public VectorLengthRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.TRAILING);
	}

	public void setValue(Object v) {
		if (v instanceof Vector3f) {
			setText((int)((Vector3f)v).length() + " m/s");
		} else {
			setText(v == null ? "" : v.toString());
		}
	}
}
