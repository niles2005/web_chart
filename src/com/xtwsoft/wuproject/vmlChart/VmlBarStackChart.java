package com.xtwsoft.wuproject.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class VmlBarStackChart extends BaseVmlChart {
	private float m_barWidthRate = 0;//单个bar的柱子的宽度占整个宽柱子的比例，缺省是0.5
	private int m_subBarWidth = 0;//柱子一半的宽度
	public VmlBarStackChart(JSONObject chartData,int width,int height) {
		super(chartData,width,height);
		m_barWidthRate = getFloat(chartData,"bar-width",0.5f);
	}
	
	public String buildChart() {
		StringBuffer strBuff = new StringBuffer();
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
		
		strBuff.append("<div style='width:" + m_width + "px;position:relative;margin:0 auto;'>\r\n");
		strBuff.append("<v:group style='WIDTH: " + m_width + "px; HEIGHT: " + m_height + "px' coordsize = '" + m_width + "," + m_height + "'>\r\n");
		drawChart(strBuff);
		strBuff.append("</v:group>\r\n");
		strBuff.append("</div>\r\n");
		
		return strBuff.toString();
	}
	
    public int getYPos(float value) {
        double rate = 1.0 * (value - m_yMin) / m_yMax;
        return (int)(m_gridY + m_gridH - rate * m_gridH);
    }
	
    public void drawChart(StringBuffer strBuff) {
    	if(m_xLabels != null) {
            int xNum = m_xLabels.size();
            String gridColour = this.m_xAxis.getString("grid-colour");
            String poleColour = this.m_xAxis.getString("pole-colour");
            double xSpace = 1.0 * m_gridW / xNum;
            m_subBarWidth = (int)(xSpace * m_barWidthRate * 0.5);
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
        
    	drawStackBarGroup(strBuff);
    }
	
    public void drawLine(StringBuffer strBuff,int x1,int y1,int x2,int y2,String color,String strokeWeight,boolean isDash) {
    	String from = x1 + "," + y1;
    	String to = x2 + "," + y2;
    	strBuff.append("<v:line from = '" + from + "' to = '" + to + "' strokecolor = '" + color + "' strokeweight = '" + strokeWeight + "'>\r\n");
    	if(isDash) {
    		strBuff.append("<v:stroke dashstyle = 'dot'>\r\n");
			strBuff.append("</v:stroke>\r\n");
    	}                
    	strBuff.append("</v:line>\r\n");
    }
    
    public void drawLabel(StringBuffer strBuff,int x,int y,String text,String fontSize) {
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
    	int x = xPos - m_subBarWidth;
    	int w = m_subBarWidth * 2;
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
