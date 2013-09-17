/**
 * 
 *
 * History:
 *   Aug 17, 2009 5:55:33 PM Created by NieLei
 */
package com.xtwsoft.images;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class GIF extends MinimapImage {

	private int m_palette[];

	private int m_transparentIndex;

	private int m_bitsPerPixel;
	
	private String m_comment = "";
	
	private boolean m_interlace = false;

	public GIF(BufferedImage bufferImage) {
		this(bufferImage,null);
	}

	public GIF(BufferedImage bufferImage,Color transparentColor) {
		super(bufferImage);
		m_transparentIndex = -1;
		trueTo256(this.m_rgbDatas);

		initColorIndex();
		initBitsPerPixel();
		if(transparentColor != null) {
			int iTransColor = transparentColor.getRGB();
			for(int i=0;i<m_palette.length;i++) {
				if(m_palette[i] == iTransColor) {
					m_transparentIndex = i;
					break;
				}
			}
		}
	}
	
	public void setInterlace(boolean interlace) {
		m_interlace = interlace;
	}

	private int[] trueTo256(int[][] rgbs) {
		byte[][] pngData = new byte[m_width][m_height];
		Hashtable colorNumHash = new Hashtable();
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
					ColorNum cn = (ColorNum) colorNumHash.get(key);
					if (cn == null) {
						cn = new ColorNum(rgb, 1);
						colorNumHash.put(key, cn);
					} else {
						cn.num += 1;
					}

					// R，G，B各取4位
					int blue0 = (int) ((rgb >> 0) & 0xFF);
					int green0 = (int) ((rgb >> 8) & 0xFF);
					int red0 = (int) ((rgb >> 16) & 0xFF);

					int blue = (int) blue0 >> 4;
					int green = (int) green0 >> 4;
					int red = (int) red0 >> 4;

					// 拼成一个12位整数
					int clrIndex = (blue << 8) + (green << 4) + (red);

					// 相应的数组元素加1
					colorHits[clrIndex]++;
					lastRgb = rgb;
					lastIndex = clrIndex;
					lastColorNum = cn;
				}
			}
		}

		int[] colorIndex = new int[4096];

		// 将为零的元素清除出去
		int plteCounts = 0;
		for (int clrIndex = 0; clrIndex < 4096; clrIndex++) {
			if (colorHits[clrIndex] != 0) {
				colorHits[plteCounts] = colorHits[clrIndex];

				// 注意调整相应的索引值
				colorIndex[plteCounts] = clrIndex;
				plteCounts++; // 颜色数加1
			}
		}

		// 用起泡排序将PalCounts种颜色按从大到小的顺序排列
		for (int i = 0; i < plteCounts - 1; i++) {
			for (int j = i + 1; j < plteCounts; j++) {
				if (colorHits[j] > colorHits[i]) {
					int temp = colorHits[i];
					colorHits[i] = colorHits[j];
					colorHits[j] = temp;
					// 注意调整相应的索引值
					temp = colorIndex[i];
					colorIndex[i] = colorIndex[j];
					colorIndex[j] = temp;
				}
			}
		}

		int[] pal = null;
		// 其余的颜色依据最小平方误差近似为前256中最接近的一种
		if (plteCounts > 256) {
			pal = new int[256];
			for (int i = 0; i < 256; i++) {
				// 由12位索引值得到R，G，B的最高4位值
				int red = ((colorIndex[i] & 0x00f) << 4);
				int green = ((colorIndex[i] & 0x0f0));
				int blue = ((colorIndex[i] & 0xf00) >> 4);
				pal[i] = new Color(red, green, blue).getRGB();
				// ColorHits作为颜色记数的作用已经完成了，下面的作用是记录12位索
				// 引值对应的调色板//中的索引值
				colorHits[i] = i;
			}

			for (int i = 256; i < plteCounts; i++) {
				// ColorError1记录最小平方误差，一开始赋一个很大的值

				int colorError1 = 1000000000;

				// 由12位索引值得到R，G，B的最高4位值
				int blue = (int) ((colorIndex[i] & 0xf00) >> 4);
				int green = (int) ((colorIndex[i] & 0x0f0));
				int red = (int) ((colorIndex[i] & 0x00f) << 4);
				int clrIndex = 0;

				for (int j = 0; j < 256; j++) {
					int rr = (pal[j] >> 16) & 0xFF;
					int gg = (pal[j] >> 8) & 0xFF;
					int bb = (pal[j] >> 0) & 0xFF;

					// ColorError2计算当前的平方误差
					int colorError2 = (blue - bb) * (blue - bb) + (green - gg)
							* (green - gg) + (red - rr) * (red - rr);

					if (colorError2 < colorError1) { // 找到更小的了
						colorError1 = colorError2;
						clrIndex = j; // 记录对应的调色板的索引值
					}
				}

				// ColorHits记录12位索引值对应的调色板中的索引值
				colorHits[i] = clrIndex;
			}
		} else {
			pal = new int[256];
			for (int i = 0; i < plteCounts; i++) {
				// 由12位索引值得到R，G，B的最高4位值
				int red = ((colorIndex[i] & 0x00f) << 4);
				int green = ((colorIndex[i] & 0x0f0));
				int blue = ((colorIndex[i] & 0xf00) >> 4);
				pal[i] = new Color(red, green, blue).getRGB();
				// ColorHits作为颜色记数的作用已经完成了，下面的作用是记录12位索
				// 引值对应的调色板//中的索引值
				colorHits[i] = i;
			}
		}

		for (int y = 0; y < rgbs.length; y++) {
			for (int x = 0; x < rgbs[0].length; x++) {

				// R，G，B各取4位
				int rgb = rgbs[y][x];
				int blue = ((rgb >> 0) & 0xFF) >> 4;
				int green = ((rgb >> 8) & 0xFF) >> 4;
				int red = ((rgb >> 16) & 0xFF) >> 4;
				// 拼成一个12位整数
				int clrIndex = (blue << 8) + (green << 4) + (red);
				for (int i = 0; i < plteCounts; i++) {
					if (clrIndex == colorIndex[i]) {
						// 根据12索引值取得对应的调色板中的索引值
						pngData[y][x] = (byte) colorHits[i];
						break;
					}
				}
			}
		}

		ArrayList list = new ArrayList();
		Iterator iter = colorNumHash.values().iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		Collections.sort(list);

		colorNumHash.clear();

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
					// 根据12索引值取得对应的调色板中的索引值

					Integer theIndex = new Integer(colorHits[j]);
					// 防止重复设置颜色，只需最高数量的颜色
					if (!colorNumHash.containsKey(theIndex)) {
						colorNumHash.put(theIndex, "");
						pal[colorHits[j]] = rgb;
					}
					break;
				}
			}
		}
		for(int j=0;j<m_height;j++) {
			for(int i=0;i<m_width;i++) {
				int index = pngData[i][j];
				if(index < 0) {
					index += 256;
				}
				this.m_rgbDatas[i][j] = pal[index];
			}
		}

		return pal;
	}

	private void initBitsPerPixel() {
		if (m_palette.length <= 2)
			m_bitsPerPixel = 1;
		else if (m_palette.length <= 4)
			m_bitsPerPixel = 2;
		else if (m_palette.length <= 16)
			m_bitsPerPixel = 4;
		else
			m_bitsPerPixel = 8;
	}

	private boolean contains(int list[], int value) {
		for (int i = 0; i < list.length; i++)
			if (list[i] == value)
				return true;

		return false;
	}

	private void initColorIndex() {
		int list[] = new int[256];
		int colorIndex = 0;
		for (int y = 0; y < m_height; y++) {
			for (int x = 0; x < m_width; x++) {
				int rgb = m_rgbDatas[x][y];
				if (contains(list, rgb))
					continue;
				if (colorIndex >= 256)
					throw new RuntimeException(
							"Too many colors for a GIF use NeuQuantQuantizerOP to reduce the number of colors used.");
				list[colorIndex] = rgb;
				colorIndex++;
			}
		}

		m_palette = new int[colorIndex];
		System.arraycopy(list, 0, m_palette, 0, colorIndex);
		Arrays.sort(m_palette);
	}

	public int getBackground() {
		return 0;
	}

	public void writeToOutputStream(OutputStream os) throws IOException {
		int initCodeSize = m_bitsPerPixel > 1 ? m_bitsPerPixel : 2;
		writeGifHeader(m_width, m_height, m_bitsPerPixel, (byte) getBackground(), os);
		writeColorMap(m_palette, m_bitsPerPixel, os);
		writeTransparentExtention(m_transparentIndex, os);
		writeImageSeperator(os);
		writeImageHeader(m_width, m_height, m_interlace,
				initCodeSize, os);
		writeImageData(m_rgbDatas, m_width, m_height, m_palette, m_interlace, initCodeSize, os);
		writeSeriesEnder(os);
		writeComment(m_comment, os);
		writeGifTerminator(os);

	}

	private byte[] toWord(int number) {
		byte result[] = new byte[2];
		result[0] = (byte) (number & 0xff);
		result[1] = (byte) (number >> 8 & 0xff);
		return result;
	}

	private void writeGifHeader(int width, int height, int bitsPerPixel,
			byte background, OutputStream dos) throws IOException {
		dos.write("GIF89a".getBytes());
		dos.write(toWord(width));
		dos.write(toWord(height));
		byte indicator = -128;
		indicator |= 0x70;
		indicator |= (byte) (bitsPerPixel - 1);
		dos.write(indicator);
		dos.write(background);
		dos.write(49);
	}

	private void writeColorMap(int index[], int bitsPerPixel, OutputStream dos)
			throws IOException {
		int mapSize = 1 << bitsPerPixel;
		for (int i = 0; i < mapSize; i++)
			if (i >= index.length) {
				dos.write(0);
				dos.write(0);
				dos.write(0);
			} else {
				dos.write((byte) (index[i] >> 16 & 0xff));
				dos.write((byte) (index[i] >> 8 & 0xff));
				dos.write((byte) (index[i] & 0xff));
			}

	}

	private void writeTransparentExtention(int transparentIndex, OutputStream dos)
			throws IOException {
		if (transparentIndex != -1)
			dos.write(new byte[] { 33, -7, 4, 1, 0, 0, (byte) transparentIndex,
					0 });
	}

	private void writeImageHeader(int width, int height, boolean interlace,
			int initCodeSize, OutputStream dos) throws IOException {
		dos.write(toWord(0));
		dos.write(toWord(0));
		dos.write(toWord(width));
		dos.write(toWord(height));
		dos.write(interlace ? 64 : 0);
		dos.write((byte) initCodeSize);
	}

	private void writeImageData(int pixels[][], int width, int height,
			int index[], boolean interlace, int initCodeSize, OutputStream out)
			throws IOException {
		ArrayPixelSource source = new ArrayPixelSource(pixels, width, height, index,
				interlace);
		(new LZWEncoder(initCodeSize + 1, source, out)).compress();
	}

	private void writeImageSeperator(OutputStream dos) throws IOException {
		dos.write(44);
	}

	private void writeSeriesEnder(OutputStream dos) throws IOException {
		dos.write(0);
	}

	private void writeComment(String comment, OutputStream dos)
			throws IOException {
		if (comment != null && comment.length() > 0) {
			dos.write(new byte[] { 33, -2, (byte) comment.length() });
			dos.write(comment.getBytes());
			dos.write(0);
		}
	}

	private void writeGifTerminator(OutputStream dos) throws IOException {
		dos.write(59);
	}
}
