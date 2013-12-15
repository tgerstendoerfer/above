package ch.fha.ia02.vector;

import javax.swing.table.*;
import javax.vecmath.*;

/**
 * A cell renderer that displays a <tt>Vector3f</tt>.
 *
 * @author Thomas Gerstendoerfer
 */
public class VectorRenderer extends DefaultTableCellRenderer
{
	public void setValue(Object v) {
		if (v instanceof Vector3f) {
			Vector3f vec = (Vector3f)v;
			setText("(" + (int)vec.x + ", " + (int)vec.y + ", " + (int)vec.z + ")");
		} else {
			setText(v == null ? "" : v.toString());
		}
	}
}
