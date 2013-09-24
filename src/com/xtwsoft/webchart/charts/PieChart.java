package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;


public class PieChart extends BaseChart {
	public PieChart(JSONObject element,JSONObject legend,int imageWidth,int imageHeight) {
		super(element,legend,imageWidth,imageHeight);
	}
	
	public void draw(Graphics2D g2) {
		double legendHeight = 0;
		double legendWidth = 0;
		FontMetrics fm = g2.getFontMetrics();
		
		for(int i=0;i<m_keys.size();i++) {
			if(i > 0) {
				legendHeight += this.getLegendOffset();
			}
			JSONObject key = m_keys.getJSONObject(i);
			Rectangle2D rect = fm.getStringBounds(key.getString("text"), g2);
			legendHeight += rect.getHeight();
			if(rect.getWidth() > legendWidth) {
				legendWidth = rect.getWidth(); 
			}
		}		
		legendWidth += this.getLegendIconWidth() + this.getLegendIconLabelMargin();
		
		int pieRadius = 0;
		int legendRectX = m_imageHeight;
		int legendRectWidth = m_imageWidth - m_imageHeight;
		int legendRectY = 0;
		int legendRectHeight = m_imageHeight;
		
		int pieWidth = 0;
		
		legendRectX = m_imageWidth - legendRectWidth;
		if(m_imageWidth > m_imageHeight + legendWidth) {
			pieWidth = m_imageHeight;
			legendRectX = m_imageHeight;
			pieRadius = (int)(pieWidth / 2 * 0.9);
			legendRectWidth = m_imageWidth - m_imageHeight;
		} else if(m_imageWidth > legendWidth) {
			pieWidth = (int)((m_imageWidth - legendWidth));
			if(pieWidth < m_imageWidth / 2) {
				pieWidth = (int)(m_imageWidth  / 2);
				pieRadius = (int)(pieWidth / 2 * 0.9);
				legendRectX = (int)(m_imageWidth  / 2);
				legendRectWidth = (int)legendWidth;
			} else {
				pieRadius = (int)(pieWidth / 2 * 0.9);
				legendRectX = (int)(m_imageWidth - legendWidth);
				legendRectWidth = (int)legendWidth;
			}
		} else {
			pieWidth = (int)(m_imageWidth  / 2);
			pieRadius = (int)(pieWidth / 2 * 0.9);
			legendRectX = (int)(m_imageWidth  / 2);
			legendRectWidth = (int)legendWidth;
		}
		
		g2.translate(pieWidth / 2, m_imageHeight /2);
		
		double totalValue = 0;
		int valueCount = m_values.size();
		for(int i=0;i<valueCount;i++) {
			JSONObject value = m_values.getJSONObject(i);
			totalValue += this.getDouble(value,"value");
		}
		
		drawPie(g2,pieRadius,totalValue);
		drawPieHoleAndLine(g2,pieRadius,totalValue);
		drawPieLabel(g2,pieRadius,totalValue);
		
		g2.translate(-pieWidth / 2, -m_imageHeight /2);
		
		//图形部分宽度
		int graphWidth = pieRadius * 2;
		//图形部分高度
		int graphHeight = pieRadius * 2;
		drawLegend(g2,legendRectX,legendRectY,legendRectWidth,legendRectHeight,legendWidth,legendHeight,graphWidth,graphHeight);
	}
	
	private void drawPie(Graphics2D g2,int pieRadius,double totalValue) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int valueCount = m_values.size();
		
		int r = pieRadius;
		int startAnglue0 = (int)this.getDouble(m_element,"start-angle");
		int startAnglue = startAnglue0; 
		int totalAngle = 360 + startAnglue0;
		for(int i=0;i<valueCount;i++) {
			JSONObject value = m_values.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(value.getString("colour")));
			int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
			
			if(i == valueCount - 1) {
				angle = totalAngle - startAnglue;
			}
			g2.fillArc(-r, -r, r * 2, r * 2, startAnglue, angle);
			startAnglue += angle;
		}
	}
	
	private void drawPieHoleAndLine(Graphics2D g2,int pieRadius,double totalValue) {
		double holePosRate = getDouble(m_element,"hole-pos-rate");
		if(holePosRate > 0) {
			int innerR = (int)(pieRadius * holePosRate);
			g2.setColor(Color.white);
			g2.fillOval(-innerR, -innerR, innerR * 2, innerR * 2);
		}
		
		double splitLineWidth = getDouble(m_element,"split-line-width");
		if(splitLineWidth > 0) {
			Stroke defaultStroke = g2.getStroke();
			if(splitLineWidth > 0) {
				BasicStroke stroke = new BasicStroke((float)splitLineWidth);
				g2.setStroke(stroke);
			}

			int valueCount = m_values.size();
			int startAnglue0 = (int)this.getDouble(m_element,"start-angle");
			int startAnglue = startAnglue0; 
			int totalAngle = 360 + startAnglue0;
			int lineR = (int)(pieRadius * 1.1);//84;

			for(int i=0;i<valueCount;i++) {
				JSONObject value = m_values.getJSONObject(i);
				int angle = (int)(360.0 * value.getDoubleValue("value") / totalValue);
				if(i == valueCount - 1) {
					angle = totalAngle - startAnglue;
				}
				
				double x =  lineR * Math.cos(startAnglue * Math.PI / 180);
				double y =  -lineR * Math.sin(startAnglue * Math.PI / 180);
				g2.drawLine(0, 0, (int)x, (int)y);
				
				startAnglue += angle;
			}
			g2.setStroke(defaultStroke);
		}
	}
	
	private void drawPieLabel(Graphics2D g2,int pieRadius,double totalValue) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font bakFont = g2.getFont();
		Font newFont = new Font(Font.SANS_SERIF, getFontFamily(), 12);
		g2.setFont(newFont);
		int startAnglue0 = (int)this.getDouble(m_element,"start-angle");
		int startAnglue = startAnglue0; 
		int totalAngle = 360 + startAnglue0;
		
		int valueCount = m_values.size();
		float labelPosRate = m_element.getFloatValue("label-pos-rate");
		int labelR = (int)(pieRadius * labelPosRate);
		FontMetrics fm = g2.getFontMetrics();
		for(int i=0;i<valueCount;i++) {
			JSONObject value = m_values.getJSONObject(i);
			
			g2.setFont(g2.getFont().deriveFont(this.getFloat(value, "font-size", 12)));
			
			int angle = (int)(360.0 * value.getFloatValue("value") / totalValue);
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
		g2.setFont(bakFont);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	
}
