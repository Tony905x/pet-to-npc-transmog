package com.pettonpc;

import javax.swing.JComboBox;

public interface ConfigProvider {
	boolean enableCustom();
	NpcData getSelectedNpc();
	int getModelRadius();
	int getOffsetX();
	int getOffsetY();
	int getSpawnAnimationID();
	int getStandingAnimationId();
	int getWalkingAnimationId();

	// Methods related to configuration dropdown
	String getCurrentConfigurationName();
	void setCurrentConfigurationName(String configName);

	// Add getters for NPC model IDs
	int getNpcModelID1();
	int getNpcModelID2();
	int getNpcModelID3();
	int getNpcModelID4();
	int getNpcModelID5();
	int getNpcModelID6();
	int getNpcModelID7();
	int getNpcModelID8();
	int getNpcModelID9();
	int getNpcModelID10();

}

