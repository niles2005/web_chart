package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;


public class DashboardChart extends BaseChart {
	public DashboardChart() {
	}
	
	public void drawElement(Graphics2D g2,int width,int height,JSONObject element) {
		int pieRadius = 0;

		int chartPosX,chartPosY;
		
		if(width > height * 2) {
			chartPosX = width / 2;
			chartPosY = height;
			pieRadius = height;
		} else {
			chartPosX = width / 2;
			chartPosY = height;
			pieRadius = width / 2;
		}
		
		g2.translate(chartPosX, chartPosY);
		
		drawPie(g2,element,pieRadius);
//		drawPieLabel(g2,width,height,element,pieRadius,totalValue);
		
		g2.translate(-chartPosX, -chartPosY);
		
	}
	
	private void drawPie(Graphics2D g2,JSONObject element,int pieRadius) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		JSONArray values = element.getJSONArray("values");
		int valueCount = values.size();
		
		int eachAngle = 15;
		int r = pieRadius;
		int totalAngle = eachAngle * valueCount;
		if(totalAngle > 180) {
			totalAngle = 180;
			eachAngle = totalAngle / valueCount;
		}
		int startAnglue0 = (180 - totalAngle) / 2;//(int)this.getDouble(element,"start-angle");
		int startAnglue = startAnglue0; 
		for(int i=0;i<valueCount;i++) {
			JSONObject value = values.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(value.getString("colour")));
			int angle = eachAngle;
			
			g2.fillArc(-r, -r, r * 2, r * 2, startAnglue, angle);
			startAnglue += angle;
		}
		drawPieHoleAndLine(g2,element,pieRadius,startAnglue0,eachAngle);
	}
	
	private void drawPieHoleAndLine(Graphics2D g2,JSONObject element,int pieRadius,int fromAnglue,int eachAngle) {
		double holePosRate = getDouble(element,"hole-pos-rate");
		if(holePosRate > 0) {
			int innerR = (int)(pieRadius * holePosRate);
			g2.setColor(Color.white);
			g2.fillOval(-innerR, -innerR, innerR * 2, innerR * 2);
		}
		
		double splitLineWidth = getDouble(element,"split-line-width");
		if(splitLineWidth > 0) {
			Stroke defaultStroke = g2.getStroke();
			if(splitLineWidth > 0) {
				BasicStroke stroke = new BasicStroke((float)splitLineWidth);
				g2.setStroke(stroke);
			}

			JSONArray values = element.getJSONArray("values");
			int valueCount = values.size();
			int r = pieRadius;//84;

			int startAnglue0 = fromAnglue;//(int)this.getDouble(element,"start-angle");
			int startAnglue = startAnglue0; 
			for(int i=0;i<valueCount;i++) {
				int angle = eachAngle;
				
				double x =  r * Math.cos(startAnglue * Math.PI / 180);
				double y =  -r * Math.sin(startAnglue * Math.PI / 180);
				g2.drawLine(0, 0, (int)x, (int)y);
				
				startAnglue += angle;
			}
			g2.setStroke(defaultStroke);
		}
	}
//	
//	private void drawPieLabel(Graphics2D g2,int width,int height,JSONObject element,int pieRadius) {
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//		int startAnglue0 = (int)this.getDouble(element,"start-angle");
//		int startAnglue = startAnglue0; 
//		int totalAngle = 360 + startAnglue0;
//		
//		JSONArray values = element.getJSONArray("values");
//		int valueCount = values.size();
//		double labelPosRate = element.getDoubleValue("label-pos-rate");
//		int labelR = (int)(pieRadius * labelPosRate);
//		FontMetrics fm = g2.getFontMetrics();
//		for(int i=0;i<valueCount;i++) {
//			JSONObject value = values.getJSONObject(i);
//			int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
//			if(i == valueCount - 1) {
//				angle = totalAngle - startAnglue;
//			}
//			int middleArc = startAnglue + angle / 2;
//			Rectangle2D rect = fm.getStringBounds(value.getString("label"), g2);
//			
//			double x =  labelR * Math.cos(middleArc * Math.PI / 180);// - rect.getWidth() / 2;
//			double y =  -labelR * Math.sin(middleArc * Math.PI / 180);// - rect.getHeight() / 2;
//			g2.translate(x, y);
//			g2.setColor(getColor(value,"label-colour",Color.white));
//			g2.drawString(value.getString("label"), -(int)(rect.getWidth()/2), (int)(rect.getHeight()/4 ));
//			
//			startAnglue += angle;
//			g2.translate(-x, -y);
//		}
//	}
	

}
