package com.pettonpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import javax.swing.JSlider;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;

@PluginDescriptor(
	name = "Pet-to-NPC Transmog",
	description = "Customize your pets appearance to be any NPC/Object",
	tags = {"pet", "npc", "transmog", "companion", "follower"}
)
public class NpcFollowerPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Hooks hooks;
	@Inject
	private ConfigManager configManager;
	private DataManager dataManager;
	private NpcFollowerPanel npcFollowerPanel;

	protected boolean transmogInitialized = false;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
	protected List<RuneLiteObject> transmogObjects;

	private static final int ANGLE_CONSTANT = 2048;
	private static final int ANGLE_OFFSET = 1500;
	private static final int TILE_TO_LOCAL_UNIT = 128;

	private AnimationManager animationManager;
//	private DataManager dataManager;
	private PlayerStateTracker playerStateTracker;
	private NpcFollowerPanel panel;
	private NavigationButton navButton;

	private static final BufferedImage ICON = ImageUtil.loadImageResource(NpcFollowerPlugin.class, "/icon.png");

	public boolean isTransmogInitialized() {
		return transmogInitialized;
	}

	public List<RuneLiteObject> getTransmogObjects() {
		return transmogObjects;
	}

//	@Provides
//	NpcFollowerConfig provideConfig(ConfigManager configManager)
//	{
//		return configManager.getConfig(NpcFollowerConfig.class);
//	}


	@Override
	protected void startUp()
	{
		if (client.getGameState() != GameState.LOGGING_IN)
		{
			System.out.println("Startup GameStateChanged.  Logging in...");
		}
		if (client.getGameState() != GameState.LOADING)
		{
			System.out.println("Startup GameStateChanged.  LOADING...");
		}

		if (client.getGameState() != GameState.LOGIN_SCREEN)
		{
			System.out.println("Startup GameStateChanged.  LOGIN SCREEN");
		}

		if (client.getGameState() != GameState.STARTING)
		{
			System.out.println("Startup GameStateChanged.  STARTING...");
		}

		if (client.getGameState() != GameState.UNKNOWN)
		{
			System.out.println("Startup GameStateChanged.  UNKNOWN");
		}

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			System.out.println("Startup GameStateChanged.  logged in.");
		}



		initializeVariables();
		dataManager = new DataManager(configManager);
		panel = new NpcFollowerPanel(this, configManager, dataManager);// Initialize the panel here
		animationManager = new AnimationManager(client, panel, null); // Pass panel instead of config
		playerStateTracker = new PlayerStateTracker(client, animationManager, this);
		animationManager.setPlayerStateTracker(playerStateTracker); // Update playerStateTracker in animationManager
		hooks.registerRenderableDrawListener(drawListener);

		BufferedImage icon = ImageUtil.loadImageResource(WorldHopperPlugin.class, "icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Pet-to-NPC Transmog")
			.icon(ICON)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		// Populate the dropdown on startup

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			if (dataManager != null){
			dataManager.updateConfigDropdown(panel.getConfigDropdown());
			}
		}
	}

	@Override
	protected void shutDown() throws InterruptedException
	{
		final CountDownLatch latch = new CountDownLatch(1);

		clientThread.invokeLater(() -> {
			transmogObjects.forEach(transmogObject -> {
				transmogObject.setActive(false);
				transmogObject.setFinished(true);
			});
			transmogObjects.clear();
			latch.countDown();
		});

		latch.await();

		hooks.unregisterRenderableDrawListener(drawListener);
		initializeVariables();
		clientToolbar.removeNavigation(navButton);

	}

//	@Subscribe
//	public void onGameStateChanged(GameStateChanged event)
//	{
//		if (client.getGameState() != GameState.LOGGED_IN)
//		{
//			System.out.println("GameStateChanged.  logged in.");
//
//		}
//	}

	@Subscribe
	public void onProfileChanged(ProfileChanged event)
	{
			System.out.println("Profile Changed");
	}



	private void initializeVariables()
	{
		transmogInitialized = false;
		transmogObjects = new ArrayList<>();
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		NPC follower = client.getFollower();
		NpcData selectedNpc = panel.getSelectedNpc(); // Use panel instead of config
		Actor player = client.getLocalPlayer();

		if (follower != null)
		{
			if (transmogObjects == null)
			{
				transmogObjects = new ArrayList<>();
				if (playerStateTracker != null) {
					playerStateTracker.setTransmogObjects(transmogObjects);
				}
				if (animationManager != null) {
					animationManager.setTransmogObjects(transmogObjects);
				}
			}
			if (!transmogInitialized)
			{
				RuneLiteObject transmogObject = initializeTransmogObject(follower);
				if (transmogObject != null)
				{
					transmogInitialized = true;
				}
			}
			updateTransmogObject(follower);
			if (playerStateTracker != null) {
				playerStateTracker.updateFollowerMovement(follower);
			}
		}
	}

	private RuneLiteObject initializeTransmogObject(NPC follower)
	{
		transmogObjects.clear();
		// Set old transmog objects to inactive and finished
		for (RuneLiteObject transmogObject : transmogObjects)
		{
			transmogObject.setActive(false);
			transmogObject.setFinished(true);
		}

		playerStateTracker.setTransmogObjects(transmogObjects);
		animationManager.setTransmogObjects(transmogObjects);

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		
		NpcData selectedNpc = panel.getSelectedNpc(); // Use panel instead of config

		System.out.println("initialize method transmog state" + transmogObject);

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel(selectedNpc);
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);

				int radius;
				if (panel.enableCustom())
				{
					radius = panel.getModelRadius();
				}
				else
				{
					radius = selectedNpc.getRadius();
				}
				System.out.println("Initializing with radius: " + radius);
				transmogObject.setRadius(radius);

				playerStateTracker.setTransmogObjects(transmogObjects);
				animationManager.setTransmogObjects(transmogObjects);
				playerStateTracker.setCurrentState(PlayerState.SPAWNING);
			}
		}
		return transmogObject;
	}


//	@Subscribe
public void onConfigChanged()
{
	if (panel == null) {
		return;
	}

	NpcData selectedNpc = panel.getSelectedNpc(); // Use the selected NPC from the dropdown
	List<Integer> modelIds = selectedNpc.getModelIDs();

	// Update the panel fields based on the custom option
	if (panel.enableCustom())
	{
		String configName = (String) panel.getConfigDropdown().getSelectedItem();
		panel.updateFieldsWithDropdownData(configName);
	}
	else
	{
		panel.updateFieldsWithNpcData(selectedNpc);
	}


	clientThread.invokeLater(() -> {
		Model mergedModel = createNpcModel(selectedNpc); // Pass the selected NPC to createNpcModel
		if (mergedModel != null)
		{
			if (!transmogObjects.isEmpty())
			{
				RuneLiteObject transmogObject = transmogObjects.get(0);
				transmogObject.setModel(mergedModel);
				transmogObject.setActive(true);
				if (panel.enableCustom())
				{
					System.out.println("Using custom radius values");
					transmogObject.setRadius(panel.getModelRadius());
				}
				else
				{
					System.out.println("Using preset radius values");
					transmogObject.setRadius(selectedNpc.getRadius());
				}
				playerStateTracker.setTransmogObjects(transmogObjects);
				animationManager.setTransmogObjects(transmogObjects);

				NPC follower = client.getFollower();
				playerStateTracker.setCurrentState(PlayerState.SPAWNING);
			}
		}
	});
}


	private void updateTransmogObject(NPC follower)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint followerLocation = follower.getLocalLocation();

		int offsetX, offsetY;
		if (panel.enableCustom()) // Check if custom option is enabled
		{
			offsetX = panel.getOffsetX() * TILE_TO_LOCAL_UNIT; // Use panel values
			offsetY = panel.getOffsetY() * TILE_TO_LOCAL_UNIT; // Use panel values
		}
		else
		{
			NpcData selectedNpc = panel.getSelectedNpc(); // Get the selected NPC from the enum
			offsetX = selectedNpc.getOffsetX() * TILE_TO_LOCAL_UNIT; // Use preset values from the enum
			offsetY = selectedNpc.getOffsetY() * TILE_TO_LOCAL_UNIT; // Use preset values from the enum
		}

		int newX = followerLocation.getX() + offsetX;
		int newY = followerLocation.getY() + offsetY;
		LocalPoint newLocation = new LocalPoint(newX, newY);

		Player player = client.getLocalPlayer();
		int dx = player.getLocalLocation().getX() - newX;
		int dy = player.getLocalLocation().getY() - newY;
		int angle;

		if (offsetX == 0 && offsetY == 0) // Check if offsets are zero
		{
			angle = follower.getCurrentOrientation();
		}
		else
		{
			angle = (int) ((Math.atan2(-dy, dx) * ANGLE_CONSTANT) / (2 * Math.PI) + ANGLE_OFFSET) % ANGLE_CONSTANT;
		}

		Angle followerOrientation = new Angle(angle);

		if (transmogObjects != null)
		{
			for (RuneLiteObject transmogObject : transmogObjects)
			{
				if (transmogObject != null)
				{
					transmogObject.setLocation(newLocation, worldView.getPlane());
					transmogObject.setOrientation(followerOrientation.getAngle());

					int radius;
					if (panel.enableCustom())
					{
						radius = panel.getModelRadius();
					}
					else
					{
						radius = panel.getSelectedNpc().getRadius();
					}
//					System.out.println("Updating with radius: " + radius);
					transmogObject.setRadius(radius);

					playerStateTracker.setTransmogObjects(transmogObjects);
					animationManager.setTransmogObjects(transmogObjects);
				}
			}
		}
	}


	public Model createNpcModel(NpcData selectedNpc)
	{
		List<Integer> modelIds = new ArrayList<>();

		if (panel.enableCustom()) // Use panel instead of config
		{
			System.out.println("Using custom model IDs");
			int[] npcModelIDs = {panel.getNpcModelID1(), panel.getNpcModelID2(), panel.getNpcModelID3(), panel.getNpcModelID4(), panel.getNpcModelID5(), panel.getNpcModelID6(), panel.getNpcModelID7(), panel.getNpcModelID8(), panel.getNpcModelID9(), panel.getNpcModelID10()}; // Use panel instead of config

			for (int modelId : npcModelIDs)
			{
				if (modelId > 0)
				{
					modelIds.add(modelId);
				}
			}
		}
		else
		{
			System.out.println("Using preset model IDs");
			modelIds.addAll(selectedNpc.getModelIDs());
		}

		System.out.println("Model IDs: " + modelIds); // Add logging to trace model IDs

		if (modelIds.isEmpty())
		{
			System.out.println("if (modelIds.isEmpty)");
			return null;
		}

		ModelData[] modelDataArray = modelIds.stream().map(client::loadModelData).toArray(ModelData[]::new);

		if (Arrays.stream(modelDataArray).anyMatch(Objects::isNull))
		{
			System.out.println("if (Arrays.stream(modelDataArray).anyMatch(Objects::isNull))");
			return null;
		}

		ModelData mergedModelData = client.mergeModels(modelDataArray);
		if (mergedModelData == null)
		{
			System.out.println("if (mergedModelData == null)");
			return null;
		}

		Model finalModel = mergedModelData.light();
		if (finalModel == null)
		{
			System.out.println("if (finalModel == null)");
			return null;
		}
		System.out.println("return finalModel;");
		return finalModel;
	}




	public void setTransmogObjects(List<RuneLiteObject> transmogObjects) {
		this.transmogObjects = transmogObjects;
	}

	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;
			if (npc == client.getFollower())
			{
				return false;
			}
		}
		return true;
	}

	public void loadConfiguration(String name, JTextField... npcModelIDFields)
	{
		System.out.println("In the LoadConfiguration Plugin");
//		NPC follower = client.getFollower();
//		reinitializeTransmogObjects(); // Reinitialize transmog objects after loading configuration

		for (int i = 0; i < npcModelIDFields.length; i++)
		{
			String npcModelID = loadConfiguration(name, String.valueOf(i + 1)); // Use panel instead of configManager
			npcModelIDFields[i].setText(npcModelID);
		}

	}

	public String loadConfiguration(String name, String key)
	{
		return configManager.getConfiguration("petToNpcTransmog", name + "_" + key);
	}





	// Method to save multiple NPC model IDs and additional fields
	public void saveConfiguration(String name, String... npcModelIDs) {
		for (int i = 0; i < npcModelIDs.length; i++) {
			if (npcModelIDs[i] != null && !npcModelIDs[i].isEmpty()) {
				System.out.println("Saving Model ID " + (i + 1) + ": " + npcModelIDs[i]);
				saveConfiguration(name, "npcModelID" + (i + 1), Integer.parseInt(npcModelIDs[i]));
			}
		}

		// Save additional fields
		dataManager.saveConfiguration(name, "npcStandingAnim", panel.getStandingAnimationId());
		dataManager.saveConfiguration(name, "npcWalkingAnim", panel.getWalkingAnimationId());
		dataManager.saveConfiguration(name, "npcSpawnAnim", panel.getSpawnAnimationID());
		dataManager.saveConfiguration(name, "npcRadius", panel.getModelRadius());
		dataManager.saveConfiguration(name, "npcXoffset", panel.getOffsetX());
		dataManager.saveConfiguration(name, "npcYoffset", panel.getOffsetY());

		updateSavedConfigNames(name);
	}

	public void saveConfiguration(String name, String key, int value) {
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, String.valueOf(value));
	}


	public void loadSliderConfiguration(String name, JSlider... sliders)
	{
		System.out.println("In the LoadSliderConfiguration Plugin");
		for (JSlider slider : sliders)
		{
			String value = loadConfiguration(name, slider.getName()); // Use panel instead of configManager
			if (value != null)
			{
				slider.setValue(Integer.parseInt(value));
			}
			else
			{
				slider.setValue(0); // Set a default value if the configuration value is null
			}
		}
	}



	private void updateSavedConfigNames(String newConfigName)
	{
		String savedConfigNames = dataManager.getSavedConfigNames(); // Use panel instead of configManager

		// Check if savedConfigNames is null and initialize it if necessary
		if (savedConfigNames == null)
		{
			savedConfigNames = "";
		}

		if (!savedConfigNames.contains(newConfigName))
		{
			if (savedConfigNames.isEmpty())
			{
				savedConfigNames = newConfigName;
			}
			else
			{
				savedConfigNames = savedConfigNames + "," + newConfigName;
			}
			dataManager.setSavedConfigNames(savedConfigNames); // Use panel instead of configManager
		}
	}






	public void deleteConfiguration(String name)
	{
		// Remove the configuration using the provided name
		for (int i = 1; i <= 10; i++)
		{
			configManager.unsetConfiguration("petToNpcTransmog", name + "_npcModelID" + i);
		}
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcStandingAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcWalkingAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcSpawnAnim");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcRadius");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcXoffset");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcYoffset");
		configManager.unsetConfiguration("petToNpcTransmog", name + "_npcTextLocation");

		// Update the saved configuration names
		String savedConfigNames = dataManager.getSavedConfigNames();
		if (savedConfigNames != null && !savedConfigNames.isEmpty())
		{
			String[] configNames = savedConfigNames.split(",");
			StringBuilder updatedConfigNames = new StringBuilder();
			for (String configName : configNames)
			{
				if (!configName.equals(name))
				{
					if (updatedConfigNames.length() > 0)
					{
						updatedConfigNames.append(",");
					}
					updatedConfigNames.append(configName);
				}
			}
			dataManager.setSavedConfigNames(updatedConfigNames.toString());
		}
	}

//	public void setSavedConfigNames(String savedConfigNames)
//	{
//		configManager.setConfiguration("petToNpcTransmog", "savedConfigNames", savedConfigNames);
//	}
}
