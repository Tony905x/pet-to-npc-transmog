package com.pettonpc;

import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import java.awt.*;
import net.runelite.client.config.ConfigManager;

public class NpcFollowerPanel extends PluginPanel {
	private final NpcFollowerPlugin plugin;
	private final ConfigManager configManager;
	private final DataManager dataManager;
	private JComboBox<String> npcPresetDropdown;
	private JCheckBox enableCustomCheckbox;
	private JComboBox<String> configDropdown;
	private JTextField[] npcModelIDFields;
	private JTextField npcStandingAnim;
	private JTextField npcWalkingAnim;
	private JTextField npcSpawnAnim;
	private JSlider npcRadiusSlider;
	private JSlider npcXoffsetSlider;
	private JSlider npcYoffsetSlider;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton instructionsButton;
	private static final int DEFAULT_MODEL_ID = 0;
	private static final int DEFAULT_RADIUS = 120;
	private static final int DEFAULT_OFFSET = 0;
	private static final int DEFAULT_STANDING_ANIM = 0;
	private static final int DEFAULT_WALKING_ANIM = 0;
	private static final int DEFAULT_SPAWN_ANIM = 0;

	public NpcFollowerPanel(NpcFollowerPlugin plugin, ConfigManager configManager, DataManager dataManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.dataManager = dataManager;
		setLayout(new BorderLayout());

		// Create components
		JLabel titleLabel = new JLabel("<html><font color='orange'><u>Pet-To-NPC Transmog</u></font></html>");
		npcPresetDropdown = new JComboBox<>();
		npcPresetDropdown.setToolTipText("Select a preset NPC to transform your pet into.");
		enableCustomCheckbox = new JCheckBox();
		enableCustomCheckbox.setToolTipText("Enable custom NPC transformation.");
		configDropdown = new JComboBox<>();
		configDropdown.setToolTipText("Select a saved custom configuration.");
		npcModelIDFields = new JTextField[10];
		for (int i = 0; i < npcModelIDFields.length; i++) {
			npcModelIDFields[i] = new JTextField();
			npcModelIDFields[i].setToolTipText("Enter the model ID's for NPC. If only one Model ID is needed then the remaining ID fields can be left blank.");
		}
		npcStandingAnim = new JTextField();
		npcStandingAnim.setToolTipText("Enter the standing animation ID for the NPC.");
		npcWalkingAnim = new JTextField();
		npcWalkingAnim.setToolTipText("Enter the walking animation ID for the NPC.");
		npcSpawnAnim = new JTextField();
		npcSpawnAnim.setToolTipText("Enter the spawn animation ID for the NPC.");
		npcRadiusSlider = new JSlider(0, 5, 0);
		npcRadiusSlider.setToolTipText("Adjust the radius of the NPC.  Doesn't increase size, it prevents clipping for large NPC's");
		npcXoffsetSlider = new JSlider(0, 5, 0);
		npcXoffsetSlider.setToolTipText("Adjust the X offset of the NPC.");
		npcYoffsetSlider = new JSlider(0, 5, 0);
		npcYoffsetSlider.setToolTipText("Adjust the Y offset of the NPC.");
		saveButton = new JButton("Save Configuration");
		saveButton.setToolTipText("Save the current configuration.");
		deleteButton = new JButton("Delete Configuration");
		deleteButton.setToolTipText("Delete the selected configuration.");
		instructionsButton = new JButton("Instructions");
		instructionsButton.setToolTipText("View instructions for using the plugin.");


		// Create panels
		JPanel titlePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));

		// Add title to the panel with underline
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(instructionsButton, BorderLayout.SOUTH);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Constraints for NPC Presets Label
		gbc.insets = new Insets(5, 0, 20, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(new JLabel("<html><b>Presets:</b></html>"), gbc);
		gbc.gridx = 1;
		mainPanel.add(npcPresetDropdown, gbc);

		// Constraints for Enable Custom Configuration
		gbc.insets = new Insets(5, 0, 1, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("<html><b>Enable Custom:</b></html>"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(enableCustomCheckbox, gbc);

		// Constraints for Configuration Dropdown
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("<html><b>Saved Customs:</b></html>"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(configDropdown, gbc);

// Constraints for NPC Model ID 1
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("<html><b>NPC Model ID 1:</b></html>"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcModelIDFields[0], gbc);

		// Constraints for NPC Model ID fields 2 to 10
		for (int i = 1; i < npcModelIDFields.length; i++) {
			gbc.insets = new Insets(5, 0, 5, 20); // Set left inset to 10 for fields 2 to 10
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.weightx = 0.5;
			mainPanel.add(new JLabel("NPC Model ID " + (i + 1) + ":"), gbc);
			gbc.gridx = 1;
			gbc.weightx = 0.5;
			mainPanel.add(npcModelIDFields[i], gbc);
		}

		// Constraints for Standing Animation ID
		gbc.insets = new Insets(20, 0, 5, 0); // Set left inset to 10 for fields 2 to 10
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("<html><b>Standing ID:</b></html>"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcStandingAnim, gbc);

		// Constraints for Walking Animation ID
		gbc.insets = new Insets(5, 0, 30, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("<html><b>Walking ID:</b></html>"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcWalkingAnim, gbc);

		// Add "Optional" label above the Animation ID field
		JLabel optionalLabel = new JLabel("<html><font color='orange'><u>Optional Changes</u></font></html>");
		optionalLabel.setFont(new Font("Serif", Font.BOLD, 11));

		gbc.insets = new Insets(5, 0, 1, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(optionalLabel, gbc);

		// Constraints for Spawn Animation ID
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("Animation ID:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcSpawnAnim, gbc);

		// Constraints for NPC Radius
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("Radius:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcRadiusSlider, gbc);

		// Constraints for X Offset
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("X Offset:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcXoffsetSlider, gbc);

		// Constraints for Y Offset
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.5;
		mainPanel.add(new JLabel("Y Offset:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		mainPanel.add(npcYoffsetSlider, gbc);

		// Add buttons to button panel
		buttonPanel.add(saveButton);
		buttonPanel.add(deleteButton);

		// Add panels to main panel
		add(titlePanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Populate the NPC preset dropdown
		populateNpcPresetDropdown();

		// Handle preset dropdown
		npcPresetDropdown.addActionListener(e -> {
			plugin.panelChange();
			saveCurrentConfiguration();
		});

		enableCustomCheckbox.addActionListener(e -> {
			plugin.panelChange();
			toggleCustomFields(enableCustomCheckbox.isSelected());
			saveCurrentConfiguration();
		});

		// Handle saving values
		saveButton.addActionListener(e -> {

			// Show input dialog to get the configuration name
			String configName = JOptionPane.showInputDialog(null, "Enter configuration name:", "Save Configuration", JOptionPane.PLAIN_MESSAGE);

			if (configName == null || configName.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please enter a name for the configuration before saving.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			plugin.saveConfiguration(
				configName,
				npcModelIDFields[0].getText(),
				npcModelIDFields[1].getText(),
				npcModelIDFields[2].getText(),
				npcModelIDFields[3].getText(),
				npcModelIDFields[4].getText(),
				npcModelIDFields[5].getText(),
				npcModelIDFields[6].getText(),
				npcModelIDFields[7].getText(),
				npcModelIDFields[8].getText(),
				npcModelIDFields[9].getText(),
				npcStandingAnim.getText(),
				npcWalkingAnim.getText(),
				npcSpawnAnim.getText(),
				String.valueOf(getModelRadius()), // Save slider value as String
				String.valueOf(npcXoffsetSlider.getValue()), // Save slider value as String
				String.valueOf(npcYoffsetSlider.getValue())  // Save slider value as String
			);
			dataManager.updateConfigDropdown(configDropdown);

			// Set the newly saved configuration as the selected item
			configDropdown.setSelectedItem(configName);

			// Load the newly saved configuration
			plugin.loadConfiguration(configName, npcModelIDFields);
			plugin.loadSliderConfiguration(configName, npcRadiusSlider, npcXoffsetSlider, npcYoffsetSlider);
			plugin.panelChange();
		});

		// Handle saving values
		instructionsButton.addActionListener(e -> showInstructionsDialog());

		// Handle deleting values
		deleteButton.addActionListener(e -> {
			String selectedConfig = (String) configDropdown.getSelectedItem();
			if (selectedConfig != null && !selectedConfig.isEmpty()) {
				plugin.deleteConfiguration(selectedConfig);
				dataManager.updateConfigDropdown(configDropdown);
			}
		});

		// Add ActionListener to configDropdown
		configDropdown.addActionListener(e -> {
			String selectedConfig = (String) configDropdown.getSelectedItem();
			if (selectedConfig != null && plugin != null && plugin.transmogInitialized) {
				plugin.loadConfiguration(selectedConfig, npcModelIDFields);
				plugin.loadSliderConfiguration(selectedConfig, npcRadiusSlider, npcXoffsetSlider, npcYoffsetSlider);
				plugin.panelChange();
				saveCurrentConfiguration();
			}
		});

		// Initialize dropdown
		initializeConfigDropdown();

		// Set the initial state of custom fields based on the checkbox
		toggleCustomFields(enableCustomCheckbox.isSelected());
	}

	private void addLabelAndField(JPanel panel, String labelText, JComponent field) {
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		panel.add(label);
		panel.add(field);
	}

	// Initialize config dropdown
	private void initializeConfigDropdown() {
		if (dataManager != null) {
			dataManager.initializeConfigDropdown(configDropdown);
		}
	}

	public void saveCurrentConfiguration() {
		String selectedConfig = enableCustomCheckbox.isSelected() ? (String) configDropdown.getSelectedItem() : (String) npcPresetDropdown.getSelectedItem();
		boolean isCustomEnabled = enableCustomCheckbox.isSelected();

		configManager.setConfiguration("petToNpcTransmog", "lastSelectedConfig", selectedConfig);
		configManager.setConfiguration("petToNpcTransmog", "isCustomEnabled", String.valueOf(isCustomEnabled));
	}

	public void loadLastConfiguration() {
		String lastSelectedConfig = configManager.getConfiguration("petToNpcTransmog", "lastSelectedConfig");
		boolean isCustomEnabled = Boolean.parseBoolean(configManager.getConfiguration("petToNpcTransmog", "isCustomEnabled"));



		enableCustomCheckbox.setSelected(isCustomEnabled);
		if (isCustomEnabled) {
			configDropdown.setSelectedItem(lastSelectedConfig);
			plugin.loadConfiguration(lastSelectedConfig, npcModelIDFields);
			plugin.loadSliderConfiguration(lastSelectedConfig, npcRadiusSlider, npcXoffsetSlider, npcYoffsetSlider);
			toggleCustomFields(enableCustomCheckbox.isSelected());

			// Debugging output for custom fields
			for (int i = 0; i < npcModelIDFields.length; i++) {
			}
		} else {
			npcPresetDropdown.setSelectedItem(lastSelectedConfig);
		}
		plugin.panelChange();
	}

	private void populateNpcPresetDropdown() {
		for (NpcData npcData : NpcData.values()) {
			npcPresetDropdown.addItem(npcData.getName());
		}
	}

	public NpcData getSelectedNpc() {
		String selectedNpcName = (String) npcPresetDropdown.getSelectedItem();
		for (NpcData npcData : NpcData.values()) {
			if (npcData.getName().equals(selectedNpcName)) {
				return npcData;
			}
		}
		return null; // or a default value
	}

	public boolean enableCustom() {
		return enableCustomCheckbox.isSelected();
	}

	public int getModelRadius() {
		// Each slider step represents an increment of 60
		return (npcRadiusSlider.getValue() + 1) * 60;
	}

	public int getOffsetX() {
		return npcXoffsetSlider.getValue();
	}

	public int getOffsetY() {
		return npcYoffsetSlider.getValue();
	}

	public int getNpcModelID1() {
		return parseIntegerField(npcModelIDFields[0], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID2() {
		return parseIntegerField(npcModelIDFields[1], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID3() {
		return parseIntegerField(npcModelIDFields[2], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID4() {
		return parseIntegerField(npcModelIDFields[3], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID5() {
		return parseIntegerField(npcModelIDFields[4], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID6() {
		return parseIntegerField(npcModelIDFields[5], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID7() {
		return parseIntegerField(npcModelIDFields[6], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID8() {
		return parseIntegerField(npcModelIDFields[7], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID9() {
		return parseIntegerField(npcModelIDFields[8], DEFAULT_MODEL_ID);
	}

	public int getNpcModelID10() {
		return parseIntegerField(npcModelIDFields[9], DEFAULT_MODEL_ID);
	}

	public int getStandingAnimationId() {
		return parseIntegerField(npcStandingAnim, DEFAULT_STANDING_ANIM);
	}

	public int getWalkingAnimationId() {
		return parseIntegerField(npcWalkingAnim, DEFAULT_WALKING_ANIM);
	}

	public int getSpawnAnimationID() {
		return parseIntegerField(npcSpawnAnim, DEFAULT_SPAWN_ANIM);
	}

	private int parseIntegerField(JTextField field, int defaultValue) {
		String text = field.getText();
		if (text == null || text.isEmpty()) {
			return defaultValue;
		}
		return Integer.parseInt(text);
	}

	public JComboBox<String> getConfigDropdown() {
		return configDropdown;
	}

	public void updateFieldsWithNpcData(NpcData npcData) {
		npcModelIDFields[0].setText(String.valueOf(npcData.getModelIDs().get(0)));
		npcModelIDFields[1].setText(npcData.getModelIDs().size() > 1 ? String.valueOf(npcData.getModelIDs().get(1)) : "");
		npcModelIDFields[2].setText(npcData.getModelIDs().size() > 2 ? String.valueOf(npcData.getModelIDs().get(2)) : "");
		npcModelIDFields[3].setText(npcData.getModelIDs().size() > 3 ? String.valueOf(npcData.getModelIDs().get(3)) : "");
		npcModelIDFields[4].setText(npcData.getModelIDs().size() > 4 ? String.valueOf(npcData.getModelIDs().get(4)) : "");
		npcModelIDFields[5].setText(npcData.getModelIDs().size() > 5 ? String.valueOf(npcData.getModelIDs().get(5)) : "");
		npcModelIDFields[6].setText(npcData.getModelIDs().size() > 6 ? String.valueOf(npcData.getModelIDs().get(6)) : "");
		npcModelIDFields[7].setText(npcData.getModelIDs().size() > 7 ? String.valueOf(npcData.getModelIDs().get(7)) : "");
		npcModelIDFields[8].setText(npcData.getModelIDs().size() > 8 ? String.valueOf(npcData.getModelIDs().get(8)) : "");
		npcModelIDFields[9].setText(npcData.getModelIDs().size() > 9 ? String.valueOf(npcData.getModelIDs().get(9)) : "");
		npcStandingAnim.setText(String.valueOf(npcData.getStandingAnim()));
		npcWalkingAnim.setText(String.valueOf(npcData.getWalkAnim()));
		npcSpawnAnim.setText(String.valueOf(npcData.getSpawnAnim()));

		// Adjust the radius slider value
		int radius = npcData.getRadius();
		int sliderValue = (radius / 60) - 1;
		npcRadiusSlider.setValue(sliderValue);

		npcXoffsetSlider.setValue(npcData.getOffsetX());
		npcYoffsetSlider.setValue(npcData.getOffsetY());
	}


	public void updateFieldsWithDropdownData(String configName) {
		npcModelIDFields[0].setText(plugin.loadConfiguration(configName, "npcModelID1"));
		npcModelIDFields[1].setText(plugin.loadConfiguration(configName, "npcModelID2"));
		npcModelIDFields[2].setText(plugin.loadConfiguration(configName, "npcModelID3"));
		npcModelIDFields[3].setText(plugin.loadConfiguration(configName, "npcModelID4"));
		npcModelIDFields[4].setText(plugin.loadConfiguration(configName, "npcModelID5"));
		npcModelIDFields[5].setText(plugin.loadConfiguration(configName, "npcModelID6"));
		npcModelIDFields[6].setText(plugin.loadConfiguration(configName, "npcModelID7"));
		npcModelIDFields[7].setText(plugin.loadConfiguration(configName, "npcModelID8"));
		npcModelIDFields[8].setText(plugin.loadConfiguration(configName, "npcModelID9"));
		npcModelIDFields[9].setText(plugin.loadConfiguration(configName, "npcModelID10"));
		npcStandingAnim.setText(plugin.loadConfiguration(configName, "npcStandingAnim"));
		npcWalkingAnim.setText(plugin.loadConfiguration(configName, "npcWalkingAnim"));
		npcSpawnAnim.setText(plugin.loadConfiguration(configName, "npcSpawnAnim"));

		// Load and set the radius value
		String radiusValue = plugin.loadConfiguration(configName, "npcRadius");
		if (radiusValue != null) {
			int radius = Integer.parseInt(radiusValue);
			int sliderValue = (radius / 60) - 1;
			npcRadiusSlider.setValue(sliderValue);
		} else {
			npcRadiusSlider.setValue(0);
		}

		npcXoffsetSlider.setValue(Integer.parseInt(plugin.loadConfiguration(configName, "npcXoffset")));
		npcYoffsetSlider.setValue(Integer.parseInt(plugin.loadConfiguration(configName, "npcYoffset")));
	}

	public void toggleCustomFields(boolean enable) {
		npcPresetDropdown.setEnabled(!enable);
		configDropdown.setEnabled(enable);
		for (JTextField field : npcModelIDFields) {
			field.setEnabled(enable);
		}
		npcStandingAnim.setEnabled(enable);
		npcWalkingAnim.setEnabled(enable);
		npcSpawnAnim.setEnabled(enable);
		npcRadiusSlider.setEnabled(enable);
		npcXoffsetSlider.setEnabled(enable);
		npcYoffsetSlider.setEnabled(enable);
		saveButton.setEnabled(enable);
		deleteButton.setEnabled(enable);
	}

	private void showInstructionsDialog() {
		String instructionsText = "<html>" +
			"<style>" +
			"ul { padding-left: 0; margin-left: 0; list-style-position: inside; }" +
			"li { margin-left: 0; }" +
			"</style>" +
			"<center><b><u><font color='orange'>Alter Your Pets Appearance To Any NPC/Monster</font></u></b></center><br>" +
			"<b>1)</b> Have any pet out, if you don't have a pet then a cat or a kitten will work<br><br>" +
			"<b>2)</b> For preset NPC's simply select the NPC from the 'NPC Presets' dropdown and you are done<br><br>" +
			"<b>3)</b> To use a different NPC click the 'Enable Custom' checkbox and follow the below steps<br><br>" +
			"<b>4)</b> <font color='orange'>https://runemonk.com/tools/entityviewer/</font> can give you model ID's. This is the site I personally use " +
			"but there are alternatives such as <font color='orange'>https://chisel.weirdgloop.org/moid/npc_name.html</font><br><br>" +
			"<b>5)</b> Search for the NPC you want to use<br><br>" +
			"<b>6)</b> Click on the Data Tab<br><br>" +
			"<b>7)</b> Use the ID under 'models:' (Not the first one listed under 'id:' which is a npcID not a modelID)<br>" +
			"Paste it into the plugin panel NPC ModelID1 field<br><br>" +
			"<b>8)</b> Some models combine multiple ID's in which case you will have to put the other model ID's in the panels other modelID fields" +
			"If you look at the preset NPC's values in the plugin panel it will give you an idea of the proper way to fill it out<br><br>" +
			"<b>9)</b> Use the standingAnimation ID and the walkingAnimation ID as well and put them in the panel<br><br>" +
			"<b>10)</b> Click Save and type a name you wish to save it under.  In order for the new model to be updated you will have to save.<br><br>" +
			"<b>EXTRA INFO</b><br>" +
			"<ul>" +
			"<li>The NPC transmog is only seen on your end. Others will still see your actual pet</li>" +
			"<li>Large models will clip due to their size. Increasing the 'radius' in the panel will prevent this</li>" +
			"<li>If a large model is too close to you, increase the X Offset or the Y Offset to put more distance between you and the NPC</li>" +
			"<li>Spawn ID can be used to try out animations for the NPC. Animation ID's can be found in the 'Model' tab instead of the 'data' tab on RuneMonk</li>" +
			"<li>For now turn the plugin off if you need to pick up your pet until a future update</li>" +
			"</ul>" +
			"</html>";

		JTextPane instructions = new JTextPane();
		instructions.setContentType("text/html");
		instructions.setText(instructionsText);
		instructions.setEditable(false);
		instructions.setBorder(new EmptyBorder(10, 10, 10, 10));
		instructions.setPreferredSize(new Dimension(600, 650));

		JOptionPane.showMessageDialog(null, instructions, "Instructions", JOptionPane.PLAIN_MESSAGE);
	}
}

