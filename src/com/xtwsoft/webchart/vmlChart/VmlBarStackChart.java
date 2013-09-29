package com.xtwsoft.webchart.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlBarStackChart extends BaseVmlChart {
	public VmlBarStackChart(JSONObject chartData,int width,int height) {
		super(chartData,width,height);
	}
	
    public void drawChart(StringBuffer strBuff) {
    	drawXAxisAndLabels(strBuff);
    	drawYAxisAndLabels(strBuff);
        
    	drawStackBarGroup(strBuff);
    }
	
    public void drawStackBarGroup(StringBuffer strBuff) {
    	JSONObject[] keyArray = new JSONObject[m_keys.size()]; 
    	for(int i=0;i<m_keys.size();i++) {
    		JSONObject key = m_keys.getJSONObject(i);
    		keyArray[i] = key;
    	}
    	
        for (int i = 0; i < m_values.size(); i++) {
        	JSONArray arr = m_values.getJSONArray(i);
        	JSONObject xLabel = m_xLabels.getJSONObject(i);
        	drawStackBarItem(strBuff,xLabel,arr,keyArray);
        }
    }

    public void drawStackBarItem(StringBuffer strBuff,JSONObject xLabel,JSONArray arr,JSONObject[] keyArray) {
    	int xPos = xLabel.getInteger("xPos");
    	String text = xLabel.getString("text");
    	int x = xPos - m_subXBarWidth;
    	int w = m_subXBarWidth * 2;
    	float totalValue = 0;
    	for(int i=0;i<arr.size();i++) {
    		totalValue += arr.getFloat(i);
    	}
    	float value = 0;
    	int lastY = getYPos(0);
    	for(int i=0;i<keyArray.length;i++) {
    		JSONObject key = keyArray[i];
    		String colour = key.getString("colour");
    		String name = key.getString("text");
    		float theValue = arr.getFloatValue(i);
    		value += theValue;
        	int y = getYPos(value / totalValue * m_yMax);
			String label = text + ":" + name + ":" + arr.getString(i);
        	
        	int height = lastY - y;
        	strBuff.append("<v:rect style=' WIDTH: " + w + "px;  HEIGHT: " + height + "px; TOP: " + y + "px; LEFT: " + x + "px' title=" + label + " coordsize = '21600,21600' fillColor='" + colour + "'>\r\n");
        	strBuff.append("<v:stroke opacity = '0'>\r\n");
			strBuff.append("</v:stroke>\r\n");
        	strBuff.append("</v:rect>\r\n");
        	lastY = y;
    	}
    }
}
