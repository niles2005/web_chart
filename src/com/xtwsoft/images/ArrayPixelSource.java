/**
 * 
 *
 * History:
 *   Aug 17, 2009 5:55:33 PM Created by NieLei
 */
package com.xtwsoft.images;

import java.util.Arrays;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class ArrayPixelSource {
	private int pass;

	private int countdown;

	private int curx;

	private int cury;

	private int rgb[][];

	private int width;

	private int height;

	private int index[];

	private boolean interlace;

	public ArrayPixelSource(int rgb[][], int width, int height, int index[],
			boolean interlace) {
		this.rgb = rgb;
		this.width = width;
		this.height = height;
		this.index = index;
		this.interlace = interlace;
		pass = 0;
		curx = 0;
		cury = 0;
		countdown = width * height;
	}

	private void bumpPixel() {
		curx++;
		if (curx == width) {
			curx = 0;
			if (!interlace)
				cury++;
			else
				switch (pass) {
				default:
					break;

				case 0: // '\0'
					cury += 8;
					if (cury >= height) {
						pass++;
						cury = 4;
					}
					break;

				case 1: // '\001'
					cury += 8;
					if (cury >= height) {
						pass++;
						cury = 2;
					}
					break;

				case 2: // '\002'
					cury += 4;
					if (cury >= height) {
						pass++;
						cury = 1;
					}
					break;

				case 3: // '\003'
					cury += 2;
					break;
				}
		}
	}

	public int nextPixel() {
		if (countdown == 0) {
			return -1;
		} else {
			countdown--;
			byte r = (byte) Arrays.binarySearch(index, rgb[curx][cury]);
			bumpPixel();
			return r & 0xff;
		}
	}

}