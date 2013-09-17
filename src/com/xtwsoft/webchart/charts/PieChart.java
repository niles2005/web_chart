package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;


public class PieChart extends BaseChart {
	public PieChart() {
	}
	
	private int legendFlagWidth = 10;
	private int legendFlagMargin = 3;//legend图标和字符间距

	public void drawElement(Graphics2D g2,int width,int height,JSONObject element) {
		double legendHeight = 0;
		double legendWidth = 0;
		JSONArray keys = element.getJSONArray("keys");
		FontMetrics fm = g2.getFontMetrics();
		for(int i=0;i<keys.size();i++) {
			JSONObject key = keys.getJSONObject(i);
			Rectangle2D rect = fm.getStringBounds(key.getString("text"), g2);
			legendHeight += rect.getHeight();
			if(rect.getWidth() > legendWidth) {
				legendWidth = rect.getWidth(); 
			}
		}		
		legendWidth += legendFlagWidth + legendFlagMargin;
		
		int pieRadius = 0;
		int legendRectX = height;
		int legendRectWidth = width - height;
		int legendRectY = 0;
		int legendRectHeight = height;
		
		int pieWidth = 0;
		
		legendRectX = width - legendRectWidth;
		if(width > height + legendWidth) {
			pieWidth = height;
			legendRectX = height;
			pieRadius = (int)(pieWidth / 2 * 0.9);
			legendRectWidth = width - height;
		} else if(width > legendWidth) {
			pieWidth = (int)((width - legendWidth));
			if(pieWidth < width / 2) {
				pieWidth = (int)(width  / 2);
				pieRadius = (int)(pieWidth / 2 * 0.9);
				legendRectX = (int)(width  / 2);
				legendRectWidth = (int)legendWidth;
			} else {
				pieRadius = (int)(pieWidth / 2 * 0.9);
				legendRectX = (int)(width - legendWidth);
				legendRectWidth = (int)legendWidth;
			}
		} else {
			pieWidth = (int)(width  / 2);
			pieRadius = (int)(pieWidth / 2 * 0.9);
			legendRectX = (int)(width  / 2);
			legendRectWidth = (int)legendWidth;
		}
		
		g2.translate(pieWidth / 2, height /2);
		
		JSONArray values = element.getJSONArray("values");
		
		double totalValue = 0;
		int valueCount = values.size();
		for(int i=0;i<valueCount;i++) {
			JSONObject value = values.getJSONObject(i);
			totalValue += this.getDouble(value,"value");
		}
		
		drawPie(g2,width,height,element,pieRadius,totalValue);
		drawPieHoleAndLine(g2,width,height,element,pieRadius,totalValue);
		drawPieLabel(g2,width,height,element,pieRadius,totalValue);
		
		g2.translate(-pieWidth / 2, -height /2);
		
		drawLegend(g2,legendRectX,legendRectY,legendRectWidth,legendRectHeight,legendWidth,legendHeight,element);
	}
	
	private void drawPie(Graphics2D g2,int width,int height,JSONObject element,int pieRadius,double totalValue) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		JSONArray values = element.getJSONArray("values");
		int valueCount = values.size();
		
		int r = pieRadius;
		int startAnglue0 = (int)this.getDouble(element,"start-angle");
		int startAnglue = startAnglue0; 
		int totalAngle = 360 + startAnglue0;
		for(int i=0;i<valueCount;i++) {
			JSONObject value = values.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(value.getString("colour")));
			int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
			
			if(i == valueCount - 1) {
				angle = totalAngle - startAnglue;
			}
			g2.fillArc(-r, -r, r * 2, r * 2, startAnglue, angle);
			startAnglue += angle;
		}
	}
	
	private void drawPieHoleAndLine(Graphics2D g2,int width,int height,JSONObject element,int pieRadius,double totalValue) {
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
			int startAnglue0 = (int)this.getDouble(element,"start-angle");
			int startAnglue = startAnglue0; 
			int totalAngle = 360 + startAnglue0;
			int r = pieRadius;//84;

			for(int i=0;i<valueCount;i++) {
				JSONObject value = values.getJSONObject(i);
				int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
				if(i == valueCount - 1) {
					angle = totalAngle - startAnglue;
				}
				
				double x =  r * Math.cos(startAnglue * Math.PI / 180);
				double y =  -r * Math.sin(startAnglue * Math.PI / 180);
				g2.drawLine(0, 0, (int)x, (int)y);
				
				startAnglue += angle;
			}
			g2.setStroke(defaultStroke);
		}
	}
	
	private void drawPieLabel(Graphics2D g2,int width,int height,JSONObject element,int pieRadius,double totalValue) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		int startAnglue0 = (int)this.getDouble(element,"start-angle");
		int startAnglue = startAnglue0; 
		int totalAngle = 360 + startAnglue0;
		
		JSONArray values = element.getJSONArray("values");
		int valueCount = values.size();
		double labelPosRate = element.getDoubleValue("label-pos-rate");
		int labelR = (int)(pieRadius * labelPosRate);
		FontMetrics fm = g2.getFontMetrics();
		for(int i=0;i<valueCount;i++) {
			JSONObject value = values.getJSONObject(i);
			int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
			if(i == valueCount - 1) {
				angle = totalAngle - startAnglue;
			}
			int middleArc = startAnglue + angle / 2;
			Rectangle2D rect = fm.getStringBounds(value.getString("label"), g2);
			
			double x =  labelR * Math.cos(middleArc * Math.PI / 180);// - rect.getWidth() / 2;
			double y =  -labelR * Math.sin(middleArc * Math.PI / 180);// - rect.getHeight() / 2;
			g2.translate(x, y);
			g2.setColor(getColor(value,"label-colour",Color.white));
			g2.drawString(value.getString("label"), -(int)(rect.getWidth()/2), (int)(rect.getHeight()/4 ));
			
			startAnglue += angle;
			g2.translate(-x, -y);
		}
	}
	
	private void drawLegend(Graphics2D g2,int rectX,int rectY,int rectWidth,int rectHeight,double legendWidth,double legendHeight,JSONObject element) {
//		g2.setColor(Color.red);
//		g2.fillRect(rectX,rectY,rectWidth,rectHeight);
		JSONArray keys = element.getJSONArray("keys");
		int rr = legendFlagWidth;
		double drawPartWidth = legendWidth + legendFlagWidth + legendFlagMargin;   

		int x = rr /2;
		int y = 0;
		FontMetrics fm = g2.getFontMetrics();
		
		double labelX = rectX + rectWidth / 2 - drawPartWidth / 2;
		double labelY = rectHeight/2-legendHeight / 2;
		g2.translate(labelX, labelY);

		int labelHeight = 0;
		int ascent = fm.getAscent();
		for(int i=0;i<keys.size();i++) {
			JSONObject key = keys.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(key.getString("colour")));
			String text = key.getString("text");
			if(labelHeight == 0) {
				Rectangle2D rect = fm.getStringBounds(text, g2);
				labelHeight = (int)rect.getHeight();
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fillOval(x, (int)(y + labelHeight / 2) - rr / 2, rr, rr);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
//			g2.setColor(Color.gray);
			g2.drawString(text, x + legendFlagWidth + 3 , y+ ascent);
			 
			y += labelHeight;
		}		
	}
	

}
