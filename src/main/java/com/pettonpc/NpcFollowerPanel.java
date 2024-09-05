package com.pettonpc;

import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.runelite.client.config.ConfigManager;

public class NpcFollowerPanel extends PluginPanel
{
	private final NpcFollowerPlugin plugin;
	private final ConfigManager configManager;
	private JComboBox<String> npcPresetDropdown;
	private JTextField configNameField;
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
	private JTextField npcRadius;
	private JTextField npcXoffset;
	private JTextField npcYoffset;
	private JTextField npcTextLocation;
	private JComboBox<String> configDropdown;

	public NpcFollowerPanel(NpcFollowerPlugin plugin, ConfigManager configManager)
	{
		this.plugin = plugin;
		this.configManager = configManager;
		setLayout(new BorderLayout());

		// Create components
		npcPresetDropdown = new JComboBox<>();
		configNameField = new JTextField();
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
		npcRadius = new JTextField();
		npcXoffset = new JTextField();
		npcYoffset = new JTextField();
		npcTextLocation = new JTextField();
		configDropdown = new JComboBox<>();
		JButton saveButton = new JButton("Save Configuration");

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
		mainPanel.add(new JLabel("NPC Presets:"));
		mainPanel.add(npcPresetDropdown);

		mainPanel.add(new JLabel("Configuration Name:"));
		mainPanel.add(configNameField);

		mainPanel.add(new JLabel("NPC Model ID 1:"));
		mainPanel.add(npcModelID1Field);

		mainPanel.add(new JLabel("NPC Model ID 2:"));
		mainPanel.add(npcModelID2Field);

		mainPanel.add(new JLabel("NPC Model ID 3:"));
		mainPanel.add(npcModelID3Field);

		mainPanel.add(new JLabel("NPC Model ID 4:"));
		mainPanel.add(npcModelID4Field);

		mainPanel.add(new JLabel("NPC Model ID 5:"));
		mainPanel.add(npcModelID5Field);

		mainPanel.add(new JLabel("NPC Model ID 6:"));
		mainPanel.add(npcModelID6Field);

		mainPanel.add(new JLabel("NPC Model ID 7:"));
		mainPanel.add(npcModelID7Field);

		mainPanel.add(new JLabel("NPC Model ID 8:"));
		mainPanel.add(npcModelID8Field);

		mainPanel.add(new JLabel("NPC Model ID 9:"));
		mainPanel.add(npcModelID9Field);

		mainPanel.add(new JLabel("NPC Model ID 10:"));
		mainPanel.add(npcModelID10Field);

		mainPanel.add(new JLabel("Standing Animation ID:"));
		mainPanel.add(npcStandingAnim);

		mainPanel.add(new JLabel("Walking Animation ID:"));
		mainPanel.add(npcWalkingAnim);

		mainPanel.add(new JLabel("Spawn Animation ID:"));
		mainPanel.add(npcSpawnAnim);

		mainPanel.add(new JLabel("NPC Radius:"));
		mainPanel.add(npcRadius);

		mainPanel.add(new JLabel("Xoffset:"));
		mainPanel.add(npcXoffset);

		mainPanel.add(new JLabel("Yoffset:"));
		mainPanel.add(npcYoffset);

		mainPanel.add(new JLabel("Text Location:"));
		mainPanel.add(npcTextLocation);

		mainPanel.add(new JLabel("Configuration Dropdown:"));
		mainPanel.add(configDropdown);


		// Add buttons to button panel
		buttonPanel.add(saveButton);

		// Add panels to main panel
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Handle preset dropdown
		npcPresetDropdown.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String configName = configNameField.getText();
				if (!configName.isEmpty())
				{
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
						npcRadius.getText(),
						npcXoffset.getText(),
						npcYoffset.getText(),
						npcTextLocation.getText()
					);
					updateConfigDropdown(configDropdown);
				}
			}
		});

		// Handle saving values
		saveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String configName = configNameField.getText();
				if (!configName.isEmpty())
				{
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
						npcRadius.getText(),
						npcXoffset.getText(),
						npcYoffset.getText(),
						npcTextLocation.getText()
					);
					updateConfigDropdown(configDropdown);
				}
			}
		});

		// Add ActionListener to configDropdown
		configDropdown.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
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
						npcSpawnAnim,
						npcRadius,
						npcXoffset,
						npcYoffset,
						npcTextLocation
					);
					updateConfigDropdown(configDropdown);
				}
			}
		});

		// Initialize dropdown
	}

	public void updateConfigDropdown(JComboBox<String> configDropdown)
	{
		System.out.println("Updating the dropdown!");
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

	public NpcData getSelectedNpc()
	{
		// Implement logic to get the selected NPC data
		// Placeholder, replace with actual implementation
		return NpcData.GnomeChild; // Example, replace with actual logic
	}

	public boolean enableCustom()
	{
		// Implement logic to check if custom is enabled
		return true; // Placeholder, replace with actual implementation
	}

	public int getModelRadius()
	{
		String text = npcRadius.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(npcRadius.getText());
	}

	public int getOffsetX()
	{
		String text = npcXoffset.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(npcXoffset.getText());
	}

	public int getOffsetY()
	{
		String text = npcYoffset.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(npcYoffset.getText());
	}

	public int getNpcModelID1()
	{
		String text = npcModelID1Field.getText();
		if (text == null || text.isEmpty()) {
			return 54288; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID2()
	{
		String text = npcModelID2Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID3()
	{
		String text = npcModelID3Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID4()
	{
		String text = npcModelID4Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID5()
	{
		String text = npcModelID5Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID6()
	{
		String text = npcModelID6Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID7()
	{
		String text = npcModelID7Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID8()
	{
		String text = npcModelID8Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID9()
	{
		String text = npcModelID9Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getNpcModelID10()
	{
		String text = npcModelID10Field.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(text);
	}

	public int getStandingAnimationId()
	{
		String text = npcStandingAnim.getText();
		if (text == null || text.isEmpty()) {
			return 11473; // or any default value you prefer
		}
		return Integer.parseInt(npcStandingAnim.getText());
	}

	public int getWalkingAnimationId()
	{
		String text = npcWalkingAnim.getText();
		if (text == null || text.isEmpty()) {
			return 11474; // or any default value you prefer
		}
		return Integer.parseInt(npcWalkingAnim.getText());
	}

	public int getSpawnAnimationID()
	{
		String text = npcSpawnAnim.getText();
		if (text == null || text.isEmpty()) {
			return 0; // or any default value you prefer
		}
		return Integer.parseInt(npcSpawnAnim.getText());
	}

	public void saveConfiguration(String name, String npcModelID, int index)
	{
		// Save the configuration using the provided name and npcModelID
		configManager.setConfiguration("petToNpcTransmog", name + "_npcModelID" + index, npcModelID);
	}

	public String loadConfiguration(String name, int index)
	{
		// Load the configuration using the provided name and index
		System.out.println("in the loadconfiguration panel");
		return configManager.getConfiguration("petToNpcTransmog", name + "_npcModelID" + index);
	}

	public String getSavedConfigNames()
	{
		// Return the saved configuration names
		return configManager.getConfiguration("petToNpcTransmog", "savedConfigNames");
	}

	public void setSavedConfigNames(String savedConfigNames)
	{
		// Set the saved configuration names
		configManager.setConfiguration("petToNpcTransmog", "savedConfigNames", savedConfigNames);
	}

	public JComboBox<String> getConfigDropdown()
	{
		return configDropdown;
	}
}

