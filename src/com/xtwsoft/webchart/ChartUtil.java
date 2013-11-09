package com.xtwsoft.webchart;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;

public class ChartUtil {
	public static Color getColor(String colorString,double alpha) {
		if(colorString == null) {
			return null;
		}
		if(colorString.startsWith("#") && colorString.length() == 7) {
			int colorValue = Integer.parseInt(colorString.substring(1),16);
			Color c = new Color(colorValue);
			if(alpha >= 1 || alpha <= 0) {
				return c;
			}
			if(alpha != 1) {
				c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(255 * alpha));
			}
			return c;
		}
		try {
			Field field = Color.class.getField(colorString);
			Color c = (Color)field.get(null);
			if(alpha >= 1 || alpha <= 0) {
				return c;
			}
			if(alpha != 1) {
				c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(255 * alpha));
			}
			return c;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Color getColor(String colorString) {
		if(colorString == null) {
			return null;
		}
		if(colorString.startsWith("#") && colorString.length() == 7) {
			int colorValue = Integer.parseInt(colorString.substring(1),16);
			return new Color(colorValue);
		}
		try {
			
			System.err.println(colorString);
			Field field = Color.class.getField(colorString);
			return (Color)field.get(null);
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static File AppPath = null;
	public static void intWebAppPath(String strAppPath) {
		AppPath = new File(strAppPath);
	}
	
	
	public static void main(String[] args) {
		Color c = ChartUtil.getColor("#FF0000");
		System.err.println(c);
		c = ChartUtil.getColor("blue");
		System.err.println(c);
	}
}
