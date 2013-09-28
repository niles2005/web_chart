package com.xtwsoft.wuproject.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlLineChart extends BaseVmlChart {
	private String m_strokeWeight = null;
	private int m_drawPointRadius = 0;//折现上圆点半径，缺省值为4
	public VmlLineChart(JSONObject chartData,int width,int height) {
		super(chartData,width,height);
		m_drawPointRadius = getInt(chartData,"draw-point-radius",4);
		m_strokeWeight = getString(chartData,"stroke-weight","1");
	}
	
    public void drawChart(StringBuffer strBuff) {
    	if(m_xLabels != null) {
            int xNum = m_xLabels.size();
            String gridColour = this.m_xAxis.getString("grid-colour");
            String poleColour = this.m_xAxis.getString("pole-colour");
            double xSpace = 1.0 * m_gridW / xNum;
            for(int i=0;i<xNum;i++) {
                int xx = (int)(m_gridX + xSpace * i + 0.5 * xSpace);
                int y0 = m_gridY;
                int y1 = m_gridY + m_gridH;

				drawLine(strBuff,xx,y0,xx,y1,gridColour,"0.2pt",false);
				drawLine(strBuff,xx,y1,xx,y1 + 3,poleColour,"0.2pt",false);
				
				JSONObject xLabel = m_xLabels.getJSONObject(i);
				xLabel.put("xPos", xx);
				String xText = xLabel.getString("text");
				Object objShow = xLabel.get("show");
				boolean show = true;
				if(objShow == null ) {//缺省为空时，显示标题
					show = true;
				} else if(objShow instanceof Boolean) {
					show = ((Boolean)objShow).booleanValue();
				}
				if(show) {
				    drawLabel(strBuff,xx - 18,y1,xText,"8");
				}
            }
    	}

    	if(m_yLabels != null) {
            String gridColour = this.m_yAxis.getString("grid-colour");
            int yNum = m_yLabels.size();
            for(int i=0;i<yNum;i++) {
                int yy = getYPos(1.0f * i / (yNum - 1) * m_yMax);
                drawLine(strBuff,m_gridX,yy,m_gridX + m_gridW,yy,gridColour,"0.3pt",i != 0);
                
                JSONObject yLabel = m_yLabels.getJSONObject(i);
                drawLabel(strBuff,m_gridX - 28,yy-12,yLabel.getString("text"),"9");
            }
    	}
        
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

    	strBuff.append("<v:polyline style='antialias: true' points = '" + tmpBuff.toString() + "' filled = 'f' strokecolor = '" + colour + "' strokeweight = '" + m_strokeWeight + "'>\r\n");
    	strBuff.append("</v:polyline>\r\n");
    	
        for (int i = 0; i < m_values.size(); i++) {
        	JSONObject xLabel = m_xLabels.getJSONObject(i);
        	JSONArray arr = m_values.getJSONArray(i);
        	float v = arr.getFloatValue(index);
        	int yPos = getYPos(v) - m_drawPointRadius;
			int xPos = xLabel.getInteger("xPos") - m_drawPointRadius;

			int width = m_drawPointRadius * 2;
			int height = m_drawPointRadius * 2;
			
			String label = xLabel.getString("text") + ":" + name + ":" + arr.getString(index);
			strBuff.append("<v:oval style='WIDTH: " + width + "px; HEIGHT: " + height + "px; TOP: " + yPos + "px; LEFT: " + xPos + "px' title=" + label + " coordsize = '21600,21600' fillcolor = '" + colour + "' strokecolor = 'white' strokeweight = '1.5pt'>\r\n");
			strBuff.append("</v:oval>\r\n");
        }
    }
}
