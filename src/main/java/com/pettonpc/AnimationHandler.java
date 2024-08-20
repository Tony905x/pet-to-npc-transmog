package com.pettonpc;

//import com.google.inject.Provides;

import java.util.List;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;

@SuppressWarnings("LombokSetterMayBeUsed")
public class AnimationHandler
{
	private final Client client;
	private final NpcFollowerConfig config;
//	private ClientThread clientThread;
	private List<RuneLiteObject> transmogObjects;
//	private Thread animationThread;
//	private NpcFollowerPlugin npcFollowerPlugin;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int currentFrame;
	private PlayerStateTracker playerStateTracker;

	public void setPlayerStateTracker(PlayerStateTracker playerStateTracker)
	{
		this.playerStateTracker = playerStateTracker;
	}

	public AnimationHandler(Client client, NpcFollowerConfig config, PlayerStateTracker playerStateTracker)
	{
		this.client = client;
		this.config = config;
		this.playerStateTracker = playerStateTracker;
	}

//	@Provides
//	NpcFollowerConfig provideConfig(ConfigManager configManager)
//	{
//		return configManager.getConfig(NpcFollowerConfig.class);
//	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void triggerSpawnAnimation(NPC follower)
	{
		NpcData selectedNpc = config.selectedNpc();
		int spawnAnimationId;
		Animation spawnAnimation;

		if (selectedNpc == null)
		{
			return;
		}


		if (!config.enableCustom())
		{
			spawnAnimationId = selectedNpc.spawnAnim;
		}
		else
		{
			spawnAnimationId = config.spawnAnimationID();
		}

		spawnAnimation = client.loadAnimation(spawnAnimationId);


		for (RuneLiteObject transmogObject : transmogObjects)
		{
			playerStateTracker.setCurrentState(PlayerState.IDLE);

			if (transmogObject != null && follower != null)
			{
				transmogObject.setActive(true);
				transmogObject.setAnimation(spawnAnimation);
				transmogObject.setShouldLoop(false);

				playerStateTracker.setCurrentState(PlayerState.IDLE);
			}
		}
	}

	public void handleWalkingAnimation(NPC follower)
	{
		PlayerState currentState = playerStateTracker.getCurrentState();

		if (currentState == PlayerState.SPAWNING)
		{
			cancelCurrentAnimation();
		}

		NpcData selectedNpc = config.selectedNpc();
		int walkingAnimationId = (config.enableCustom()) ? config.walkingAnimationId() : selectedNpc.getWalkAnim();
		Animation walkingAnimation = client.loadAnimation(walkingAnimationId);

		if (selectedNpc == null || walkingAnimation == null || follower == null)
		{
			return;
		}

		transmogObjects.forEach(transmogObject -> {
			if (transmogObject != null)
			{
				currentFrame = transmogObject.getAnimationFrame();
				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);

				if (previousWalkingFrame == -1 || previousWalkingFrame > currentFrame)
				{
					transmogObject.setAnimation(walkingAnimation);
				}
				previousWalkingFrame = currentFrame;
			}
		});
	}

	public void handleStandingAnimation(NPC follower)
	{
		PlayerState currentState = playerStateTracker.getCurrentState();

		if (currentState == PlayerState.SPAWNING || currentState == PlayerState.IDLE)
		{
			return;
		}

		NpcData selectedNpc = config.selectedNpc();
		if (selectedNpc == null)
		{
			return;
		}

		int standingAnimationId = (config.enableCustom()) ? config.standingAnimationId() : selectedNpc.getStandingAnim();
		Animation standingAnimation = client.loadAnimation(standingAnimationId);
//		NPC followerLoop = client.getFollower();

		if (standingAnimation == null || follower == null)
		{
			return;
		}

		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null)
			{
				currentFrame = transmogObject.getAnimationFrame();
				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);
				if (previousStandingFrame == -1 || previousStandingFrame > currentFrame)
				{
					transmogObject.setAnimation(standingAnimation);
				}
				previousStandingFrame = currentFrame;
			}
		}
	}

	public void cancelCurrentAnimation()
	{
//		if (animationThread != null && animationThread.isAlive())
//		{
//			animationThread.interrupt();
//		}

		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null)
			{
				transmogObject.setShouldLoop(false);
				transmogObject.setActive(false);
			}
		}
	}
}
