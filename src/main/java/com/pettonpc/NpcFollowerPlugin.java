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
		// Set old transmog objects to inactive and finished
		for (RuneLiteObject transmogObject : transmogObjects)
		{
			transmogObject.setActive(false);
			transmogObject.setFinished(true);
		}

		transmogObjects.clear();
		if (playerStateTracker != null) {
			playerStateTracker.setTransmogObjects(transmogObjects);
		}
		if (animationHandler != null) {
			animationHandler.setTransmogObjects(transmogObjects);
		}

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		NpcData selectedNpc = panel.getSelectedNpc(); // Use panel instead of config

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel();
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);
				if (panel.enableCustom()) // Use panel instead of config
				{
					transmogObject.setRadius(panel.getModelRadius()); // Use panel instead of config
				}
				else
				{
					transmogObject.setRadius(selectedNpc.radius);
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

	private void updateTransmogObject(NPC follower)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint followerLocation = follower.getLocalLocation();

		int offsetX = panel.getOffsetX() * TILE_TO_LOCAL_UNIT; // Use panel instead of config
		int offsetY = panel.getOffsetY() * TILE_TO_LOCAL_UNIT; // Use panel instead of config

		int newX = followerLocation.getX() + offsetX;
		int newY = followerLocation.getY() + offsetY;
		LocalPoint newLocation = new LocalPoint(newX, newY);

		Player player = client.getLocalPlayer();
		int dx = player.getLocalLocation().getX() - newX;
		int dy = player.getLocalLocation().getY() - newY;
		int angle;

		if (panel.getOffsetX() == 0 && panel.getOffsetY() == 0) // Use panel instead of config
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

	public Model createNpcModel()
	{
		NpcData selectedNpc = panel.getSelectedNpc(); // Use panel instead of config
		List<Integer> modelIds = new ArrayList<>();

		if (panel.enableCustom()) // Use panel instead of config
		{
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
			modelIds.addAll(selectedNpc.getModelIDs());
		}

		System.out.println("Model IDs: " + modelIds); // Add logging to trace model IDs

		if (modelIds.isEmpty())
		{
			return null;
		}

		ModelData[] modelDataArray = modelIds.stream().map(client::loadModelData).toArray(ModelData[]::new);

		if (Arrays.stream(modelDataArray).anyMatch(Objects::isNull))
		{
			return null;
		}

		ModelData mergedModelData = client.mergeModels(modelDataArray);
		if (mergedModelData == null)
		{
			return null;
		}

		Model finalModel = mergedModelData.light();
		if (finalModel == null)
		{
			return null;
		}

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
//			panel.saveConfiguration(name, npcModelIDs[i], i + 1); // Use panel instead of configManager
			panel.saveConfiguration(name, npcModelIDs[i], i + 1); // Use panel instead of configManager
		}
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
