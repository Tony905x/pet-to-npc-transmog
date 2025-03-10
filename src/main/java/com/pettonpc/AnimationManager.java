package com.pettonpc;

import java.util.List;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;

@SuppressWarnings("LombokSetterMayBeUsed")
public class AnimationManager
{
	private final Client client;
//	private final NpcFollowerPanel panel;
	private List<RuneLiteObject> transmogObjects;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int currentFrame;
	private PlayerStateTracker playerStateTracker;
	private ConfigProvider configProvider;

	public void setPlayerStateTracker(PlayerStateTracker playerStateTracker)
	{
		this.playerStateTracker = playerStateTracker;
	}

	public AnimationManager(Client client, ConfigProvider configProvider, PlayerStateTracker playerStateTracker) {
		this.client = client;
		this.configProvider = configProvider;
		this.playerStateTracker = playerStateTracker;
	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void triggerSpawnAnimation(NPC follower)
	{
		NpcData selectedNpc = configProvider.getSelectedNpc();
		int spawnAnimationId;
		Animation spawnAnimation;

		if (selectedNpc == null)
		{
			return;
		}

		if (!configProvider.enableCustom())
		{
			spawnAnimationId = selectedNpc.spawnAnim;
		}
		else
		{
			spawnAnimationId = configProvider.getSpawnAnimationID();
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

		NpcData selectedNpc = configProvider.getSelectedNpc();
		int walkingAnimationId = (configProvider.enableCustom()) ? configProvider.getWalkingAnimationId() : selectedNpc.getWalkAnim();
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

		NpcData selectedNpc = configProvider.getSelectedNpc();
		if (selectedNpc == null)
		{
			return;
		}

		int standingAnimationId = (configProvider.enableCustom()) ? configProvider.getStandingAnimationId() : selectedNpc.getStandingAnim();
		Animation standingAnimation = client.loadAnimation(standingAnimationId);

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
