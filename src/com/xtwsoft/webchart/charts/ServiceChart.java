package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;


public class ServiceChart extends BaseChart {
	public ServiceChart() {
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
		double islandPosRate = getDouble(element,"island-pos-rate");
		int islandRadius = (int)(pieRadius * islandPosRate);
		chartPosY -= islandRadius + height / 20;
		pieRadius -= height / 20;
		g2.translate(chartPosX, chartPosY);
		
		drawPie(g2,element,pieRadius,islandRadius);
//		drawPieLabel(g2,width,height,element,pieRadius,totalValue);
		
		g2.translate(-chartPosX, -chartPosY);
		
	}
	
	private void drawPie(Graphics2D g2,JSONObject element,int pieRadius,int islandRadius) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font newFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		g2.setFont(newFont);
		JSONArray values = element.getJSONArray("values");
		String selectValue = null;
		float selectValueFontSize = 0;
		if(values.size() > 0) {
			JSONObject value = values.getJSONObject(0);
			selectValue = value.getString("value");
			selectValueFontSize = value.getFloatValue("font-size");
		}
		if(selectValue == null) {
			selectValue = "";
		}
		JSONArray keys = element.getJSONArray("keys");
		int keyCount = keys.size();
		
		int eachAngle = 25;
		int r = pieRadius;
		int totalAngle = eachAngle * keyCount;
		if(totalAngle > 180) {
			totalAngle = 180;
			eachAngle = totalAngle / keyCount;
		}
		int startAnglue0 = (180 - totalAngle) / 2;//(int)this.getDouble(element,"start-angle");
		int startAnglue = startAnglue0; 
		int selectLabelAngle = 0;
		JSONObject selectKey = null;
		for(int i=keyCount-1;i>=0;i--) {
			JSONObject key = keys.getJSONObject(i);
			Color realColor = ChartUtil.getColor(key.getString("colour"));
			Color alphaColor = ChartUtil.getColor(key.getString("colour"),0.3);
			int angle = eachAngle;
			
			int labelAngle = startAnglue + angle / 2;
			String text = key.getString("text");
			if(selectKey == null && selectValue.equals(text)) {
				selectKey = key;
				selectLabelAngle = labelAngle;
				g2.setColor(realColor);
			} else {
				g2.setColor(alphaColor);
			}
			g2.fillArc(-r, -r, r * 2, r * 2, startAnglue, angle);
			g2.setColor(realColor);
			
			float fontSize = selectValueFontSize;
			if(fontSize <= 0) {
				fontSize = selectKey.getFloatValue("font-size");
			}
			if(fontSize > 0) {
				g2.setFont(g2.getFont().deriveFont(fontSize));
			}
			double x =  pieRadius * Math.cos(labelAngle * Math.PI / 180);
			double y =  -pieRadius * Math.sin(labelAngle * Math.PI / 180);
			g2.translate(x, y);

			float alpha = (float) ((labelAngle - 90)/ 180.0 * Math.PI);
			if (alpha != 0) {
				g2.rotate(-alpha);
			}
			
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(text, g2);
			int posX = -(int)(rect.getWidth() / 2);
			int posY = (int)(fm.getAscent() - rect.getHeight());
			
			g2.drawString(text, posX, posY);
			if (alpha != 0) {
				g2.rotate(alpha);
			}
			g2.translate(-x, -y);
			
			startAnglue += angle;
		}

		//draw pie circle point画伞形上的虚的小圆点
		int piePointR = (int)(pieRadius * 0.85);
		g2.setColor(new Color(255,0,0,10));
		startAnglue = startAnglue0;
		int circlePointR = (int)(pieRadius * 0.025);
		for(int i=0;i<10;i++) {
			int angle = eachAngle / 2;
			
			int piePointAngle = startAnglue + angle / 2;
			double x =  piePointR * Math.cos(piePointAngle * Math.PI / 180);
			double y =  -piePointR * Math.sin(piePointAngle * Math.PI / 180);
			
			g2.translate(x, y);

			g2.fillOval(-circlePointR, -circlePointR, circlePointR * 2, circlePointR * 2);
			g2.translate(-x, -y);
			
			startAnglue += angle;
		}		
		g2.setColor(new Color(255,0,0));
		drawPieHoleAndLine(g2,element,pieRadius,startAnglue0,eachAngle);
		
		if(selectKey != null) {
			//指针底部在岛里的半径
			int pointerIslandR = (int)(islandRadius * 0.5);
			
			//画指针
			double pointerPosRate = getDouble(element,"pointer-pos-rate");
			if(pointerPosRate <= 0) {
				pointerPosRate = 0.5;
			}
			int pointerR = (int)(pieRadius * pointerPosRate);
			
			g2.setColor(Color.gray);

			float alpha = (float) ((selectLabelAngle - 90)/ 180.0 * Math.PI);
			if (alpha != 0) {
				g2.rotate(-alpha);
			}
			GeneralPath path = new GeneralPath();
			path.moveTo(0, 0);
			path.lineTo(-pointerIslandR, 0);
			path.lineTo(0,-pointerR);
			path.lineTo(pointerIslandR, 0);
			path.closePath();
			g2.fill(path);
			if (alpha != 0) {
				g2.rotate(alpha);
			}
			
			
			g2.setColor(ChartUtil.getColor(selectKey.getString("colour")));
			g2.fillOval(-islandRadius, -islandRadius, islandRadius * 2, islandRadius * 2);
			
			float fontSize = selectValueFontSize;
			if(fontSize <= 0) {
				fontSize = selectKey.getFloatValue("font-size");
			}
			if(fontSize > 0) {
				g2.setFont(g2.getFont().deriveFont(fontSize));
			}
			g2.setColor(Color.white);
			String text = selectKey.getString("text");
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(text, g2);
			int x = -(int)(rect.getWidth() / 2);
			int y = (int)(fm.getAscent() - rect.getHeight() / 2);
			g2.drawString(text, x,y);
			
		}
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

			JSONArray keys = element.getJSONArray("keys");
			int keyCount = keys.size();
			int r = pieRadius;//84;

			int startAnglue0 = fromAnglue;//(int)this.getDouble(element,"start-angle");
			int startAnglue = startAnglue0; 
			for(int i=0;i<keyCount;i++) {
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
