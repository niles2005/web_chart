package com.xtwsoft.webchart.sketch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TrunkRoad {
	private JSONObject m_chartData;
	private int m_imageWidth;
	private int m_imageHeight;
	private int xScope ;
	private double widthPerSec ;
	private int thickness;
	private int gap;
	private Map<String,JSONObject> crossingMap = new HashMap<String,JSONObject>();
	private static final int m_marginWidth = 32;
	private static final int m_marginHeight = 28;
	private static final int lengendWidth= 60;
	private static final int crossingNameWidth= 50;

	
	
	public TrunkRoad(JSONObject chartData,int imageWidth,int imageHeight) {
		m_chartData = chartData;
		m_imageWidth = imageWidth;
		m_imageHeight = imageHeight;
		thickness = imageHeight / 40;
		
		
		JSONArray crossingList = m_chartData.getJSONArray("intersectionMsgList");
		if(crossingList != null && crossingList.size() > 0) {
			gap = (m_imageHeight - 2*m_marginHeight) / crossingList.size();
			for (Object crossing : crossingList) {
				 JSONObject JSONCrossing  = (JSONObject)crossing;
				 crossingMap.put(JSONCrossing.getString("intersection_id"), JSONCrossing);
				 
				 String cycle = JSONCrossing.getString("cycle_information");
				 String [] cycleSections = cycle.split("\\|");
				 int sec = 0;
				 for( String signal : cycleSections){
					 int subIndex = signal.indexOf(":");
					 if(subIndex >= 0) {
						 sec += Integer.parseInt(signal.substring(subIndex+1));
					 }
				 }
				 if(sec > xScope) {
					 xScope = sec;
				 }
			}
			widthPerSec = (m_imageWidth - (m_marginWidth * 2) - lengendWidth - crossingNameWidth) * 1.0/xScope ;
		}
	}
	
	
	
	public void draw(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		String endId = m_chartData.getString("end_intersection");
		String interSectionId = m_chartData.getString("start_intersection");
		int loop = 0;
		while (true) {
			JSONObject JSONCrossing = crossingMap.get(interSectionId);
			g2.translate(m_marginWidth,m_marginHeight + gap/2 + loop*gap);
			drawInterval(g2,JSONCrossing);
			g2.translate(-m_marginWidth,-(m_marginHeight + gap/2 + loop*gap));
			if (!interSectionId.equals(endId)) {
				interSectionId = JSONCrossing.getString("next_intersection");
				loop++;
			}else{
				drawTrunk(g2,JSONCrossing.getString("intersection_name"));
				break;
			}
		} 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private void drawInterval(Graphics2D g2,JSONObject JSONCrossing ){

		g2.translate(0,thickness/2);
		Stroke defaultStroke = g2.getStroke();
		
		g2.setColor(Color.gray);
		g2.setStroke(new BasicStroke(thickness+2,BasicStroke.CAP_BUTT ,BasicStroke.JOIN_MITER));
		g2.drawLine(crossingNameWidth+8, 0, lengendWidth+crossingNameWidth-8 , 0);
		g2.drawLine(lengendWidth/2+crossingNameWidth, -gap/2, lengendWidth/2+crossingNameWidth, gap/2);
		
		g2.setStroke(new BasicStroke(thickness ,BasicStroke.CAP_BUTT ,BasicStroke.JOIN_MITER));
		g2.setColor(Color.white);
		g2.drawLine(crossingNameWidth+8-1, 0, lengendWidth+crossingNameWidth-8+1  , 0);
		g2.drawLine(lengendWidth/2+crossingNameWidth, -gap/2 - 1, lengendWidth/2+crossingNameWidth, gap/2 + 1);
		g2.translate(0,-thickness/2);
		
		
		
		
		String crossingName = JSONCrossing.getString("intersection_name");
		
		g2.translate(0,thickness );
		if(crossingName.split("/").length >=1 ) {
			crossingName = crossingName.split("/")[0];
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			g2.setColor(Color.GRAY);
			g2.drawString(crossingName, 0, 0);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g2.translate(0,-(thickness));
		
		
		g2.setStroke(defaultStroke);
		g2.setColor(new Color(235,80,80));
		g2.fillRect(lengendWidth+crossingNameWidth, 0,  m_imageWidth - (m_marginWidth * 2) - lengendWidth - crossingNameWidth ,thickness);
		g2.setColor(Color.white);
		g2.drawRect(lengendWidth+crossingNameWidth, 0,  m_imageWidth - (m_marginWidth * 2) - lengendWidth - crossingNameWidth ,thickness);
		
		String cycle = JSONCrossing.getString("cycle_information");
		String[] cycleSections = cycle.split("\\|");
		int offsetX = lengendWidth+crossingNameWidth;
		for (String signal : cycleSections) {
			int subIndex = signal.indexOf(":");
			if (subIndex >= 0) {
				String colorSignal = signal.substring(0, subIndex);
				int signalWidth = (int)Math.round(Integer.parseInt(signal.substring(subIndex + 1)) * widthPerSec);
				if ("G".equals(colorSignal)) {
					g2.setColor(new Color(40,195,185));
					g2.fillRect(offsetX, 0, signalWidth ,thickness);
					g2.setColor(Color.white);
					
					g2.drawRect(offsetX, 0, signalWidth ,thickness);
				} 

				offsetX += signalWidth;
			}
		}
	}
	
	private void drawTrunk(Graphics2D g2, String trunkName){
		if(trunkName.split("/").length >=2 ) {
			g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			g2.setColor(Color.GRAY);
			
			FontMetrics fm = g2.getFontMetrics();
			String trunk = trunkName.split("/")[1];
			Rectangle2D rect = fm.getStringBounds(trunk, g2);
			int roadNameHalfWidth = (int)(rect.getWidth()/2);
			int roadNameHeight = (int)rect.getHeight();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.drawString(trunk,  m_marginWidth+lengendWidth+crossingNameWidth/2 - roadNameHalfWidth , m_marginHeight);
			g2.drawString(trunk,  m_marginWidth+lengendWidth+crossingNameWidth/2 - roadNameHalfWidth , m_imageHeight-m_marginHeight+roadNameHeight);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	
		
	}
	
}
