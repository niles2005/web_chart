/**
 * 
 *
 * History:
 *   Jun 1, 2007 1:00:00 PM Created by NieLei
 */
package com.xtwsoft.webchart;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Jun 1, 2007 1:00:00 PM
 *
 */
public class ChartImage {
	private int buffWidth = -1;

	private int buffHeight = -1;

	private BufferedImage bufferImage = null;

	private Graphics2D graphics;
	
	public ChartImage() {
	}

	public int getWidth() {
		return buffWidth;
	}

	public int getHeight() {
		return buffHeight;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

	public BufferedImage getImage() {
		return bufferImage;
	}

	public int getTranslateX() {
		return translateX;
	}

	public int getTranslateY() {
		return translateY;
	}

	private int translateX, translateY;

	public void setTranslate(int x, int y) {
		translateX = x;
		translateY = y;
		graphics.translate(x, y);
	}

	private AffineTransform defaultTransform = null;

	public boolean resetSize(Rectangle rect) {
		if (buffWidth != rect.width || buffHeight != rect.height) {
			buffWidth = rect.width;
			buffHeight = rect.height;
			return resetSize(buffWidth, buffHeight);
		}
		return false;
	}

	public boolean resetSize(int rectWidth, int rectHeight) {
		if (buffWidth != rectWidth || buffHeight != rectHeight) {
			buffWidth = rectWidth;
			buffHeight = rectHeight;
			bufferImage = new BufferedImage(buffWidth, buffHeight,BufferedImage.TYPE_INT_RGB);
			graphics = bufferImage.createGraphics();
//			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);

			defaultTransform = graphics.getTransform();
			return true;
		}
		return false;
	}

	public void resetGraphics() {
		graphics.setTransform(defaultTransform);
	}

}
