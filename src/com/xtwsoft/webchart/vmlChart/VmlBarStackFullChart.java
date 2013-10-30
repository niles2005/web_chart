package com.xtwsoft.webchart.vmlChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlBarStackFullChart extends BaseVmlChart {
	public VmlBarStackFullChart(JSONObject chartData,int width,int height) {
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

        ArrayList valuesList = new ArrayList();
        for (int i = 0; i < m_values.size(); i++) {
        	JSONArray arr = m_values.getJSONArray(i);
        	valuesList.add(arr);
        }        
        Collections.sort(valuesList,new Comparator() {
        	public int compare(Object object1, Object object2) {
        		JSONArray arr1 = (JSONArray)object1;
        		JSONArray arr2 = (JSONArray)object2;
        		JSONObject xLabel1 = getXLabel(arr1.getString(0));
        		JSONObject xLabel2 = getXLabel(arr2.getString(0));
        		return xLabel1.getIntValue("x") - xLabel2.getIntValue("x");
        	}
        });
    	
        for (int i = 0; i < valuesList.size(); i++) {
        	JSONArray arr = (JSONArray)valuesList.get(i);
        	JSONObject xLabel = this.getXLabel(arr.getString(0));
        	drawStackBarItem(strBuff,xLabel,arr,keyArray);
        }
    }

    public void drawStackBarItem(StringBuffer strBuff,JSONObject xLabel,JSONArray arr,JSONObject[] keyArray) {
    	int xPos = xLabel.getInteger("xPos");
    	String text = xLabel.getString("text");
    	int x = xPos - m_subXBarWidth;
    	int w = m_subXBarWidth * 2;
    	float totalValue = 0;
    	for(int i=1;i<arr.size();i++) {
    		totalValue += arr.getFloat(i);
    	}
    	float value = 0;
    	int lastY = getYPos(0);
    	for(int i=0;i<keyArray.length;i++) {
    		JSONObject key = keyArray[i];
    		String colour = key.getString("colour");
    		String name = key.getString("text");
    		float theValue = arr.getFloatValue(i + 1);
    		value += theValue;
        	int y = getYPos(value / totalValue * m_yMax);
			String label = text + ":" + name + ":" + arr.getString(i + 1);
        	
        	int height = lastY - y;
        	strBuff.append("<v:rect style=' WIDTH: " + w + "px;  HEIGHT: " + height + "px; TOP: " + y + "px; LEFT: " + x + "px' title=" + label + " coordsize = '21600,21600' fillColor='" + colour + "'>\r\n");
        	strBuff.append("<v:stroke opacity = '0'>\r\n");
			strBuff.append("</v:stroke>\r\n");
        	strBuff.append("</v:rect>\r\n");
        	lastY = y;
    	}
    }
}
