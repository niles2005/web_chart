package com.xtwsoft.webchart;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;

public class ChartUtil {
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
