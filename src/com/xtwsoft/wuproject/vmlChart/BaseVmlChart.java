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
	protected JSONObject m_chartData = null;
	
	public BaseVmlChart(JSONObject chartData,int width,int height) {
		m_chartData = chartData;
		m_width = width;
		m_height = height;
		m_gridMarginLeft = getInt(chartData,"grid-margin-left");
		m_gridMarginRight = getInt(chartData,"grid-margin-right");
		m_gridMarginTop = getInt(chartData,"grid-margin-top");
		m_gridMarginBottom = getInt(chartData,"grid-margin-bottom");
		
		
		m_legend = chartData.getJSONObject("legend");
		
		m_values = chartData.getJSONArray("values");
		m_keys = chartData.getJSONArray("keys");
		
		m_xAxis = chartData.getJSONObject("x-axis");
		m_xLabels = m_xAxis.getJSONArray("labels");

		m_yAxis = chartData.getJSONObject("y-axis");
		m_yLabels = m_yAxis.getJSONArray("labels");
		
		
		
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
	
	protected int getYPos(float value) {
        double rate = 1.0 * (value - m_yMin) / m_yMax;
        return (int)(m_gridY + m_gridH - rate * m_gridH);
    }

    protected void drawLine(StringBuffer strBuff,int x1,int y1,int x2,int y2,String color,String strokeWeight,boolean isDash) {
    	String from = x1 + "," + y1;
    	String to = x2 + "," + y2;
    	strBuff.append("<v:line from = '" + from + "' to = '" + to + "' strokecolor = '" + color + "' strokeweight = '" + strokeWeight + "'>\r\n");
    	if(isDash) {
    		strBuff.append("<v:stroke dashstyle = 'dot'>\r\n");
			strBuff.append("</v:stroke>\r\n");
    	}                
    	strBuff.append("</v:line>\r\n");
    }
    
    protected void drawLabel(StringBuffer strBuff,int x,int y,String text,String fontSize) {
    	if(text == null || text.length() == 0) {
    		return;
    	}
    	strBuff.append("<v:rect style=' WIDTH: 50px;  HEIGHT: 23px; TOP: " + y + "px; PADDING-TOP: 0px; LEFT: " + x + "px' coordsize = '21600,21600' stroked = 'f'>\r\n");
		strBuff.append("<v:stroke opacity = '0'>\r\n");
		strBuff.append("</v:stroke>\r\n");
		strBuff.append("<v:fill opacity = '0'>\r\n");
		strBuff.append("</v:fill>\r\n");
		strBuff.append("<v:textbox style=' FONT: " + fontSize + "pt Calibri; '>" + text + "</v:textbox>\r\n");
		strBuff.append("</v:rect>\r\n");
     }    
    
	protected void drawTitles(StringBuffer strBuff) {
		strBuff.append("<div style='width: " + m_width + "px;position:relative;margin:0 auto;text-align:center;'>\r\n");
		String legendIcon = this.m_legend.getString("icon");
		String iconStyle = "display: inline-block;width: 23px;height: 16px;vertical-align: top;background-image: url(/images/" + legendIcon + ".png);background-repeat: no-repeat;";
		for(int i=0;i<m_keys.size();i++) {
			JSONObject key = m_keys.getJSONObject(i);
			String colour = key.getString("colour");
			String text = key.getString("text");

			strBuff.append("<i style='" + iconStyle + "background-color:" + colour + "'></i><span>" + text + "</span>\r\n");
		}		
		strBuff.append("</div>\r\n");
	}
	
	public String buildChart() {
		StringBuffer strBuff = new StringBuffer();
		drawTitles(strBuff);
		strBuff.append("<div style='width:" + m_width + "px;position:relative;margin:0 auto;'>\r\n");
		strBuff.append("<v:group style='WIDTH: " + m_width + "px; HEIGHT: " + m_height + "px' coordsize = '" + m_width + "," + m_height + "'>\r\n");
		drawChart(strBuff);
		strBuff.append("</v:group>\r\n");
		strBuff.append("</div>\r\n");
		
		return strBuff.toString();
	}
	
	
    public abstract void drawChart(StringBuffer strBuff);
}
