package com.pettonpc;

import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayManager;

public class PlayerStateTracker
{
	@Inject
	private OverlayManager overlayManager;
//	@Inject
//	private TextOverlay textOverlay;

	private PlayerState currentState;
	private final Client client;
	private final AnimationManager animationManager;
	private List<RuneLiteObject> transmogObjects;

	public PlayerStateTracker(Client client, AnimationManager animationManager, NpcFollowerPlugin npcFollowerPlugin)
	{
		this.client = client;
		this.animationManager = animationManager;
	}

	public List<RuneLiteObject> getTransmogObjects()
	{
		return transmogObjects;
	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void setCurrentState(PlayerState newState)
	{
		NPC follower = client.getFollower();
		this.currentState = newState;
		updateFollowerMovement(follower);
		updateFollowerState(follower);
	}

	public PlayerState getCurrentState()
	{
		return this.currentState;
	}

	public void updateFollowerMovement(NPC follower)
	{
		if (transmogObjects == null || follower == null || currentState == PlayerState.SPAWNING)
		{
			return;
		}

		LocalPoint currentLocation = follower.getLocalLocation();
		PlayerState newState;
		int WALKING_ANIMATION_ID = follower.getWalkAnimation();
		int STANDING_ANIMATION_ID = follower.getIdlePoseAnimation();
		int ACTION_ANIMATION_ID = follower.getPoseAnimation();

		if (ACTION_ANIMATION_ID == WALKING_ANIMATION_ID)
		{
			newState = PlayerState.MOVING;
		}
		else
		{
			newState = PlayerState.STANDING;
		}

		if (newState != currentState)
		{
			animationManager.cancelCurrentAnimation();
		}

		currentState = newState;
		updateFollowerState(follower);
	}

	public void updateFollowerState(NPC follower)
	{
		switch (currentState)
		{
			case MOVING:
				animationManager.handleWalkingAnimation(follower);
				break;
			case STANDING:
				animationManager.handleStandingAnimation(follower);
				break;
			case SPAWNING:
				animationManager.triggerSpawnAnimation(follower);
				break;
			case IDLE:
				updateFollowerMovement(follower);
				break;
		}
	}
}
