package com.pettonpc;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

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
	private NpcFollowerConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TextOverlay textOverlay;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Hooks hooks;

	protected boolean transmogInitialized = false;
	private LocalPoint lastFollowerLocation;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
	protected List<RuneLiteObject> transmogObjects;

	private static final int ANGLE_CONSTANT = 2048;
	private static final int ANGLE_OFFSET = 1500;
	private static final int TILE_TO_LOCAL_UNIT = 128;

	private AnimationHandler animationHandler;
	private NpcFollowerPlugin npcFollowerPlugin;
	private PlayerStateTracker playerStateTracker;

	public boolean isTransmogInitialized() {
		return transmogInitialized;
	}

	public List<RuneLiteObject> getTransmogObjects() {
		return transmogObjects;
	}

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	@Override
	protected void startUp()
	{
		initializeVariables();
		animationHandler = new AnimationHandler(client, config, null); // Pass null for playerStateTracker temporarily
		playerStateTracker = new PlayerStateTracker(client,animationHandler, npcFollowerPlugin);
		animationHandler.setPlayerStateTracker(playerStateTracker); // Update playerStateTracker in animationHandler
		hooks.registerRenderableDrawListener(drawListener);
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
		overlayManager.remove(textOverlay);
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
		NpcData selectedNpc = config.selectedNpc();
		Actor player = client.getLocalPlayer();

		if (follower != null)
		{
			if (transmogObjects == null)
			{
				transmogObjects = new ArrayList<>();
				playerStateTracker.setTransmogObjects(transmogObjects);
				animationHandler.setTransmogObjects(transmogObjects);
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
			playerStateTracker.updateFollowerMovement(follower);
		}
	}

	private RuneLiteObject initializeTransmogObject(NPC follower)
	{
		transmogObjects.clear();
		playerStateTracker.setTransmogObjects(transmogObjects);
		animationHandler.setTransmogObjects(transmogObjects);

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		NpcData selectedNpc = config.selectedNpc();

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel();
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);
				if (config.enableCustom())
				{
					transmogObject.setRadius(config.modelRadius());
				}
				else
				{
					transmogObject.setRadius(selectedNpc.radius);
				}
				playerStateTracker.setTransmogObjects(transmogObjects);
				animationHandler.setTransmogObjects(transmogObjects);
				playerStateTracker.setCurrentState(PlayerState.SPAWNING);
			}
		}
		return transmogObject;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("petToNpcTransmog"))
		{
			NpcData selectedNpc = config.selectedNpc();
			List<Integer> modelIds = selectedNpc.getModelIDs();
			if (modelIds.isEmpty())
			{
				return;
			}

			if (!event.getGroup().equals(NpcFollowerConfig.GROUP))
			{
				return;
			}

			if (event.getKey().equals("selectedNpc") || config.enableCustom() || !config.enableCustom())
			{
				clientThread.invokeLater(() -> {
					Model mergedModel = createNpcModel();
					if (mergedModel != null)
					{
						RuneLiteObject transmogObject = transmogObjects.get(0);
						transmogObject.setModel(mergedModel);
						transmogObject.setActive(true);
						if (config.enableCustom())
						{
							transmogObject.setRadius(config.modelRadius());
						}
						else
						{
							transmogObject.setRadius(selectedNpc.radius);
						}
						playerStateTracker.setTransmogObjects(transmogObjects);
						animationHandler.setTransmogObjects(transmogObjects);

//						if (selectedNpc.name.equals("Gnome Child"))
//						{
							NPC follower = client.getFollower();
							playerStateTracker.setCurrentState(PlayerState.SPAWNING);
//						}
					}
				});
			}
		}
	}

	private void updateTransmogObject(NPC follower)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint followerLocation = follower.getLocalLocation();

		int offsetX = config.offsetX() * TILE_TO_LOCAL_UNIT;
		int offsetY = config.offsetY() * TILE_TO_LOCAL_UNIT;

		int newX = followerLocation.getX() + offsetX;
		int newY = followerLocation.getY() + offsetY;
		LocalPoint newLocation = new LocalPoint(newX, newY);

		Player player = client.getLocalPlayer();
		int dx = player.getLocalLocation().getX() - newX;
		int dy = player.getLocalLocation().getY() - newY;
		int angle;

		if (config.offsetX() == 0 && config.offsetY() == 0)
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
					playerStateTracker.setTransmogObjects(transmogObjects);
					animationHandler.setTransmogObjects(transmogObjects);
				}
			}
		}
	}

	public Model createNpcModel()
	{
		NpcData selectedNpc = config.selectedNpc();
		List<Integer> modelIds = new ArrayList<>();

		if (config.enableCustom())
		{
			int[] npcModelIDs = {config.npcModelID1(), config.npcModelID2(), config.npcModelID3(), config.npcModelID4(), config.npcModelID5(), config.npcModelID6(), config.npcModelID7(), config.npcModelID8(), config.npcModelID9(), config.npcModelID10()};

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
	}
