package com.pettonpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import javax.swing.JSlider;
import net.runelite.api.Client;
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
	private DataManager dataManager;
	private NpcFollowerPanel npcFollowerPanel;

	protected boolean transmogInitialized = false;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
	protected List<RuneLiteObject> transmogObjects;

	private static final int ANGLE_CONSTANT = 2048;
	private static final int ANGLE_OFFSET = 1500;
	private static final int TILE_TO_LOCAL_UNIT = 128;
	private int currentSpawnAnimationId = -1; // Default or initial value


	private AnimationManager animationManager;
	private PlayerStateTracker playerStateTracker;
	private NpcFollowerPanel panel;
	private NavigationButton navButton;

	private static final BufferedImage ICON = ImageUtil.loadImageResource(NpcFollowerPlugin.class, "/icon.png");

//	public boolean isTransmogInitialized() {
//		return transmogInitialized;
//	}

//	public List<RuneLiteObject> getTransmogObjects() {
//		return transmogObjects;
//	}

	@Override
	protected void startUp()
	{
		initializeVariables();
		dataManager = new DataManager(configManager);
		panel = new NpcFollowerPanel(this, configManager, dataManager);
		animationManager = new AnimationManager(client, panel, null);
		playerStateTracker = new PlayerStateTracker(client, animationManager, this);
		animationManager.setPlayerStateTracker(playerStateTracker);
		hooks.registerRenderableDrawListener(drawListener);

		navButton = NavigationButton.builder()
			.tooltip("Pet-to-NPC Transmog")
			.icon(ICON)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		if (dataManager.getSavedConfigNames() == null || dataManager.getSavedConfigNames().isEmpty())
		{
			System.out.println(" start if (dataManager.getSavedConfigNames() == null || dataManager.getSavedConfigNames().isEmpty())");
			panel.toggleCustomFields(false);
//			return;
		}

		if (panel != null & dataManager.getSavedConfigNames() != null)
		{
			System.out.println("start if (panel != null & dataManager.getSavedConfigNames() != null)");
			panel.loadLastConfiguration();
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

	private void initializeVariables()
	{
		transmogInitialized = false;
		transmogObjects = new ArrayList<>();
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		NPC follower = client.getFollower();

		if (follower != null)
		{
			if (transmogObjects == null)
			{
				transmogObjects = new ArrayList<>();
				if (playerStateTracker != null)
				{
					playerStateTracker.setTransmogObjects(transmogObjects);
				}
				if (animationManager != null)
				{
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
			if (playerStateTracker != null)
			{
				playerStateTracker.updateFollowerMovement(follower);
			}
		}
	}

	private RuneLiteObject initializeTransmogObject(NPC follower)
	{
		transmogObjects.clear();
		for (RuneLiteObject transmogObject : transmogObjects)
		{
			transmogObject.setActive(false);
			transmogObject.setFinished(true);
		}

		playerStateTracker.setTransmogObjects(transmogObjects);
		animationManager.setTransmogObjects(transmogObjects);

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		NpcData selectedNpc = panel.getSelectedNpc();

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel(selectedNpc);
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);

				int radius = panel.enableCustom() ? panel.getModelRadius() : selectedNpc.getRadius();
				transmogObject.setRadius(radius);

				playerStateTracker.setTransmogObjects(transmogObjects);
				animationManager.setTransmogObjects(transmogObjects);
				playerStateTracker.setCurrentState(PlayerState.SPAWNING);
			}
		}
		return transmogObject;
	}

	public void panelChange()
	{
		if (panel == null)
		{
			System.out.println("if (panel == null)");
			return;
		}


		NpcData selectedNpc = panel.getSelectedNpc();
		if (panel.enableCustom())

		{
			System.out.println("if (panel.enableCustom())");
			if (dataManager.getSavedConfigNames() == null || dataManager.getSavedConfigNames().isEmpty())
			{
				System.out.println("if (dataManager.getSavedConfigNames() == null || dataManager.getSavedConfigNames().isEmpty())");

				panel.updateFieldsWithNpcData(selectedNpc);
//				panel.setFieldsToDefaults();
//				return;
			} else {
				System.out.println("else panel.updateFieldsWithDropdownData(configName)");
				String configName = (String) panel.getConfigDropdown().getSelectedItem();
				panel.updateFieldsWithDropdownData(configName);
			}
		}
		else
		{
			System.out.println("else");
			panel.updateFieldsWithNpcData(selectedNpc);
		}

		clientThread.invokeLater(() -> {
			Model mergedModel = createNpcModel(selectedNpc);
			if (mergedModel != null && !transmogObjects.isEmpty())
//			if (transmogInitialized)
			{
				System.out.println("if (mergedModel != null && !transmogObjects.isEmpty())");
				RuneLiteObject transmogObject = transmogObjects.get(0);
				transmogObject.setModel(mergedModel);
				transmogObject.setActive(true);
				int radius = panel.enableCustom() ? panel.getModelRadius() : selectedNpc.getRadius();
				transmogObject.setRadius(radius);
				playerStateTracker.setTransmogObjects(transmogObjects);
				animationManager.setTransmogObjects(transmogObjects);
				playerStateTracker.setCurrentState(PlayerState.SPAWNING);
			}
		});
	}

	private void updateTransmogObject(NPC follower)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint followerLocation = follower.getLocalLocation();
		Player player = client.getLocalPlayer();

		int offsetX;
		int offsetY;
		int angle;
		int radius;
		int spawnAnimationId;  // To store the current spawn animation

		if (panel.enableCustom())
		{
			offsetX = panel.getOffsetX() * TILE_TO_LOCAL_UNIT;
			offsetY = panel.getOffsetY() * TILE_TO_LOCAL_UNIT;

			// Fetch custom spawn animation ID
			spawnAnimationId = panel.getSpawnAnimationID();
		}
		else
		{
			offsetX = panel.getSelectedNpc().getOffsetX() * TILE_TO_LOCAL_UNIT;
			offsetY = panel.getSelectedNpc().getOffsetY() * TILE_TO_LOCAL_UNIT;

			// Fetch spawn animation ID from the selected NPC
			spawnAnimationId = panel.getSelectedNpc().getSpawnAnim();
		}

		int newX = followerLocation.getX() + offsetX;
		int newY = followerLocation.getY() + offsetY;
		int dx = player.getLocalLocation().getX() - newX;
		int dy = player.getLocalLocation().getY() - newY;
		LocalPoint newLocation = new LocalPoint(newX, newY);

		if (offsetX == 0 && offsetY == 0)
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

					if (panel.enableCustom())
					{
						radius = panel.getModelRadius();
					}
					else
					{
						radius = panel.getSelectedNpc().getRadius();
					}
					transmogObject.setRadius(radius);

					// Set the model of the transmog object
					Model mergedModel = createNpcModel(panel.getSelectedNpc());
					if (mergedModel != null)
					{
						transmogObject.setModel(mergedModel);
					}

					// Check if spawn animation has changed
					if (spawnAnimationId != currentSpawnAnimationId)
					{
						currentSpawnAnimationId = spawnAnimationId; // Update the stored spawn animation ID
						playerStateTracker.setCurrentState(PlayerState.SPAWNING); // Trigger spawning state
					}

					playerStateTracker.setTransmogObjects(transmogObjects);
					animationManager.setTransmogObjects(transmogObjects);
				}
			}
		}
	}



	public Model createNpcModel(NpcData selectedNpc)
	{
		List<Integer> modelIds = new ArrayList<>();

		if (panel.enableCustom())
		{
			int[] npcModelIDs = {
				panel.getNpcModelID1(), panel.getNpcModelID2(), panel.getNpcModelID3(), panel.getNpcModelID4(),
				panel.getNpcModelID5(), panel.getNpcModelID6(), panel.getNpcModelID7(), panel.getNpcModelID8(),
				panel.getNpcModelID9(), panel.getNpcModelID10()
			};

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

		return mergedModelData.light();
	}

	public Model createCustomNpcModel(List<Integer> modelIds, int standingAnim, int walkingAnim, int spawnAnim)
	{
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

		// Apply animations if needed (this part depends on your existing logic)
		// mergedModelData.setStandingAnim(standingAnim);
		// mergedModelData.setWalkingAnim(walkingAnim);
		// mergedModelData.setSpawnAnim(spawnAnim);

		return mergedModelData.light();
	}


	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
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
		for (int i = 0; i < npcModelIDFields.length; i++)
		{
			String npcModelID = loadConfiguration(name, "npcModelID" + (i + 1));
			npcModelIDFields[i].setText(npcModelID);
		}
	}

	public String loadConfiguration(String name, String key)
	{
		return configManager.getConfiguration("petToNpcTransmog", name + "_" + key);
	}

	public void saveConfiguration(String name, String... npcModelIDs)
	{
		for (int i = 0; i < npcModelIDs.length; i++)
		{
			if (npcModelIDs[i] != null && !npcModelIDs[i].isEmpty())
			{
				saveConfiguration(name, "npcModelID" + (i + 1), Integer.parseInt(npcModelIDs[i]));
			}
		}

		dataManager.saveConfiguration(name, "npcStandingAnim", panel.getStandingAnimationId());
		dataManager.saveConfiguration(name, "npcWalkingAnim", panel.getWalkingAnimationId());
		dataManager.saveConfiguration(name, "npcSpawnAnim", panel.getSpawnAnimationID());
		dataManager.saveConfiguration(name, "npcRadius", panel.getModelRadius());
		dataManager.saveConfiguration(name, "npcXoffset", panel.getOffsetX());
		dataManager.saveConfiguration(name, "npcYoffset", panel.getOffsetY());

		updateSavedConfigNames(name);
	}

	public void saveConfiguration(String name, String key, int value)
	{
		configManager.setConfiguration("petToNpcTransmog", name + "_" + key, String.valueOf(value));
	}

	public void loadSliderConfiguration(String name, JSlider... sliders)
	{
		for (JSlider slider : sliders)
		{
			String value = loadConfiguration(name, slider.getName());
			if (value != null)
			{
				slider.setValue(Integer.parseInt(value));
			}
			else
			{
				slider.setValue(0);
			}
		}
	}

	private void updateSavedConfigNames(String newConfigName)
	{
		String savedConfigNames = dataManager.getSavedConfigNames();

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
			dataManager.setSavedConfigNames(savedConfigNames);
		}
	}

	public void deleteConfiguration(String name)
	{
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
}
