package com.xtwsoft.images;

import java.awt.Color;

/**
 *	 
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class ColorNum implements java.lang.Comparable {
	public int color = 0;

	public int num = 0;

	public ColorNum(int color, int num) {
		this.color = color;
		this.num = num;
	}

	public int compareTo(Object obj) {
		ColorNum other = (ColorNum) obj;
		return other.num - this.num;
	}

	public String toString() {
		return new Color(color) + "   " + num;
	}
}