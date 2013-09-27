package com.xtwsoft.wuproject.vmlChart;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class BaseVmlChart {
	protected int m_gridMarginLeft = 0;//网格与图片边界距离
	protected int m_gridMarginRight = 0;
	protected int m_gridMarginTop = 0;
	protected int m_gridMarginBottom = 0;
 
	protected int m_gridX = 0;//网格x
	protected int m_gridY = 0;//网格y
	protected int m_gridW = 0;//网格宽
	protected int m_gridH = 0;//网格高
	
    protected float m_yMin = 0;
    protected float m_yMax = 0;
	
	protected JSONObject m_legend;

	protected JSONObject m_xAxis;
	protected JSONArray m_xLabels = null;

	protected JSONObject m_yAxis;
	protected JSONArray m_yLabels = null;
	protected JSONArray m_keys = null;//图例标题数组
	protected JSONArray m_values = null;
	protected int m_width;//图片宽
	protected int m_height;//图片高
	
	
	public BaseVmlChart(JSONObject chartData,int width,int height) {
		m_gridMarginLeft = getInt(chartData,"grid-margin-left");
		m_gridMarginRight = getInt(chartData,"grid-margin-right");
		m_gridMarginTop = getInt(chartData,"grid-margin-top");
		m_gridMarginBottom = getInt(chartData,"grid-margin-bottom");
		
		
		m_legend = chartData.getJSONObject("legend");
		
		m_values = chartData.getJSONArray("values");
		m_keys = chartData.getJSONArray("keys");
		
		m_xAxis = chartData.getJSONObject("x_axis");
		m_xLabels = m_xAxis.getJSONArray("labels");

		m_yAxis = chartData.getJSONObject("y_axis");
		m_yLabels = m_yAxis.getJSONArray("labels");
		
		m_width = width;
		m_height = height;
		
		
		m_yMin = this.m_yAxis.getFloat("min");
		m_yMax = this.m_yAxis.getFloat("max");
		m_gridX = m_gridMarginLeft;
		m_gridY = m_gridMarginTop;
		m_gridW = width - m_gridMarginLeft - m_gridMarginRight;
		m_gridH = height - m_gridMarginTop - m_gridMarginBottom;
	}
	
	public double getDouble(JSONObject object,String name) {
		return getDouble(object,name,0);
	}
	
	public double getDouble(JSONObject object,String name,double defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).doubleValue();
		} else if(value instanceof Double) {
			return (Double)value;
		} else if(value instanceof Float) {
			return (Double)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}	
	public float getFloat(JSONObject object,String name) {
		return getFloat(object,name,0);
	}
	

	public float getFloat(JSONObject object,String name,float defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).floatValue();
		} else if(value instanceof Double) {
			return (Float)value;
		} else if(value instanceof Float) {
			return (Float)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}	
	
	public float getFloat(Object value) {
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).floatValue();
		} else if(value instanceof Double) {
			return (Float)value;
		} else if(value instanceof Float) {
			return (Float)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return 0;
	}	
	
	public int getInt(JSONObject object,String name) {
		return getInt(object,name,0);
	}
	

	public int getInt(JSONObject object,String name,int defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).intValue();
		} else if(value instanceof Double) {
			return ((Double)value).intValue();
		} else if(value instanceof Float) {
			return ((Float)value).intValue();
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}		
	
	public String getString(JSONObject object,String name,String defaultValue) {
		String value = object.getString(name);
		if(value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}		
	
	public abstract String buildChart();
}
