package com.xtwsoft.webchart.vmlChart;

import java.util.List;

import com.xtwsoft.utils.Split;

public class VmlColor {
	private String m_color = null;
	private String m_vmlAlpha = null;
	public VmlColor(String color) {
		m_color = color;
		if(color.toLowerCase().startsWith("rgba")) {
			List list = Split.split(color, ",");
			if(list.size() == 4) {
				String str0 = (String)list.get(0);
				String newColor = "rgb" + str0.substring(4) + "," + list.get(1) + "," + list.get(2) + ")";
				m_color = newColor;
				String strAlpha = ((String)list.get(3)).trim();
				if(strAlpha.endsWith(")")) {
					strAlpha = strAlpha.substring(0,strAlpha.length() - 1);
				}
				float alpha = Float.parseFloat(strAlpha);
				if(alpha < 0) {
					alpha = 0;
				} else if(alpha > 1) {
					alpha = 1;
				}
				m_vmlAlpha = (int)(100 * alpha) + "%";
//				System.err.println(m_color);
//				System.err.println(m_vmlAlpha);
			}
		}
	}
	
	public String getColor() {
		return m_color;
	}
	
	public String getVmlAlpha() {
		return this.m_vmlAlpha;
	}
	public static void main(String[] args) {
		new VmlColor("rgba(255,0,0,0.75)");
	}
}
