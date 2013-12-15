package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.vecmath.*;

import ch.fha.ia02.vector.*;

/**
 * Shows a detailed, regularly updated view of a model and all
 * its enclosed objects.
 *
 * @author Thomas Gerstendoerfer
 */
public class ModelInspector extends JPanel
{
	/** Update delay, in milliseconds. */
	private static final int UPDATE_DELAY = 1000;

	private static String[] columns = {
		"Name",
		"Faction",
		"Health",
		"Status",
		"Location",
		"Speed",
		"Class",
		};

	private static final int COL_NAME = 0;
	private static final int COL_FACTION = 1;
	private static final int COL_HEALTH = 2;
	private static final int COL_STATUS = 3;
	private static final int COL_LOCATION = 4;
	private static final int COL_VELOCITY = 5;
	private static final int COL_CLASS = 6;


	private Model model;
	private JLabel modelLabel;
	private InspectorTableModel tableModel;

	/** Handles update requests for the top-level viewer. */
	private ActionListener modelLabelUpdater = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			modelLabel.setText(model.toString());
			tableModel.fireTableDataChanged();
		}
	};


	/**
	 * Creates a new inspector for the specified model.
	 */
	public ModelInspector(Model model) {
		this.model = model;
		setLayout(new BorderLayout());
		Timer timer = new Timer(UPDATE_DELAY, modelLabelUpdater);
		add(modelLabel = new JLabel(model.toString()), BorderLayout.NORTH);
		tableModel = new InspectorTableModel(model.getAgents());
		JTable table = new JTable(tableModel);
		table.getColumnModel().getColumn(COL_LOCATION).setCellRenderer(new VectorRenderer());
		table.getColumnModel().getColumn(COL_VELOCITY).setCellRenderer(new VectorLengthRenderer());
		add(new JScrollPane(table), BorderLayout.CENTER);
		timer.start();
	}


	/**
	 * Model for the inspector table.
	 */
	public static class InspectorTableModel extends AbstractTableModel
	{
		private Agent[] agents;

		public InspectorTableModel(Agent[] agents) {
			this.agents = agents;
		}

		public String getColumnName(int col) {
			return columns[col];
		}

		public int getRowCount() {
			return agents.length;
		}

		public int getColumnCount() { return columns.length; }

		public Object getValueAt(int row, int col) {
			Agent o = agents[row];
			Object v = null;
			switch(col) {
				case COL_NAME:      v = o.name; break;
				case COL_FACTION:   v = o.faction; break;
				case COL_HEALTH:    v = new Integer((int)o.health); break;
				case COL_STATUS:    v = o.statusText() ; break;
				case COL_LOCATION:  v = o.position; break;
				case COL_VELOCITY:  v = o.velocity; break;
				case COL_CLASS:     v = o.getClass().getName(); break;
			}
			return v;
		}
	}


	/**
	 * Creates a new model inspector dialog window.
	 */
	public static JDialog showWindow(Frame parent, String title, Model model) {
		JDialog w = new AboveDialog();
		w.setTitle(title);
		w.getContentPane().add(new ModelInspector(model));
		w.pack();
		w.show();
		return w;
	}
}
