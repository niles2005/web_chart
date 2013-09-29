package com.xtwsoft.webchart.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlHBarStackChart extends BaseVmlChart {
	public VmlHBarStackChart(JSONObject chartData,int width,int height) {
		super(chartData,width,height);
	}
	
    public void drawChart(StringBuffer strBuff) {
    	drawXAxisAndLabels(strBuff);
    	drawYAxisAndLabels(strBuff);
        
    	drawHStackBarGroup(strBuff);
    }
	
    public void drawHStackBarGroup(StringBuffer strBuff) {
        for (int i = 0; i < m_values.size(); i++) {
        	JSONArray arr = m_values.getJSONArray(i);
        	JSONObject yLabel = m_yLabels.getJSONObject(i);
        	drawHStackBarItem(strBuff,yLabel,arr);
        }
    }

    public void drawHStackBarItem(StringBuffer strBuff,JSONObject yLabel,JSONArray arr) {
    	int yPos = this.getYPos(yLabel.getFloat("y"));
    	String text = yLabel.getString("text");
    	
    	int x0 = this.getXPos(0);
    	int x1 = 0;
    	
    	int h = this.m_subYBarWidth * 2;
    	float vv = 0;
    	int y0 = yPos - this.m_subYBarWidth;
    	
    	for(int i=0;i<arr.size();i++) {
    		JSONObject key = this.m_keys.getJSONObject(i);
    		String colour = key.getString("colour");
    		String name = key.getString("text");

    		vv += arr.getFloat(i);
    		x1 = this.getXPos(vv);
    		int w = x1 - x0;
    		
			String label = text + ":" + name + ":" + arr.getString(i);
        	
        	strBuff.append("<v:rect style=' WIDTH: " + w + "px;  HEIGHT: " + h + "px; TOP: " + y0 + "px; LEFT: " + x0 + "px' title=" + label + " coordsize = '21600,21600' fillColor='" + colour + "'>\r\n");
        	strBuff.append("<v:stroke opacity = '0'>\r\n");
			strBuff.append("</v:stroke>\r\n");
        	strBuff.append("</v:rect>\r\n");
        	
        	x0 += w;
    	}
    }
}
