package com.xtwsoft.webchart.charts;

import java.awt.Color;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;

public class BaseChart {
	public double getDouble(JSONObject object,String name) {
		Object value = object.get(name);
		if(value == null) {
			return 0;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).doubleValue();
		} else if(value instanceof Double) {
			return (Double)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return 0;
	}
	
	public double getInt(JSONObject object,String name) {
		Object splitLineWidth = object.get("split-line-width");
		if(splitLineWidth == null) {
			return 0;
		}
		return object.getDouble("split-line-width");
	}
	
	public Color getColor(JSONObject object,String name,Color defaultColor) {
		Color color = ChartUtil.getColor(object.getString(name));		
		if(color == null) {
			color = defaultColor;
		}
		return color;
	}

}
