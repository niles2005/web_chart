package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;

public class LoopChart extends BaseChart {
	private double m_shapMaxWidth;
	private double m_shapMaxHeight;
	private int m_startAngle = 90;
	private int m_maxAngle = 360 - m_startAngle;
	private double m_overlapRete = 0.8;
	private Stroke defaultStroke = null;
	
	public LoopChart(JSONObject chartData,JSONObject legend,int imageWidth,int imageHeight) {
		super(chartData,legend,imageWidth,imageHeight);
		m_shapMaxWidth = m_imageWidth * 0.7 * 0.9;
		m_shapMaxHeight = m_imageHeight * 0.9;
	}
	
	
	public void draw(Graphics2D g2) {
		defaultStroke = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Point2D.Double centerPoint = new Point2D.Double(m_imageWidth*0.65/2,m_imageHeight/2);
		g2.translate(centerPoint.x,centerPoint.y);
		
		double legendWidth = 0;
		FontMetrics fm = g2.getFontMetrics();
		for(int i=0;i<m_keys.size();i++) {
			JSONObject key = m_keys.getJSONObject(i);
			Rectangle2D rect = fm.getStringBounds(key.getString("text"), g2);
			if(rect.getWidth() > legendWidth) {
				legendWidth = rect.getWidth(); 
			}
		}		
		
		int radius = 0;
		if(m_shapMaxWidth < m_shapMaxHeight){
			radius = (int)(m_shapMaxWidth/2);
		}else{
			radius = (int)(m_shapMaxHeight/2);
		}
		
		drawLoops(g2,radius,0);
		drawLoops(g2,radius,legendWidth);
		
		g2.translate(-centerPoint.x,  -centerPoint.y);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	private void drawLoops(Graphics2D g2, double radius, double legendWidth){
		double shrinkRate = 1;
		double shrinkStep = (1.0 - 0.2)/m_values.size();
		for (int i = 0;i < m_values.size(); i++){
			JSONObject value = m_values.getJSONObject(i);
			int angle = (int)(m_maxAngle * value.getDouble("value"));
			int outterR = (int)(radius * shrinkRate);
			int innerR = outterR - (int)((radius * shrinkStep)*(1+m_overlapRete));
			double alpha = value.getDouble("alpha") ;
			String loopColorStr = value.getString("colour");
			String holeColorStr  = "#FFFFFF";
			if (i +1< m_values.size()) {
				JSONObject holeValue = m_values.getJSONObject(i + 1);
				holeColorStr = holeValue.getString("colour");
			}
			
			
			if(legendWidth > 0){
				drawLoop(g2,outterR,innerR,m_startAngle, angle, loopColorStr,holeColorStr , alpha);
				int legendY = outterR - (int)(radius * shrinkStep/2);
				int pointRadius = (int)(radius * shrinkStep/2 *0.3);
				g2.setFont(new java.awt.Font("SimSun",java.awt.Font.PLAIN,12));//宋体
				drawLegend(g2,legendY,pointRadius,legendWidth,loopColorStr,i);
			}else{
				drawAlphaLoop(g2,outterR,innerR,m_startAngle, angle, loopColorStr,alpha);
			}
			shrinkRate -= shrinkStep;
		}
		
		
	}
	
	private void drawLoop(Graphics2D g2,int outterR,int innerR,int startAngle, int angle, String loopColorStr,String holeColorStr,double alpha ){
		g2.setColor( ChartUtil.getColor(loopColorStr));
		g2.fillArc(-outterR, -outterR, outterR*2, outterR*2, startAngle, angle);
		
		g2.setColor(m_backgroundColor);
		g2.fillOval(-innerR, -innerR, innerR * 2, innerR * 2);
		
		g2.setColor(ChartUtil.getColor(holeColorStr,alpha));
		g2.fillOval(-innerR, -innerR, innerR * 2, innerR * 2);
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(m_backgroundColor);
		g2.drawOval(-innerR, -innerR, innerR * 2, innerR * 2);
		g2.setStroke(defaultStroke);
	}


	
	private void drawAlphaLoop(Graphics2D g2,int outterR,int innerR,int startAngle, int angle, String colorStr, double alpha){
		Color color = ChartUtil.getColor(colorStr,alpha);
		g2.setColor(color);
		g2.fillArc(-outterR, -outterR, outterR*2, outterR*2, startAngle + angle, 360 - angle);
		
		
		g2.setColor(m_backgroundColor);
		g2.fillOval(-innerR, -innerR, innerR*2, innerR*2);
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(m_backgroundColor);
		g2.drawOval(-outterR, -outterR, outterR * 2, outterR * 2);
		g2.setStroke(defaultStroke);
	}

	private void drawLegend(Graphics2D g2,int legendY, int pointRadius, double legendWidth,String colorStr,int textIndex) {
		g2.setColor(Color.BLACK);
		g2.drawOval( -pointRadius*3, -legendY - pointRadius, pointRadius*2, pointRadius*2);
		
		float textX = (float)(m_imageWidth * 0.95 - m_shapMaxWidth/2 - legendWidth);
		float textY = -legendY;
		g2.drawLine(0, (int)textY, (int)textX, (int)textY);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		Color color = ChartUtil.getColor(colorStr);
		g2.setColor(color);
		JSONObject key = m_keys.getJSONObject(textIndex);
		String text = key.getString("text");
		g2.setFont(g2.getFont().deriveFont(this.getFloat(key, "font-size", 12)));
		
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(key.getString("text"), g2);
		float offsetY = (float)rect.getHeight() / 2 - 3;
		float offsetX = 5;
		g2.drawString(text, textX + offsetX, textY + offsetY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
	}
	

}
