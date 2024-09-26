package com.pettonpc;

import javax.swing.JComboBox;
import net.runelite.client.config.ConfigManager;

public class DataManager {

	private ConfigManager configManager;

	public DataManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void saveConfiguration(String name, String key, int value) {
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, String.valueOf(value));
	}

	public String getSavedConfigNames() {
		return configManager.getConfiguration("petToNpcTransmog", "savedConfigNames");
	}

	public void setSavedConfigNames(String savedConfigNames) {
		configManager.setConfiguration("petToNpcTransmog", "savedConfigNames", savedConfigNames);
	}

	public void initializeConfigDropdown(JComboBox<String> configDropdown) {
		updateConfigDropdown(configDropdown);
	}

	public void updateConfigDropdown(JComboBox<String> configDropdown) {

		configDropdown.removeAllItems();
		String savedConfigNames = configManager.getConfiguration("petToNpcTransmog", "savedConfigNames");
		if (savedConfigNames != null && !savedConfigNames.isEmpty()) {
			String[] configNames = savedConfigNames.split(",");
			for (String configName : configNames) {
				configDropdown.addItem(configName);
			}
		}
	}


}
