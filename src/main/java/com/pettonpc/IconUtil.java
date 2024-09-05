package com.pettonpc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class IconUtil
{
	public static BufferedImage createPlaceholderIcon()
	{
		int width = 32;
		int height = 32;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		// Draw a simple placeholder icon (e.g., a red square)
		g2d.setColor(Color.RED);
		g2d.fillRect(0, 0, width, height);

		g2d.dispose();
		return image;
	}
}

