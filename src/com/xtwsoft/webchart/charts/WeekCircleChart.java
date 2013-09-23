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


public class WeekCircleChart extends BaseChart {
	public WeekCircleChart() {
	}
	static float dash1[] = {1.2f};
	static BasicStroke dashed = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
	static BasicStroke lightLine = new BasicStroke(1.0f);
	private int legendFlagWidth = 9;
	private int legendFlagMargin = 10;//legend图标和字符间�?

	public void drawElement(Graphics2D g2,int width,int height,JSONObject element) {
		double legendHeight = 0;
		double legendWidth = 0;
		JSONArray keys = element.getJSONArray("keys");
		Font bakFont = g2.getFont();
		for(int i=0;i<keys.size();i++) {
			JSONObject key = keys.getJSONObject(i);
			
			String strFontSize = key.getString("font-size");
			if(strFontSize != null) {
				float fontSize = Float.parseFloat(strFontSize);
				g2.setFont(bakFont.deriveFont(fontSize));
			}
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(key.getString("text"), g2);
			legendHeight += rect.getHeight() + fm.getLeading();
			if(rect.getWidth() > legendWidth) {
				legendWidth = rect.getWidth(); 
			}
		}	
		g2.setFont(bakFont);
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
		
		
		drawCircle(g2,width,height,element,pieRadius);
		
		g2.translate(-pieWidth / 2, -height /2);
		
		drawLegend(g2,legendRectX,legendRectY,legendRectWidth,legendRectHeight,legendWidth,legendHeight,element);
	}
	
	private void drawCircle(Graphics2D g2,int width,int height,JSONObject element,int pieRadius) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font oldFont = g2.getFont();
		Font newFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		g2.setFont(newFont);
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D timeLabelRect = fm.getStringBounds("06:00", g2);
		
		JSONArray values = element.getJSONArray("values");
		
		int r = pieRadius - (int)(timeLabelRect.getWidth());
		int circleNum = values.size();
		Stroke defaultStroke = g2.getStroke();
		g2.setStroke(dashed);
		g2.setColor(Color.gray);
		int drawCircleNum = circleNum + 1;
		for(int i=2;i<=drawCircleNum;i++) {
			int cr = (int)(1.0 * i / drawCircleNum * r);
			g2.drawOval(-cr, -cr, cr * 2, cr * 2);
		}
		g2.setStroke(lightLine);
		
		g2.drawOval(-r, -r, r * 2, r * 2);
		g2.setColor(Color.lightGray);
		
		int timeCount = 24;
		int timeR = (int)(r * 1.05);//84;

		for(int i=0;i<timeCount;i++) {
			int angle = (int)(360.0 * i / timeCount);
			
			double x =  timeR * Math.cos(angle * Math.PI / 180);
			double y =  -timeR * Math.sin(angle * Math.PI / 180);
			g2.drawLine(0, 0, (int)x, (int)y);
		}
		g2.setColor(Color.gray);
		int labelR = (int)(timeR + timeLabelRect.getWidth() / 2);
		for(int i=0;i<timeCount;i+=2) {
			int index = i;
			if(i > 12) {//左右对换
				index = 24 - i;
			}
			int angle = (int)(-360.0 * index / timeCount) + 90;
			
			double x =  labelR * Math.cos(angle * Math.PI / 180);
			double y =  -labelR * Math.sin(angle * Math.PI / 180);
			
			if(i == 0) {
				y += fm.getAscent() + timeLabelRect.getWidth() / 2 - timeLabelRect.getHeight();
			} else if(i == 2 || i == 22) {
				y += fm.getAscent()-fm.getAscent() / 5;
			} else if(i == 10 || i == 14) {
				y += - fm.getAscent() / 5;
			} else if(i == 12) {
				y += fm.getAscent() - timeLabelRect.getWidth() / 2;
			} else {
				y += fm.getAscent() - timeLabelRect.getHeight() / 2;
			}
			if(i > 12) {
				x = -x;
			}
			x -= (int)(timeLabelRect.getWidth() / 2);
			String labelString = i + ":00";
			if(labelString.length() == 4) {
				labelString = "0" + labelString;
			}
			g2.drawString(labelString, (int)x, (int)y );
		}
		
		for(int i=0;i<values.size();i++) {
			JSONObject item = values.getJSONObject(i);
			JSONArray valueArr = item.getJSONArray("value");
			int cr = (int)(1.0 * (drawCircleNum - i) / drawCircleNum * r);
			g2.setColor(ChartUtil.getColor(item.getString("colour")));

			for(int j=0;j<valueArr.size();j++) {
				if(j < 24) {
					int v = valueArr.getIntValue(j);
					if(v > 0) {
						int angle = (int)(-360.0 * j / timeCount) + 90;
						
						double x =  cr * Math.cos(angle * Math.PI / 180);
						double y =  -cr * Math.sin(angle * Math.PI / 180);
						
						g2.translate(x, y);
						g2.fillOval(-v, -v, v * 2, v * 2);
						g2.translate(-x, -y);
					}
				}
			}
		}
		
		
		g2.setFont(oldFont);
	}
	
	private void drawLegend(Graphics2D g2,int rectX,int rectY,int rectWidth,int rectHeight,double legendWidth,double legendHeight,JSONObject element) {
		JSONArray keys = element.getJSONArray("keys");
		int rr = legendFlagWidth;
		double drawPartWidth = legendWidth + legendFlagWidth + legendFlagMargin;   

		int x = rr /2;
		int y = 0;
		double labelX = rectX + rectWidth / 2 - drawPartWidth / 2;
		double labelY = rectHeight/2-legendHeight / 2;
		g2.translate(labelX, labelY);

		double labelHeight = 0;
		Font bakFont = g2.getFont();
		for(int i=0;i<keys.size();i++) {
			JSONObject key = keys.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(key.getString("colour")));
			String strFontSize = key.getString("font-size");
			if(strFontSize != null) {
				float fontSize = Float.parseFloat(strFontSize);
				g2.setFont(bakFont.deriveFont(fontSize));
			}
			String text = key.getString("text");
			
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(text, g2);
			labelHeight = rect.getHeight();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fillOval(x, (int)(y + (labelHeight + fm.getLeading())/ 2) - rr / 2, rr, rr);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
			g2.drawString(text, x + legendFlagWidth + legendFlagMargin, y+ fm.getAscent());
			 
			y += labelHeight + fm.getLeading();
		}	
	}
	

}
