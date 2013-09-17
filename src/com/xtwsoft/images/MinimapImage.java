/**
 * 
 */
package com.xtwsoft.images;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *	 
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public abstract class MinimapImage {
	
	protected int m_width;
	protected int m_height;
	protected int[][] m_rgbDatas;
	
	public MinimapImage(BufferedImage bufferImage) {
		try {
			m_width = bufferImage.getWidth();
			m_height = bufferImage.getHeight();
			m_rgbDatas = new int[m_width][m_height];
			for (int i = 0; i < m_width; i++) {
				for (int j = 0; j < m_height; j++) {
					int rgb = bufferImage.getRGB(i, j);
					m_rgbDatas[i][j] = rgb;
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public abstract void writeToOutputStream(OutputStream os) throws IOException;
	
	public byte[] getFileBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeToOutputStream(baos);
		return baos.toByteArray();
	}


}
