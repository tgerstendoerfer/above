package ch.fha.ia02.above;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.util.prefs.*;

/**
 * Provides access to configuration settings.
 *
 * @author Thomas Gerstendoerfer
 */
public class Settings
{
	private Preferences prefs;
	private boolean dirty = false;
	private java.util.List changeListeners = new ArrayList();

	/**
	 * Creates and initializes this settings object.
	 */
	Settings() {
		prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.addPreferenceChangeListener(new PreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent evt) {
				//System.err.println(evt.getKey() + " changed to " + evt.getNewValue());
				dirty = true;
				fireSettingsChange();
			}
		});
	}

	/** Returns <tt>true</tt> if there are any unsaved changes. */
	public boolean isDirty() {
		return dirty;
	}

	/** Saves the settings. */
	public void save() throws BackingStoreException {
		prefs.flush();
		dirty = false;
	}

	/** Resets all settings to their default values. */
	public synchronized void reset() throws BackingStoreException {
		prefs.clear();
		dirty = false;
	}


	/**
	 * Invokes all registered settings change listeners.
	 * @see SettingsChangeListener
	 */
	protected final void fireSettingsChange() {
		Iterator it = changeListeners.iterator();
		while (it.hasNext()) {
			try {
				((SettingsChangeListener)it.next()).settingsChange(this);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Registers the specified listener to receive a notification
	 * after a configuration setting was modified.
	 *
	 * @param scl The settings change listener to add.
	 * @throws NullPointerException if <tt>scl</tt> is null.
	 * @see #removeSettingsChangeListener(SettingsChangeListener)
	 */
	public void addSettingsChangeListener(SettingsChangeListener scl) {
		if (scl == null) throw new NullPointerException();
		changeListeners.add(scl);
	}

	/**
	 * Removes the specified settings change listener,
	 * so it no longer receives <em>settings change</em> events.
	 * @param scl The settings change listener to remove.
	 * @see #addSettingsChangeListener(SettingsChangeListener)
	 */
	public void removeSettingsChangeListener(SettingsChangeListener scl) {
		changeListeners.remove(scl);
	}


	/** Retrieves if collision spheres should be drawn. */
	public boolean showCollisionSpheres() {
		return prefs.getBoolean(SHOW_COLLISION_SPHERES, false);
	}
	/** Sets if collision spheres should be drawn. */
	public void setShowCollisionSpheres(boolean newValue) {
		prefs.putBoolean(SHOW_COLLISION_SPHERES, newValue);
	}
	private static final String SHOW_COLLISION_SPHERES = "ShowCollisionSpheres";


	/** Retrieves if collisions should be cause a colorchange. */
	public boolean showCollisionColor() {
		return prefs.getBoolean(SHOW_COLLISION_COLOR, true);
	}
	/** Sets if collisions shall be causing a colorchange. */
	public void setShowCollisionColor(boolean newValue) {
		prefs.putBoolean(SHOW_COLLISION_COLOR, newValue);
	}
	private static final String SHOW_COLLISION_COLOR = "ShowCollisionColor";


	/** Retrieves if the headlight should be enabled. */
	public boolean showHeadlight() {
		return prefs.getBoolean(CAMERA_HEADLIGHT, false);
	}
	/** Sets if the headlight should be enabled. */
	public void setHeadlight(boolean newValue) {
		prefs.putBoolean(CAMERA_HEADLIGHT, newValue);
	}
	private static final String CAMERA_HEADLIGHT = "CameraHeadlight";


	/** Retrieves if the background image should be displayed. */
	public boolean backgroundImageEnabled() {
		return prefs.getBoolean(BACKGROUND_IMAGE_ENABLED, !Application.onMacOSX);
	}
	public void setBackgroundImageEnabled(boolean enabled) {
		prefs.putBoolean(BACKGROUND_IMAGE_ENABLED, enabled);
	}
	private static final String BACKGROUND_IMAGE_ENABLED = "BackgroundImageEnabled";


	/** Retrieves if complex shapes should be used. */
	public boolean detailedShapes() {
		return prefs.getBoolean(DETAILED_SHAPES, true);
	}
	/** Sets if detailed shapes are enabled. */
	public void setDetailedShapes(boolean enabled) {
		prefs.putBoolean(DETAILED_SHAPES, enabled);
	}
	private static final String DETAILED_SHAPES = "DetailedShapesEnabled";


	/** Specifies if textures should be loaded. */
	public boolean loadTextures() {
		return prefs.getBoolean(LOAD_TEXTURES, true);
	}
	private static final String LOAD_TEXTURES = "LoadTextures";


	/**
	 * Specifies if the camera position should be reset when
	 * a new model is started, or the model is restarted.
	 */
	public boolean resetCamreaOnRestart() {
		return prefs.getBoolean(RESET_CAMERA_ON_RESTART, true);
	}
	private static final String RESET_CAMERA_ON_RESTART = "ResetCameraOnRestart";


	/** Retrieves the minimum frame cycle time, in milliseconds. */
	public int getMinimumFrameCycleTime() {
		return prefs.getInt(MIN_FRAME_CYCLE_TIME, 15);
	}
	/** Sets the minimum frame cycle time, in milliseconds. */
	public void setMinimumFrameCycleTime(int minimumTime) {
		prefs.putInt(MIN_FRAME_CYCLE_TIME, minimumTime);
	}
	private static final String MIN_FRAME_CYCLE_TIME = "MinimumFrameCycleTime";


	/**
	 * Returns <tt>true</tt> if each model computation is to
	 * be triggered by a timer (with constant step duration),
	 * or <tt>false</tt> if the model should be computed after
	 * each frame was rendered.
	 * @see #getTriggerStepDuration()
	 */
	public boolean useTimeTrigger() {
		return prefs.getBoolean(USE_TIME_TRIGGER, false);
	}
	private static final String USE_TIME_TRIGGER = "UseTimeTrigger";

	/**
	 * Returns the time in milliseconds between each computation
	 * step if <em>TimeTriggers</em> are used.
	 * @see #useTimeTrigger()
	 */
	public int getTriggerStepDuration() {
		return prefs.getInt(TIME_TRIGGER_STEP, 35);
	}
	private static final String TIME_TRIGGER_STEP = "TriggerStepDuration";


	/**
	 * Specifies if performance data should be collected.
	 */
	public boolean collectPerformanceData() {
		return prefs.getBoolean(COLLECT_PERFDATA, false);
	}
	private static final String COLLECT_PERFDATA = "CollectPerformanceData";



	/** Retrieves the main window's size. */
	public Dimension getMainFrameSize() {
		int w = prefs.getInt(MAIN_FRAME_WIDTH, 650);
		int h = prefs.getInt(MAIN_FRAME_HEIGHT, 550);
		if (w < 300) w = 300;
		if (h < 250) h = 250;
		return new Dimension(w, h);
	}
	/**
	 * Stores the main window's size.
	 * Note that this method will try to save the settings
	 * if the new size is different from the previous one.
	 */
	public void setMainFrameSize(Dimension size) {
		if (!getMainFrameSize().equals(size)) {
			prefs.putInt(MAIN_FRAME_WIDTH, size.width);
			prefs.putInt(MAIN_FRAME_HEIGHT, size.height);
			try { save(); } catch (Exception e) { System.err.println(e); }
		}
	}
	private static final String MAIN_FRAME_WIDTH = "MainFrameWidth";
	private static final String MAIN_FRAME_HEIGHT= "MainFrameHeight";


	/** Retrieves the main window's initial state. */
	public int getMainFrameExtendedState() {
		return prefs.getInt(MAIN_FRAME_STATE, Frame.NORMAL);
	}
	/** Stores the main window's initial state. */
	public void setMainFrameExtendedState(int state) {
		if (state != getMainFrameExtendedState()) {
			prefs.putInt(MAIN_FRAME_STATE, state);
			try { save(); } catch (Exception e) { System.err.println(e); }
		}
	}
	private static final String MAIN_FRAME_STATE = "MainFrameState";


	/**
	 * Retrieves the field of view used to compute the projection.
	 * Note that the field of view is internally stored in degrees,
	 * but returned in radians.
	 *
	 * @return the field of view, in radians.
	 */
	public double getFieldOfView() {
		return Math.toRadians(getFieldOfViewInt());
	}
	/**
	 * Retrieves the field of view used to compute the projection.
	 * @return the field of view, in degrees.
	 */
	private int getFieldOfViewInt() {
		int fov = prefs.getInt(FOV, 45);
		if (fov < MIN_FOV) fov = MIN_FOV;
		if (fov > MAX_FOV) fov = MAX_FOV;
		return fov;
	}
	private static final String FOV = "FieldOfView";
	private static int MIN_FOV = 30;
	private static int MAX_FOV = 180;




	/**
	 * Facility to edit the application configuration settings.
	 */
	public static class SettingsPanel extends JPanel {
		Settings settings;

		private JCheckBox showCollisionColor;
		private JCheckBox backgroundImageEnabled;
		private JCheckBox detailedShapes;
		private JSlider minimumFrameCycleTime;

		/**
		 * Creates a new settings panel for this application's
		 * main configuration settings object.
		 *
		 * @see Application#getSettings()
		 */
		public SettingsPanel() {
			settings = Application.getSettings();
			setLayout(new BorderLayout());

			JPanel boxes = new JPanel(new GridLayout(4,2));
			boxes.setBorder(BorderFactory.createTitledBorder("General Settings"));
			boxes.add(generateCheckBox(SHOW_COLLISION_SPHERES, settings.showCollisionSpheres()));
			boxes.add(generateCheckBox(SHOW_COLLISION_COLOR, settings.showCollisionColor()));
			boxes.add(generateCheckBox(BACKGROUND_IMAGE_ENABLED, settings.backgroundImageEnabled()));
			boxes.add(generateCheckBox(DETAILED_SHAPES, settings.detailedShapes()));
			boxes.add(generateCheckBox(LOAD_TEXTURES, settings.loadTextures()));
			boxes.add(generateCheckBox(RESET_CAMERA_ON_RESTART, settings.resetCamreaOnRestart()));
			boxes.add(generateCheckBox(USE_TIME_TRIGGER, settings.useTimeTrigger()));
			boxes.add(generateCheckBox(COLLECT_PERFDATA, settings.collectPerformanceData()));
			boxes.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
			cp.add(boxes);
			cp.add(generateSlider(TIME_TRIGGER_STEP, 10, 100, settings.getTriggerStepDuration(), 9, 18));
			cp.add(generateSlider(MIN_FRAME_CYCLE_TIME, 0, 40, settings.getMinimumFrameCycleTime()));
			cp.add(generateSlider(FOV, MIN_FOV, MAX_FOV, settings.getFieldOfViewInt(), 5, 30));
			add(cp, BorderLayout.CENTER);

			JButton save = new JButton("Save");
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						settings.save();
						fireClose();
					}
					catch(Exception ex) { System.err.println(ex); }
				}
			});

			JButton reset = new JButton("Use Defaults");
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						settings.reset();
						fireClose();
					}
					catch(Exception ex) {
						System.err.println(ex);
					}
				}
			});

			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttons.add(reset);
			buttons.add(save);
			add(buttons, BorderLayout.SOUTH);
		}

		private void fireClose() {
			// FIXME: find a better solution,
			//        this may close the wrong window
			Container c = getTopLevelAncestor();
			if (c != null) {
				c.setVisible(false);
			}
		}

		private JCheckBox generateCheckBox(String id, boolean value) {
			JCheckBox box = new JCheckBox(id, value);
			box.setName(id);
			box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object o = e.getSource();
					if (o instanceof JCheckBox) {
						JCheckBox box = (JCheckBox)o;
						String id = box.getName();
						if (id != null) {
							settings.prefs.putBoolean(id, box.isSelected());
						}
					}
				}
			});
			return box;
		}


		/**
		 * Factory method that creates a slider for a settings value.
		 * @see #generateSlider(String, int, int, int, int, int)
		 */
		private Component generateSlider(String id, int min, int max, int value) {
			return generateSlider(id, min, max, value, 5, 20);
		}


		/**
		 * Factory method that creates a slider for an <tt>int</tt> settings value.
		 * The slider is also labelled by putting it into <tt>JPanel</tt>
		 * that is fitted with a titled border.
		 *
		 * @param id identifies the preferences field.
		 * @param min lower bound of the allowed range.
		 * @param max upper bound of the allowed range.
		 * @param value the slidder's initial position.
		 * @param nTicksMajor number of major ticks.
		 * @param nTicksMinor number of minor ticks.
		 *
		 * @return a <tt>JPanel</tt> containing the new slider.
		 */
		private Component generateSlider(
			String id,
			int min,
			int max,
			int value,
			int nTicksMajor,
			int nTicksMinor)
		{
			JSlider slider = new JSlider(min, max, value);
			slider.setName(id);
			if (nTicksMajor > 0) slider.setMajorTickSpacing(((max-min)/nTicksMajor));
			if (nTicksMinor > 0) slider.setMinorTickSpacing(((max-min)/nTicksMinor));
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setToolTipText(String.valueOf(value));
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					Object o = e.getSource();
					if (o instanceof JSlider) {
						JSlider slider = (JSlider)o;
						String id = slider.getName();
						slider.setToolTipText(String.valueOf(slider.getValue()));
						if (id != null && ! slider.getValueIsAdjusting()) {
							settings.prefs.putInt(id, slider.getValue());
						}
					}
				}
			});
			JPanel pnl = new JPanel(new BorderLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(id));
			pnl.add(slider, BorderLayout.CENTER);
			pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
			return pnl;
		}
	}


	/**
	 * Creates a view settings dialog window.
	 */
	public static JDialog showDialog(Frame parent, String title) {
		JDialog dlg = new AboveDialog(parent, title, true);
		dlg.getContentPane().add(new Settings.SettingsPanel());
		dlg.setResizable(false);
		dlg.pack();
		dlg.show();
		return dlg;
	}
}