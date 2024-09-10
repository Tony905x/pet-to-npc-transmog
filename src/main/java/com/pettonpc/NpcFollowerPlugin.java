package com.pettonpc;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
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
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
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

	protected boolean transmogInitialized = false;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
	protected List<RuneLiteObject> transmogObjects;

	private static final int ANGLE_CONSTANT = 2048;
	private static final int ANGLE_OFFSET = 1500;
	private static final int TILE_TO_LOCAL_UNIT = 128;

	private AnimationHandler animationHandler;
	private PlayerStateTracker playerStateTracker;
	private NpcFollowerPanel panel;
	private NavigationButton navButton;

	public boolean isTransmogInitialized() {
		return transmogInitialized;
	}

	public List<RuneLiteObject> getTransmogObjects() {
		return transmogObjects;
	}

	@Override
	protected void startUp()
	{
		initializeVariables();
		panel = new NpcFollowerPanel(this, configManager); // Initialize the panel here
		animationHandler = new AnimationHandler(client, panel, null); // Pass panel instead of config
		playerStateTracker = new PlayerStateTracker(client, animationHandler, this);
		animationHandler.setPlayerStateTracker(playerStateTracker); // Update playerStateTracker in animationHandler
		hooks.registerRenderableDrawListener(drawListener);

		BufferedImage placeholderIcon = IconUtil.createPlaceholderIcon();

		navButton = NavigationButton.builder()
			.tooltip("Pet-to-NPC Transmog")
			.icon(placeholderIcon)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		// Populate the dropdown on startup
			panel.updateConfigDropdown(panel.getConfigDropdown());

		// Ensure the checkbox state is correctly handled
		if (panel.enableCustom())
		{
			System.out.println("Custom values enabled on startup");
		}
		else
		{
			System.out.println("Preset values enabled on startup");
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

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			initializeVariables();
		}
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
				if (animationHandler != null) {
					animationHandler.setTransmogObjects(transmogObjects);
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

//		if (playerStateTracker != null) {
			playerStateTracker.setTransmogObjects(transmogObjects);
//		}
//		if (animationHandler != null) {
			animationHandler.setTransmogObjects(transmogObjects);
//		}

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		NpcData selectedNpc = panel.getSelectedNpc(); // Use panel instead of config


		System.out.println("initialize method transmog state" + transmogObject );

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel(selectedNpc);
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);
				if (panel.enableCustom()) // Use panel instead of config
				{
					System.out.println("Intialize Using custom radius values");
					transmogObject.setRadius(panel.getModelRadius()); // Use panel instead of config
				}
				else
				{
					System.out.println("Initiailize Using preset radius values");
					transmogObject.setRadius(selectedNpc.getRadius());
				}
				if (playerStateTracker != null) {
					playerStateTracker.setTransmogObjects(transmogObjects);
				}
				if (animationHandler != null) {
					animationHandler.setTransmogObjects(transmogObjects);
				}
				if (playerStateTracker != null) {
					playerStateTracker.setCurrentState(PlayerState.SPAWNING);
				}
			}
		}
		return transmogObject;
	}


//	@Subscribe
public void onConfigChanged()
{
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
				animationHandler.setTransmogObjects(transmogObjects);

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
					if (playerStateTracker != null) {
						playerStateTracker.setTransmogObjects(transmogObjects);
					}
					if (animationHandler != null) {
						animationHandler.setTransmogObjects(transmogObjects);
					}
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




	public void saveConfiguration(String name, String... npcModelIDs)
	{
		for (int i = 0; i < npcModelIDs.length; i++)
		{
			panel.saveConfiguration(name, npcModelIDs[i], i + 1); // Use panel instead of configManager
		}

		// Save additional fields
		panel.saveConfiguration(name, "npcStandingAnim", panel.getStandingAnimationId());
		panel.saveConfiguration(name, "npcWalkingAnim", panel.getWalkingAnimationId());
		panel.saveConfiguration(name, "npcSpawnAnim", panel.getSpawnAnimationID());
		panel.saveConfiguration(name, "npcRadius", panel.getModelRadius());
		panel.saveConfiguration(name, "npcXoffset", panel.getOffsetX());
		panel.saveConfiguration(name, "npcYoffset", panel.getOffsetY());

		updateSavedConfigNames(name);
	}

	public void loadConfiguration(String name, JTextField... npcModelIDFields)
	{
		System.out.println("In the LoadConfiguration Plugin");
//		NPC follower = client.getFollower();
//		reinitializeTransmogObjects(); // Reinitialize transmog objects after loading configuration

		for (int i = 0; i < npcModelIDFields.length; i++)
		{
			String npcModelID = panel.loadConfiguration(name, i + 1); // Use panel instead of configManager
			npcModelIDFields[i].setText(npcModelID);
		}

	}

	private void updateSavedConfigNames(String newConfigName)
	{
		String savedConfigNames = panel.getSavedConfigNames(); // Use panel instead of configManager

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
			panel.setSavedConfigNames(savedConfigNames); // Use panel instead of configManager
		}
	}

	public String getSavedConfigNames()
	{
		if (panel == null) {
			return "";
		}
		String savedConfigNames = panel.getSavedConfigNames(); // Use panel instead of configManager
		return savedConfigNames != null ? savedConfigNames : "";
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
		String savedConfigNames = panel.getSavedConfigNames();
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
			panel.setSavedConfigNames(updatedConfigNames.toString());
		}
	}

//	private void reinitializeTransmogObjects()
//	{
//		NPC follower = client.getFollower();
//		if (follower != null)
//		{
//			transmogInitialized = false;
//			initializeTransmogObject(follower);
//		}else
//		{
//			// Handle the case where the follower is null
//			System.out.println("Follower NPC is null. Cannot reinitialize transmog objects.");
//		}
//	}
}
