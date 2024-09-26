# Pet-To-NPC Transmog

**Author:** Tony905x

**Description:** Customize your pet's appearance to be any NPC/Object.

## Important
- This is only seen on your end, others will still see your pet normally
- For now if you are making your own custom NPC you will need to "Save" it in order for the model to update in game

## Instructions
1.  Have any pet out, if you don't have a pet then a cat or a kitten will work.
2.  For preset NPC's simply select the NPC from the 'NPC Presets' dropdown and you are done.
3.  To use a different NPC click the 'Enable Custom' checkbox and follow the below steps:
4.  https://runemonk.com/tools/entityviewer/ can give you model ID's. This is the site I personally use but there are alternatives such as https://chisel.weirdgloop.org/moid/npc_name.html.
5.  Search for the NPC you want to use.
6.  Click on the Data Tab.
7.  Use the ID under 'models:' (Not the first one listed under 'id:' which is a npcID not a modelID) Paste it into the plugin panel NPC ModelID1 field.
8.  Some models combine multiple ID's in which case you will have to put the other model ID's in the panels other modelID fields. If you look at the preset NPC's values in the plugin panel it will give you an idea of the proper way to fill it out.
9.  Use the standingAnimation ID and the walkingAnimation ID as well and put them in the panel.
10. Click Save and type a name you wish to save it under.  In order for the new model to be updated you will have to save.

## Extra Info
- Large models will clip due to their size. Increasing the 'radius' in the panel will prevent this.
- If a large model is too close to you, increase the X Offset or the Y Offset to put more distance between you and the NPC.
- Spawn animation can be used to try out animations for the NPC. Animation ID's can be found in the 'Model' tab instead of the 'data' tab on RuneMonk.
- For now, turn the plugin off if you need to pick up your pet until a future update.

## Usage
- Install Pet-To-NPC Transmog from the plugin hub
- Use the custom panel on the right.  The icon is the green gnome head.

## Support
- If you encounter any issues or have questions, please use the "Report an issue" in the top right of this page


## Special Thanks To:
- Dezinated for the permitted use of his website "Runemonk.com" to get the necessary ID's
- Runelite team for permitting this plugin to be part of the plugin hub


