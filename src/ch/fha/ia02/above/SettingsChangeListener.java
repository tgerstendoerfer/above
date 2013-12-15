package ch.fha.ia02.above;

/**
 * A listener for receiving notifications about changes to global
 * configuration settings.
 *
 * @author Thomas Gerstendoerfer
 *
 * @see Settings
 */
public interface SettingsChangeListener
{
	/**
	 * This method gets called after a configuration setting was modified.
	 *
	 * @param settings the settings object on which the change happened.
	 */
	public void settingsChange(Settings settings);
}