//package com.pettonpc;
//
////import com.google.inject.Provides;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.Graphics2D;
//import java.util.List;
//import javax.inject.Inject;
//import net.runelite.api.Client;
//import net.runelite.api.Perspective;
//import net.runelite.api.Point;
//import net.runelite.api.RuneLiteObject;
//import net.runelite.api.coords.LocalPoint;
//import net.runelite.client.callback.ClientThread;
//import net.runelite.client.ui.FontManager;
//import net.runelite.client.ui.overlay.Overlay;
//import net.runelite.client.ui.overlay.OverlayPosition;
//import net.runelite.client.ui.overlay.OverlayUtil;
//
//public class TextOverlay extends Overlay {
//	private final NpcFollowerPlugin plugin;
//	private final Client client;
//	private final NpcFollowerConfig config;
//	private final ClientThread clientThread;
//
//	@Inject
//	public TextOverlay(NpcFollowerPlugin plugin, Client client, NpcFollowerConfig config, ClientThread clientThread) {
//		this.plugin = plugin;
//		this.client = client;
//		this.config = config;
//		this.clientThread = clientThread;
//		setPosition(OverlayPosition.DYNAMIC);
//	}

//	@Override
//	public Dimension render(Graphics2D graphics) {
//		if (plugin.isTransmogInitialized()) {
//			String text = "Hi dad"; // Replace with the text you want to display
//			List<RuneLiteObject> transmogObjects = plugin.getTransmogObjects();
//			for (RuneLiteObject transmogObject : transmogObjects) {
//				if (transmogObject != null) {
//					LocalPoint localPoint = transmogObject.getLocation();
//					if (localPoint != null) {
//						Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, text, config.textLocation());
//						if (textLocation != null) {
//							Font font = FontManager.getRunescapeBoldFont();
//							graphics.setFont(font);
//							Color textColor = Color.YELLOW;
//							OverlayUtil.renderTextLocation(graphics, textLocation, text, textColor);
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}
//}
