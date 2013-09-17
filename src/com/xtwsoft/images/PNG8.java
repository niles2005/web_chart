package com.xtwsoft.images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 *	 
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class PNG8 extends MinimapImage {
	private static byte[] m_blankGifBytes = new byte[]{71,73,70,56,57,97,1,0,1,0,(byte)209,0,0,0,0,0,0,0,0,0,0,0,0,0,0,33,(byte)249,4,9,25,0,0,0,44,0,0,0,0,1,0,1,0,0,2,2,68,1,0,59};
	private byte[] m_plteData;
	private byte[][] m_pngDatas;
	private int m_compression = 9;
	private int m_transparentIndex = -1;
	private Hashtable m_colorNumHash = new Hashtable();
	protected CRC32 m_crc = new CRC32();

	public PNG8(BufferedImage bufferImage) {
		this(bufferImage,null);
	}
	
	public PNG8(BufferedImage bufferImage,Color transparentColor) {
		super(bufferImage);
		try {
			m_pngDatas = new byte[m_width][m_height];

			int[] palette = trueTo256(this.m_rgbDatas, m_pngDatas);
			if(transparentColor != null) {
				int iTransColor = transparentColor.getRGB();
				
				
				//����11�н�����ɫת��
//				int blue0 = (int) ((iTransColor >> 0) & 0xFF);
//				int green0 = (int) ((iTransColor >> 8) & 0xFF);
//				int red0 = (int) ((iTransColor >> 16) & 0xFF);
//				int blue = (int) blue0 >> 4;
//				int green = (int) green0 >> 4;
//				int red = (int) red0 >> 4;				
//				iTransColor = (blue << 8) + (green << 4) + (red);
//				
//				int red1 = ((iTransColor & 0x00f) << 4);
//				int green1 = ((iTransColor & 0x0f0));
//				int blue1 = ((iTransColor & 0xf00) >> 4);
//				iTransColor = new Color(red1, green1, blue1).getRGB();				
				
				for(int i=0;i<palette.length;i++) {
					if(palette[i] == iTransColor) {
						m_transparentIndex = i;
						break;
					}
				}
			}

			m_plteData = new byte[palette.length * 3];

			for (int i = 0; i < palette.length; i++) {
				int rgb = palette[i];
				m_plteData[i * 3] = (byte) ((rgb >> 16) & 0xFF);
				m_plteData[i * 3 + 1] = (byte) ((rgb >> 8) & 0xFF);
				m_plteData[i * 3 + 2] = (byte) ((rgb >> 0) & 0xFF);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setCompression(int compression) {
		m_compression = compression;
		if(m_compression < 0) {
			m_compression = 0;
		} else if(m_compression > 9) {
			m_compression = 9;
		}
	}
	
	private int[] trueTo256(int[][] rgbs, byte[][] pngData) {
		int[] colorHits = new int[4096];
		int lastIndex = -1;
		int lastRgb = Integer.MAX_VALUE;
		ColorNum lastColorNum = null;
        for (int y = 0; y < rgbs.length; y++) {
            for (int x = 0; x < rgbs[0].length; x++) {
                int rgb = rgbs[y][x];
				if (rgb == lastRgb) {
					colorHits[lastIndex]++;
					lastColorNum.num++;
				} else {
					Integer key = new Integer(rgb);
					ColorNum cn = (ColorNum) m_colorNumHash.get(key);
					if (cn == null) {
						cn = new ColorNum(rgb, 1);
						m_colorNumHash.put(key, cn);
					} else {
						cn.num += 1;
					}

					// R��G��B��ȡ4λ
					int blue0 = (int) ((rgb >> 0) & 0xFF);
					int green0 = (int) ((rgb >> 8) & 0xFF);
					int red0 = (int) ((rgb >> 16) & 0xFF);

					int blue = (int) blue0 >> 4;
					int green = (int) green0 >> 4;
					int red = (int) red0 >> 4;

					// ƴ��һ��12λ����
					int clrIndex = (blue << 8) + (green << 4) + (red);

					// ��Ӧ������Ԫ�ؼ�1
					colorHits[clrIndex]++;
					lastRgb = rgb;
					lastIndex = clrIndex;
					lastColorNum = cn;
				}
			}
		}
        
        int[] colorIndex = new int[4096];
		// ��Ϊ���Ԫ�������?
		int plteCounts = 0;
		for (int clrIndex = 0; clrIndex < 4096; clrIndex++) {
			if (colorHits[clrIndex] > 0) {
				// colorIndex�д�����г��ֹ����ɫֵ
				colorIndex[plteCounts] = clrIndex;
				// colorHits �д���ɫ�ĳ��ִ���
				colorHits[plteCounts] = colorHits[clrIndex];
				plteCounts++;
			}
		}
        
        
     // ����������4096����ɫ�����Դ���Ӷൽ������?
		for (int i = 0; i < plteCounts-1 || i<256; i++) {			
			int max = colorHits[i];			
			int changSite = i;			
			for (int j=i + 1 ; j < plteCounts; j++) {
				if (colorHits[j] > max) {
					max = colorHits[j];					
					changSite = j;
				}
			}
			int temp = colorIndex[i];
			colorIndex[i] = colorIndex[changSite];	
			colorIndex[changSite] = temp;
			// ����0-255��,�Ѿ�����Ƶ�ʴӸߵ��͵���ɫֵ	
			temp = colorHits[i];
			colorHits[i] = colorHits[changSite];	
			colorHits[changSite] = temp;
		}	
				
		int[] pal =  null;
		if (plteCounts > 256) {	
			pal = new int[256];
			for (int i = 0; i < 256; i++) {
				int red = ((colorIndex[i] & 0x00f) << 4);
				int green = ((colorIndex[i] & 0x0f0));
				int blue = ((colorIndex[i] & 0xf00) >> 4);
				pal[i] = new Color(red, green, blue).getRGB();
				// ColorHits��Ϊ��ɫ����������Ѿ�����ˣ�����������Ǽ��?2λ��
				// ��ֵ��Ӧ�ĵ�ɫ���е�����ֵ
				colorHits[i] = i;
			}
			// �������ɫ������Сƽ��������Ϊ�?56����ӽ��һ��			
			
			for (int i = 256; i < plteCounts; i++) {
				// ColorError1��¼��Сƽ����һ��ʼ��һ���ܴ��ֵ

				int colorError1 = 1000000000;

				// ��12λ����ֵ�õ�R��G��B�����?λֵ
				int blue = (int) ((colorIndex[i] & 0xf00) >> 4);
				int green = (int) ((colorIndex[i] & 0x0f0));
				int red = (int) ((colorIndex[i] & 0x00f) << 4);
				int clrIndex = 0;

				for (int j = 0; j < 256; j++) {
					int rr = (pal[j] >> 16) & 0xFF;
					int gg = (pal[j] >> 8) & 0xFF;
					int bb = (pal[j] >> 0) & 0xFF;

					// ColorError2���㵱ǰ��ƽ�����?
					int colorError2 = (blue - bb) * (blue - bb) + (green - gg)
							* (green - gg) + (red - rr) * (red - rr);
					if(colorError2 == 0){
						clrIndex = j; // ��¼��Ӧ�ĵ�ɫ��������?
						break;
					}else if (colorError2 < colorError1) { // �ҵ���С����
						colorError1 = colorError2;
						clrIndex = j; // ��¼��Ӧ�ĵ�ɫ��������?
					}
				}

				// ColorHits��¼12λ����ֵ��Ӧ�ĵ�ɫ���е�����ֵ
				colorHits[i] = clrIndex;
			}
		} else {
//			if(plteCounts <= 2) {
//				pal = new int[2];
//			} else if(plteCounts <= 4) {
//				pal = new int[4];
//			} else if(plteCounts <= 16) {
//				pal = new int[16];
//			} else {
				pal = new int[256];
//			}
			for (int i = 0; i < plteCounts; i++) {
				// ��12λ����ֵ�õ�R��G��B�����?λֵ
				int red = ((colorIndex[i] & 0x00f) << 4);
				int green = ((colorIndex[i] & 0x0f0));
				int blue = ((colorIndex[i] & 0xf00) >> 4);
				pal[i] = new Color(red, green, blue).getRGB();
				// ColorHits��Ϊ��ɫ����������Ѿ�����ˣ�����������Ǽ��?2λ��
				// ��ֵ��Ӧ�ĵ�ɫ��//�е�����ֵ
				colorHits[i] = i;
			}
		}
		int count = 0;
		for (int y = 0; y < rgbs.length; y++) {
			for (int x = 0; x < rgbs[0].length; x++) {

				// R��G��B��ȡ4λ
				int rgb = rgbs[y][x];
				int blue = ((rgb >> 0) & 0xFF) >> 4;
				int green = ((rgb >> 8) & 0xFF) >> 4;
				int red = ((rgb >> 16) & 0xFF) >> 4;
				// ƴ��һ��12λ����
				int clrIndex = (blue << 8) + (green << 4) + (red);
				for (int i = 0; i < plteCounts; i++) {
					if (clrIndex == colorIndex[i]) {
						count++;
						// ���?2����ֵȡ�ö�Ӧ�ĵ�ɫ���е�����ֵ
						pngData[y][x] = (byte) colorHits[i];
						break;
					}
				}
			}
		}
		ArrayList list = new ArrayList();
		Iterator iter = m_colorNumHash.values().iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		Collections.sort(list);

		m_colorNumHash.clear();

		for (int i = 0; i < list.size(); i++) {
			if (i >= 256) {
				break;
			}
			ColorNum colorNum = (ColorNum) list.get(i);
			if (colorNum.num < 5) {
				break;
			}
			int rgb = colorNum.color;
			int blue = ((rgb >> 0) & 0xFF) >> 4;
			int green = ((rgb >> 8) & 0xFF) >> 4;
			int red = ((rgb >> 16) & 0xFF) >> 4;
			int clrIndex = (blue << 8) + (green << 4) + (red);
			for (int j = 0; j < plteCounts; j++) {
				if (clrIndex == colorIndex[j]) {
					// ���?2����ֵȡ�ö�Ӧ�ĵ�ɫ���е�����ֵ

					Integer theIndex = new Integer(colorHits[j]);
					// ��ֹ�ظ�������ɫ��ֻ��������������?
					if (!m_colorNumHash.containsKey(theIndex)) {
						m_colorNumHash.put(theIndex, "");
						pal[colorHits[j]] = rgb;
					}
					break;
				}
			}
		}

		return pal;
	

	}
	

	public void writeToOutputStream(OutputStream os) throws IOException {
		//对于透明图片，直接输出一个像素的透明gif文件
		if(this.m_transparentIndex == 0 && this.m_colorNumHash.size() == 1) {
			os.write(this.m_blankGifBytes);
		} else {
			byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

			os.write(pngIdBytes);

			writeHeader(os);
			writePalette(os);
			writeData(os);
			writeEND(os);
		}
		
		
	}

	private void writeHeader(OutputStream os) throws IOException {
		byte[] datas = new byte[13];
		int pos = 0;
		System.arraycopy(getIntBytes(m_width), 0, datas, pos, 4);
		pos += 4;
		System.arraycopy(getIntBytes(m_height), 0, datas, pos, 4);
		pos += 4;
		datas[pos++] = 8;
		datas[pos++] = 3;
		datas[pos++] = 0;
		datas[pos++] = 0;
		datas[pos++] = 0;
		writePart("IHDR", datas, os);
	}
	
	protected void writePalette(OutputStream os) throws IOException {
//		byte[] newPlte = new byte[useCount];
//		for(int i=0;i<useCount;i++) {
//			newPlte[i] = m_plteData[i];
//		}
		writePart("PLTE", m_plteData, os);
		
		if(m_transparentIndex >= 0 && m_transparentIndex < 256) {
    		byte[] tRNSDatas = new byte[256];
    		for(int i=0;i<tRNSDatas.length;i++) {
    			tRNSDatas[i] = (byte)255;
    		}
    		tRNSDatas[m_transparentIndex] = 0;
    		writePart("tRNS", tRNSDatas, os);
		}
	}

	protected void writeData(OutputStream os) throws IOException {
		try {
			Deflater scrunch = new Deflater(m_compression);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			DeflaterOutputStream compBytes = new DeflaterOutputStream(baos,
					scrunch);
			 for (int j = 0; j < m_height; j++) {
				 compBytes.write((byte) 0);
				 for (int i = 0; i < m_width; i++) {
					 compBytes.write(m_pngDatas[i][j]);
				 }
			 }
			compBytes.close();

			byte[] osBytes = baos.toByteArray();
			writePart("IDAT", osBytes, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void writeEND(OutputStream os) throws IOException {
		writePart("IEND", null, os);
	}

	private void writePart(String key, byte[] datas, OutputStream os)
			throws IOException {
		if (datas == null) {
			os.write(getIntBytes(0));
		} else {
			os.write(getIntBytes(datas.length));
		}
		byte[] keyBytes = key.getBytes();
		os.write(keyBytes);
		m_crc.reset();
		m_crc.update(keyBytes);
		if (datas != null) {
			os.write(datas);
			m_crc.update(datas);
		}
		int crcValue = (int) m_crc.getValue();
		os.write(getIntBytes(crcValue));
	}

	protected byte[] getIntBytes(int n) {
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return temp;
	}
	
	public static void main(String[] args) {
		BufferedImage bufferImage = new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);
		try {
			Graphics2D g2 = (Graphics2D)bufferImage.getGraphics();
			g2.setColor(Color.red);
			g2.fillRect(0, 0, 256, 256);
			MinimapImage image = null;
			image = new PNG8(bufferImage, Color.red);
			byte[] bytes = image.getFileBytes();
			File f = new File("test.png");
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			bos.write(bytes);
			bos.flush();
			bos.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}
}
