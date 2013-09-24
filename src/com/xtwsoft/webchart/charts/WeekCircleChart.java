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
	public WeekCircleChart(JSONObject element,JSONObject legend,int imageWidth,int imageHeight) {
		super(element,legend,imageWidth,imageHeight);
	}
	
	static float dash1[] = {1.2f};
	static BasicStroke dashed = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
	static BasicStroke lightLine = new BasicStroke(1.0f);
	private int legendFlagWidth = 9;
	private int legendFlagMargin = 10;//legend图标和字符间�?

	public void draw(Graphics2D g2) {
		double legendHeight = 0;
		double legendWidth = 0;
		
		Font bakFont = g2.getFont();
		for(int i=0;i<m_keys.size();i++) {
			JSONObject key = m_keys.getJSONObject(i);
			
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
		
		
		drawCircle(g2,pieRadius);
		
		g2.translate(-pieWidth / 2, -m_imageHeight /2);
		
		//图形部分宽度
		int graphWidth = pieRadius * 2;
		//图形部分高度
		int graphHeight = pieRadius * 2;
		drawLegend(g2,legendRectX,legendRectY,legendRectWidth,legendRectHeight,legendWidth,legendHeight,graphWidth,graphHeight);
	}
	
	private void drawCircle(Graphics2D g2,int pieRadius) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font oldFont = g2.getFont();
		Font newFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		g2.setFont(newFont);
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D timeLabelRect = fm.getStringBounds("06:00", g2);
		
		int r = pieRadius - (int)(timeLabelRect.getWidth());
		int circleNum = m_values.size();
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
		
		for(int i=0;i<m_values.size();i++) {
			JSONObject item = m_values.getJSONObject(i);
			JSONArray valueArr = item.getJSONArray("value");
			int cr = (int)(1.0 * (drawCircleNum - i) / drawCircleNum * r);
			float alpha = this.getFloat(item, "alpha",0.65f);
			g2.setColor(ChartUtil.getColor(item.getString("colour"),alpha));

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
	
}
