package com.pettonpc;

import javax.swing.JComboBox;
import net.runelite.client.config.ConfigManager;



public class DataManager
{

//	@Inject
	//
	private ConfigManager configManager;


	public DataManager(ConfigManager configManager) {
		this.configManager = configManager;
	}




	public void saveConfiguration(String name, String key, int value) {
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, String.valueOf(value));
	}

	public void saveMultipleConfigurations(String name, String... npcModelIDs) {
		for (int i = 0; i < npcModelIDs.length; i++) {
			if (npcModelIDs[i] != null && !npcModelIDs[i].isEmpty()) {
				saveConfiguration(name, "npcModelID" + (i + 1), Integer.parseInt(npcModelIDs[i]));
			}
		}
	}

	public String loadConfiguration(String name, String key) {
		return configManager.getConfiguration("petToNpcTransmog", name + "_" + key);
	}

	public void deleteConfiguration(String name) {
		for (int i = 1; i <= 10; i++) {
			configManager.unsetConfiguration("petToNpcTransmog", name + "_npcModelID" + i);
		}
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcStandingAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcWalkingAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcSpawnAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcRadius");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcXoffset");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcYoffset");
	}

	public String getSavedConfigNames()
	{
//		if (panel == null) {
//			return "";
//		}
		return configManager.getConfiguration("petToNpcTransmog", "savedConfigNames");
	}

	public void setSavedConfigNames(String savedConfigNames)
	{
		configManager.setConfiguration("petToNpcTransmog", "savedConfigNames", savedConfigNames);
	}

	public void initializeConfigDropdown(JComboBox<String> configDropdown) {
		updateConfigDropdown(configDropdown);
	}

	public void updateConfigDropdown(JComboBox<String> configDropdown) {
		System.out.println("DataManager updateConfigDropdown");

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
