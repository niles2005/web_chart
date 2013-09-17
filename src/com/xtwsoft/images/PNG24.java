package com.xtwsoft.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 *	 
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class PNG24 extends MinimapImage {
	private BufferedImage m_bufferImage = null;
	public PNG24(BufferedImage bufferImage, int compression) {
		super(bufferImage);
		m_bufferImage = bufferImage;
	}

	public void writeToOutputStream(OutputStream os) throws IOException {
		ImageIO.write(m_bufferImage, "png", os);
	}
}