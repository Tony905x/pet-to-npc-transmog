package com.pettonpc;

import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.ImageUtil;

public class NpcFollowerPanel extends PluginPanel
{
	private static final int DEFAULT_MODEL_ID = 0;
	private static final int DEFAULT_RADIUS = 120;
	private static final int DEFAULT_OFFSET = 0;
	private static final int DEFAULT_STANDING_ANIM = 11473;
	private static final int DEFAULT_WALKING_ANIM = 11474;
	private static final int DEFAULT_SPAWN_ANIM = 0;

	private final NpcFollowerPlugin plugin;
	private final ConfigManager configManager;
	private JComboBox<String> npcPresetDropdown;
	private JCheckBox enableCustomCheckbox;
	private JComboBox<String> configDropdown;
	private JTextField npcModelID1Field;
	private JTextField npcModelID2Field;
	private JTextField npcModelID3Field;
	private JTextField npcModelID4Field;
	private JTextField npcModelID5Field;
	private JTextField npcModelID6Field;
	private JTextField npcModelID7Field;
	private JTextField npcModelID8Field;
	private JTextField npcModelID9Field;
	private JTextField npcModelID10Field;
	private JTextField npcStandingAnim;
	private JTextField npcWalkingAnim;
	private JTextField npcSpawnAnim;
//	private JTextField npcRadius;
	private JSlider npcRadiusSlider;
	private JSlider npcXoffsetSlider;
	private JSlider npcYoffsetSlider;
//	private JTextField configNameField;
	private JButton saveButton;
	private JButton deleteButton;

	public NpcFollowerPanel(NpcFollowerPlugin plugin, ConfigManager configManager)
	{
		this.plugin = plugin;
		this.configManager = configManager;
		setLayout(new BorderLayout());

		// Create components
		npcPresetDropdown = new JComboBox<>();
		enableCustomCheckbox = new JCheckBox();
		configDropdown = new JComboBox<>();
		npcModelID1Field = new JTextField();
		npcModelID2Field = new JTextField();
		npcModelID3Field = new JTextField();
		npcModelID4Field = new JTextField();
		npcModelID5Field = new JTextField();
		npcModelID6Field = new JTextField();
		npcModelID7Field = new JTextField();
		npcModelID8Field = new JTextField();
		npcModelID9Field = new JTextField();
		npcModelID10Field = new JTextField();
		npcStandingAnim = new JTextField();
		npcWalkingAnim = new JTextField();
		npcSpawnAnim = new JTextField();
//		npcRadius = new JTextField();
		npcRadiusSlider = new JSlider(0, 5, 0); // Min value: 0, Max value: 5, Initial value: 0
		npcXoffsetSlider = new JSlider(0, 5, 0); // Min value: 0, Max value: 5, Initial value: 0
		npcYoffsetSlider = new JSlider(0, 5, 0); // Min value: 0, Max value: 5, Initial value: 0
//		configNameField = new JTextField();
		saveButton = new JButton("Save Configuration");
		deleteButton = new JButton("Delete Configuration");

		// Add change listeners to update the values when the slider is moved
		npcRadiusSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("Radius " + npcRadiusSlider.getValue());
			}
		});


		npcXoffsetSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("X Offset: " + npcXoffsetSlider.getValue());
			}
		});

		npcYoffsetSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("Y Offset: " + npcYoffsetSlider.getValue());
			}
		});

		// Create panels
		JPanel mainPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		mainPanel.setBorder(new EmptyBorder(10, 10, 30, 10));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 5, 5)); // Only one button now

		// Add title to the panel with underline
		JLabel titleLabel = new JLabel("<html><u>Pet-To-NPC Transmog</u></html>");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial Narrow", Font.BOLD, 16));
		add(titleLabel, BorderLayout.NORTH);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add gap above and below the title


		// Add components to main panel
		addLabelAndField(mainPanel, "NPC Presets:", npcPresetDropdown);
		addLabelAndField(mainPanel, "Enable Custom Configuration:", enableCustomCheckbox);
		addLabelAndField(mainPanel, "Configuration Dropdown:", configDropdown);
		addLabelAndField(mainPanel, "NPC Model ID 1:", npcModelID1Field);
		addLabelAndField(mainPanel, "NPC Model ID 2:", npcModelID2Field);
		addLabelAndField(mainPanel, "NPC Model ID 3:", npcModelID3Field);
		addLabelAndField(mainPanel, "NPC Model ID 4:", npcModelID4Field);
		addLabelAndField(mainPanel, "NPC Model ID 5:", npcModelID5Field);
		addLabelAndField(mainPanel, "NPC Model ID 6:", npcModelID6Field);
		addLabelAndField(mainPanel, "NPC Model ID 7:", npcModelID7Field);
		addLabelAndField(mainPanel, "NPC Model ID 8:", npcModelID8Field);
		addLabelAndField(mainPanel, "NPC Model ID 9:", npcModelID9Field);
		addLabelAndField(mainPanel, "NPC Model ID 10:", npcModelID10Field);
		addLabelAndField(mainPanel, "Standing Animation ID:", npcStandingAnim);
		addLabelAndField(mainPanel, "Walking Animation ID:", npcWalkingAnim);
		addLabelAndField(mainPanel, "Spawn Animation ID:", npcSpawnAnim);
//		addLabelAndField(mainPanel, "NPC Radius:", npcRadius);
		addLabelAndField(mainPanel, "NPC Radius:", npcRadiusSlider);
		addLabelAndField(mainPanel, "X Offset:", npcXoffsetSlider);
		addLabelAndField(mainPanel, "Y Offset:", npcYoffsetSlider);
//		addLabelAndField(mainPanel, "Configuration Name:", configNameField);
//		configNameField.setToolTipText("Enter configuration name to be saved under");

		// Add buttons to button panel
		buttonPanel.add(saveButton);
		buttonPanel.add(deleteButton);

		// Add panels to main panel
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Populate the NPC preset dropdown
		populateNpcPresetDropdown();

		// Handle preset dropdown
		npcPresetDropdown.addActionListener(e -> {
			System.out.println("preset dropdown listener");
			plugin.onConfigChanged();
		});

		enableCustomCheckbox.addActionListener(e -> {
			System.out.println("custom button listener");
			plugin.onConfigChanged();
			toggleCustomFields(enableCustomCheckbox.isSelected());
		});

		// Handle saving values
		saveButton.addActionListener(e -> {
			System.out.println("entered saving method");

			// Show input dialog to get the configuration name
			String configName = JOptionPane.showInputDialog(null, "Enter configuration name:", "Save Configuration", JOptionPane.PLAIN_MESSAGE);

			if (configName == null || configName.isEmpty())
			{
				JOptionPane.showMessageDialog(null, "Please enter a name for the configuration before saving.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			System.out.println("Saving fields!");
			plugin.saveConfiguration(
				configName,
				npcModelID1Field.getText(),
				npcModelID2Field.getText(),
				npcModelID3Field.getText(),
				npcModelID4Field.getText(),
				npcModelID5Field.getText(),
				npcModelID6Field.getText(),
				npcModelID7Field.getText(),
				npcModelID8Field.getText(),
				npcModelID9Field.getText(),
				npcModelID10Field.getText(),
				npcStandingAnim.getText(),
				npcWalkingAnim.getText(),
				npcSpawnAnim.getText(),
				String.valueOf(getModelRadius()), // Save slider value as String
				String.valueOf(npcXoffsetSlider.getValue()), // Save slider value as String
				String.valueOf(npcYoffsetSlider.getValue())  // Save slider value as String
			);
			System.out.println("updating the config dropdown from save button");
			updateConfigDropdown(configDropdown);

			// Set the newly saved configuration as the selected item
			configDropdown.setSelectedItem(configName);

			// Load the newly saved configuration
			plugin.loadConfiguration(configName, npcModelID1Field, npcModelID2Field, npcModelID3Field, npcModelID4Field, npcModelID5Field, npcModelID6Field, npcModelID7Field, npcModelID8Field, npcModelID9Field, npcModelID10Field);
			plugin.loadSliderConfiguration(configName, npcRadiusSlider, npcXoffsetSlider, npcYoffsetSlider);
			plugin.onConfigChanged();
		});


		// Handle deleting values
		deleteButton.addActionListener(e -> {
			String selectedConfig = (String) configDropdown.getSelectedItem();
			if (selectedConfig != null && !selectedConfig.isEmpty())
			{
				System.out.println("Deleting configuration: " + selectedConfig);
				plugin.deleteConfiguration(selectedConfig);
				updateConfigDropdown(configDropdown);
			}
		});

		// Add ActionListener to configDropdown
		configDropdown.addActionListener(e -> {
			String selectedConfig = (String) configDropdown.getSelectedItem();
			if (selectedConfig != null && plugin != null)
			{
				System.out.println("in the dropdown action method");
				plugin.loadConfiguration(
					selectedConfig,
					npcModelID1Field,
					npcModelID2Field,
					npcModelID3Field,
					npcModelID4Field,
					npcModelID5Field,
					npcModelID6Field,
					npcModelID7Field,
					npcModelID8Field,
					npcModelID9Field,
					npcModelID10Field,
					npcStandingAnim,
					npcWalkingAnim,
					npcSpawnAnim
//					npcRadius
				);
				plugin.loadSliderConfiguration(
					selectedConfig,
					npcRadiusSlider,
					npcXoffsetSlider,
					npcYoffsetSlider
				);
				plugin.onConfigChanged();
			}
		});

		// Initialize dropdown
		initializeConfigDropdown();

		// Set the initial state of custom fields based on the checkbox
		toggleCustomFields(enableCustomCheckbox.isSelected());
	}

	private void addLabelAndField(JPanel panel, String labelText, JComponent field)
	{
		panel.add(new JLabel(labelText));
		panel.add(field);
	}

	private void initializeConfigDropdown()
	{
		updateConfigDropdown(configDropdown);
	}

	public void updateConfigDropdown(JComboBox<String> configDropdown)
	{
		configDropdown.removeAllItems();
		String savedConfigNames = plugin.getSavedConfigNames();
		if (savedConfigNames != null && !savedConfigNames.isEmpty())
		{
			String[] configNames = savedConfigNames.split(",");
			for (String configName : configNames)
			{
				configDropdown.addItem(configName);
			}
		}
	}

	private void populateNpcPresetDropdown()
	{
		for (NpcData npcData : NpcData.values())
		{
			npcPresetDropdown.addItem(npcData.getName());
		}
	}

	public NpcData getSelectedNpc()
	{
		String selectedNpcName = (String) npcPresetDropdown.getSelectedItem();
		for (NpcData npcData : NpcData.values())
		{
			if (npcData.getName().equals(selectedNpcName))
			{
				return npcData;
			}
		}
		return null; // or a default value
	}

	public boolean enableCustom()
	{
		return enableCustomCheckbox.isSelected();
	}

	public int getModelRadius()
	{
		// Each slider step represents an increment of 60
		return (npcRadiusSlider.getValue() + 1) * 60;
	}

	public int getOffsetX()
	{
		return npcXoffsetSlider.getValue();
	}

	public int getOffsetY()
	{
		return npcYoffsetSlider.getValue();
	}

	public int getNpcModelID1()
	{
		return parseIntegerField(npcModelID1Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID2()
	{
		return parseIntegerField(npcModelID2Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID3()
	{
		return parseIntegerField(npcModelID3Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID4()
	{
		return parseIntegerField(npcModelID4Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID5()
	{
		return parseIntegerField(npcModelID5Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID6()
	{
		return parseIntegerField(npcModelID6Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID7()
	{
		return parseIntegerField(npcModelID7Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID8()
	{
		return parseIntegerField(npcModelID8Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID9()
	{
		return parseIntegerField(npcModelID9Field, DEFAULT_MODEL_ID);
	}

	public int getNpcModelID10()
	{
		return parseIntegerField(npcModelID10Field, DEFAULT_MODEL_ID);
	}

	public int getStandingAnimationId()
	{
		return parseIntegerField(npcStandingAnim, DEFAULT_STANDING_ANIM);
	}

	public int getWalkingAnimationId()
	{
		return parseIntegerField(npcWalkingAnim, DEFAULT_WALKING_ANIM);
	}

	public int getSpawnAnimationID()
	{
		return parseIntegerField(npcSpawnAnim, DEFAULT_SPAWN_ANIM);
	}

	private int parseIntegerField(JTextField field, int defaultValue)
	{
		String text = field.getText();
		if (text == null || text.isEmpty())
		{
			return defaultValue;
		}
		return Integer.parseInt(text);
	}

	public void saveConfiguration(String name, String key, int value)
	{
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, String.valueOf(value));
	}

	public void saveConfiguration(String name, String key, String value)
	{
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, value);
	}

	public String loadConfiguration(String name, int index)
	{
		return configManager.getConfiguration("petToNpcTransmog", name + "_npcModelID" + index);
	}

	public String loadConfiguration(String name, String key)
	{
		return configManager.getConfiguration("petToNpcTransmog", name + "_" + key);
	}

	public String getSavedConfigNames()
	{
		return configManager.getConfiguration("petToNpcTransmog", "savedConfigNames");
	}

	public void setSavedConfigNames(String savedConfigNames)
	{
		configManager.setConfiguration("petToNpcTransmog", "savedConfigNames", savedConfigNames);
	}

	public JComboBox<String> getConfigDropdown()
	{
		return configDropdown;
	}

	public void updateFieldsWithNpcData(NpcData npcData)
	{
		npcModelID1Field.setText(String.valueOf(npcData.getModelIDs().get(0)));
		npcModelID2Field.setText(npcData.getModelIDs().size() > 1 ? String.valueOf(npcData.getModelIDs().get(1)) : "");
		npcModelID3Field.setText(npcData.getModelIDs().size() > 2 ? String.valueOf(npcData.getModelIDs().get(2)) : "");
		npcModelID4Field.setText(npcData.getModelIDs().size() > 3 ? String.valueOf(npcData.getModelIDs().get(3)) : "");
		npcModelID5Field.setText(npcData.getModelIDs().size() > 4 ? String.valueOf(npcData.getModelIDs().get(4)) : "");
		npcModelID6Field.setText(npcData.getModelIDs().size() > 5 ? String.valueOf(npcData.getModelIDs().get(5)) : "");
		npcModelID7Field.setText(npcData.getModelIDs().size() > 6 ? String.valueOf(npcData.getModelIDs().get(6)) : "");
		npcModelID8Field.setText(npcData.getModelIDs().size() > 7 ? String.valueOf(npcData.getModelIDs().get(7)) : "");
		npcModelID9Field.setText(npcData.getModelIDs().size() > 8 ? String.valueOf(npcData.getModelIDs().get(8)) : "");
		npcModelID10Field.setText(npcData.getModelIDs().size() > 9 ? String.valueOf(npcData.getModelIDs().get(9)) : "");
		npcStandingAnim.setText(String.valueOf(npcData.getStandingAnim()));
		npcWalkingAnim.setText(String.valueOf(npcData.getWalkAnim()));
		npcSpawnAnim.setText(String.valueOf(npcData.getSpawnAnim()));
//		npcRadius.setText(String.valueOf(npcData.getRadius()));
		npcRadiusSlider.setValue(npcData.getRadius());
		npcXoffsetSlider.setValue(npcData.getOffsetX());
		npcYoffsetSlider.setValue(npcData.getOffsetY());
	}

	public void updateFieldsWithDropdownData(String configName)
	{
		npcModelID1Field.setText(loadConfiguration(configName, "npcModelID1"));
		npcModelID2Field.setText(loadConfiguration(configName, "npcModelID2"));
		npcModelID3Field.setText(loadConfiguration(configName, "npcModelID3"));
		npcModelID4Field.setText(loadConfiguration(configName, "npcModelID4"));
		npcModelID5Field.setText(loadConfiguration(configName, "npcModelID5"));
		npcModelID6Field.setText(loadConfiguration(configName, "npcModelID6"));
		npcModelID7Field.setText(loadConfiguration(configName, "npcModelID7"));
		npcModelID8Field.setText(loadConfiguration(configName, "npcModelID8"));
		npcModelID9Field.setText(loadConfiguration(configName, "npcModelID9"));
		npcModelID10Field.setText(loadConfiguration(configName, "npcModelID10"));
		npcStandingAnim.setText(loadConfiguration(configName, "npcStandingAnim"));
		npcWalkingAnim.setText(loadConfiguration(configName, "npcWalkingAnim"));
		npcSpawnAnim.setText(loadConfiguration(configName, "npcSpawnAnim"));
//		npcRadius.setText(loadConfiguration(configName, "npcRadius"));
//		npcRadiusSlider.setValue(Integer.parseInt(loadConfiguration(configName, "npcRadius")));

		// Load and set the radius value
		String radiusValue = loadConfiguration(configName, "npcRadius");
		if (radiusValue != null)
		{
			int radius = Integer.parseInt(radiusValue);
			int sliderValue = (radius / 60) - 1;
			npcRadiusSlider.setValue(sliderValue);
		}
		else
		{
			npcRadiusSlider.setValue(0); // Set a default value if the configuration value is null
		}

		npcXoffsetSlider.setValue(Integer.parseInt(loadConfiguration(configName, "npcXoffset")));
		npcYoffsetSlider.setValue(Integer.parseInt(loadConfiguration(configName, "npcYoffset")));

		// Debugging
		System.out.println("Loaded Configuration: " + configName);
		System.out.println("Standing Anim: " + npcStandingAnim.getText());
		System.out.println("Walking Anim: " + npcWalkingAnim.getText());
		System.out.println("Spawn Anim: " + npcSpawnAnim.getText());
		System.out.println("Radius: " + npcRadiusSlider.getValue());
		System.out.println("X Offset: " + npcXoffsetSlider.getValue());
		System.out.println("Y Offset: " + npcYoffsetSlider.getValue());
	}

	public void toggleCustomFields(boolean enable)
	{
		npcPresetDropdown.setEnabled(!enable);
		configDropdown.setEnabled(enable);
		npcModelID1Field.setEnabled(enable);
		npcModelID2Field.setEnabled(enable);
		npcModelID3Field.setEnabled(enable);
		npcModelID4Field.setEnabled(enable);
		npcModelID5Field.setEnabled(enable);
		npcModelID6Field.setEnabled(enable);
		npcModelID7Field.setEnabled(enable);
		npcModelID8Field.setEnabled(enable);
		npcModelID9Field.setEnabled(enable);
		npcModelID10Field.setEnabled(enable);
		npcStandingAnim.setEnabled(enable);
		npcWalkingAnim.setEnabled(enable);
		npcSpawnAnim.setEnabled(enable);
		npcRadiusSlider.setEnabled(enable);
		npcXoffsetSlider.setEnabled(enable);
		npcYoffsetSlider.setEnabled(enable);
//		configNameField.setEnabled(enable);
		saveButton.setEnabled(enable);
		deleteButton.setEnabled(enable);
	}
}
