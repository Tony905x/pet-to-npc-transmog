package com.pettonpc;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;

@Getter
public enum NpcData
{
	WiseOldMan("Wise Old Man", Lists.newArrayList(187, 9103, 4925, 28515, 323, 26619, 176, 3711, 265, 181), 813, 1146, 60, 10459, 0,0),
	GnomeChild("Gnome Child", Lists.newArrayList(2909, 2899, 2918), 195, 189, 60, 10492, 0,0),
	Nieve("Nieve", Lists.newArrayList(392, 27644, 27640, 19951, 3661, 28827, 9644, 27654, 9640, 11048), 10075, 10076, 60, 10036, 0,0),
	DrunkenDwarf("DrunkenDwarf", Lists.newArrayList(2974, 2986, 2983, 2979, 2981, 2985, 2992), 900, 104, 60, 2342,0,0),
	Konar("Konar", Lists.newArrayList(36162), 8219, 8218, 60, 8219,0,0),
	Thurgo("Thurgon", Lists.newArrayList(7034,7049,7041,7057,2985,19100), 7861, 2156, 60, 100,0,0),
//	Zilyana("Zilyana", Lists.newArrayList(27989, 27937, 27985, 27968, 27990), 6966, 6965, 120, 6967),
	Kklik("Kklik", Lists.newArrayList(13850,13848,13849), 3345, 3346, 60, 3345,0,0),
	Seren("Seren", Lists.newArrayList(38605), 8372, 8372, 180, 8380,0,0),
	Nightmare("Nightmare", Lists.newArrayList(42591), 8593, 8634, 300, 8609,2,0),
	Dog("Good Boy", Lists.newArrayList(26253), 6561, 6560, 60, 6561,0,0);
//	Whisperer("Nightmare", Lists.newArrayList(49222,49218,49221,49224,49219), 10230, 10232, 300, 8609, 2);







	// Properties
	final String name;
	final List<Integer> modelIDs;
	final int standingAnim;
	final int walkAnim;
	final int radius;
	final int spawnAnim;
	final int offsetX;
	final int offsetY;

	//Constructor
	NpcData(String name, List<Integer> modelIDs, int standingAnim, int walkAnim, int radius, int spawnAnim, int offsetX, int offsetY)
	{
		this.name = name;
		this.modelIDs = modelIDs;
		this.standingAnim = standingAnim;
		this.walkAnim = walkAnim;
		this.radius = radius;
		this.spawnAnim = spawnAnim;
		this.offsetX = offsetX;
		this.offsetY = offsetY;

	}
}



