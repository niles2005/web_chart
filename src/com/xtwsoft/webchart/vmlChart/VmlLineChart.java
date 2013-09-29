package com.xtwsoft.webchart.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlLineChart extends BaseVmlChart {
	private String m_strokeWeight = null;
	private float m_drawPointRadius = 0;//折现上圆点半径，缺省值为4
	private float m_drawPointWhiteRadius = 0;//折现上圆点半径，缺省值为4
	public VmlLineChart(JSONObject chartData,int width,int height) {
		super(chartData,width,height);
		m_drawPointRadius = getFloat(chartData,"draw-point-radius",4);
        m_drawPointWhiteRadius = getFloat(chartData,"draw-point-white-radius",0);

		m_strokeWeight = getString(chartData,"stroke-weight","1");
	}
	
    public void drawChart(StringBuffer strBuff) {
    	drawXAxisAndLabels(strBuff);
    	drawYAxisAndLabels(strBuff);
    	
		for(int i=0;i<m_keys.size();i++) {
	    	JSONObject key = m_keys.getJSONObject(i);
	        drawPolyline(strBuff,i,key);
		}    	
    }
	
    public void drawPolyline(StringBuffer strBuff,int index,JSONObject key) {
    	String colour = key.getString("colour");
    	String name = key.getString("text");
    	StringBuffer tmpBuff = new StringBuffer();
        for (int i = 0; i < m_values.size(); i++) {
        	if(i > 0) {
        		tmpBuff.append(",");
        	}
        	JSONObject xLabel = m_xLabels.getJSONObject(i);
        	JSONArray arr = m_values.getJSONArray(i);
        	float v = arr.getFloatValue(index);
        	int yPos = getYPos(v);
        	
        	
        	tmpBuff.append(xLabel.getInteger("xPos") + "," + yPos);
        }

        int thisDrawPointRadius = (int)getFloat(key,"draw-point-radius",m_drawPointRadius) - 1;
        int thisDrawPointWhiteRadius = (int)getFloat(key,"draw-point-white-radius",m_drawPointWhiteRadius) - 1;
        //IE中画环的机制和canvas不一样，画出的环比canvas上大，所以同步减去1
        if(thisDrawPointRadius < 0) {
        	thisDrawPointRadius = 0;
        }
        if(thisDrawPointWhiteRadius < 0) {
        	thisDrawPointWhiteRadius = 0;
        }
        
    	strBuff.append("<v:polyline style='antialias: true' points = '" + tmpBuff.toString() + "' filled = 'f' strokecolor = '" + colour + "' strokeweight = '" + m_strokeWeight + "'>\r\n");
    	strBuff.append("</v:polyline>\r\n");
    	
        for (int i = 0; i < m_values.size(); i++) {
        	JSONObject xLabel = m_xLabels.getJSONObject(i);
        	JSONArray arr = m_values.getJSONArray(i);
        	float v = arr.getFloatValue(index);
        	
			String label = xLabel.getString("text") + ":" + name + ":" + arr.getString(index);
            if(thisDrawPointRadius > thisDrawPointWhiteRadius) {
            	int yPos = getYPos(v) - thisDrawPointRadius;
    			int xPos = xLabel.getInteger("xPos") - thisDrawPointRadius;

    			int width = thisDrawPointRadius * 2;
    			int height = thisDrawPointRadius * 2;
            	
    			strBuff.append("<v:oval style='WIDTH: " + width + "px; HEIGHT: " + height + "px; TOP: " + yPos + "px; LEFT: " + xPos + "px' title=" + label + " coordsize = '21600,21600' fillcolor = 'white' strokecolor = '" + colour + "' strokeweight = '1.5pt'>\r\n");
    			strBuff.append("</v:oval>\r\n");
            } else if(thisDrawPointRadius == thisDrawPointWhiteRadius) {//相等，白环不画
            	int yPos = getYPos(v) - thisDrawPointRadius;
    			int xPos = xLabel.getInteger("xPos") - thisDrawPointRadius;

    			int width = thisDrawPointRadius * 2;
    			int height = thisDrawPointRadius * 2;
            	
    			strBuff.append("<v:oval style='WIDTH: " + width + "px; HEIGHT: " + height + "px; TOP: " + yPos + "px; LEFT: " + xPos + "px' title=" + label + " coordsize = '21600,21600' fillcolor = '" + colour + "' >\r\n");
    			strBuff.append("</v:oval>\r\n");
            } else {
            	int yPos = getYPos(v) - thisDrawPointWhiteRadius;
    			int xPos = xLabel.getInteger("xPos") - thisDrawPointWhiteRadius;

    			int width = thisDrawPointWhiteRadius * 2;
    			int height = thisDrawPointWhiteRadius * 2;

    			strBuff.append("<v:oval style='WIDTH: " + width + "px; HEIGHT: " + height + "px; TOP: " + yPos + "px; LEFT: " + xPos + "px' title=" + label + " coordsize = '21600,21600' fillcolor = '" + colour + "' strokecolor = 'white' strokeweight = '1.5pt'>\r\n");
    			strBuff.append("</v:oval>\r\n");
            }        	
			
        }
    }
}
