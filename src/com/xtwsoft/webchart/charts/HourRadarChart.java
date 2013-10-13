package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;

public class HourRadarChart extends BaseChart {
	private int m_coordinateRadius ; 
	private static final int m_marginHeight = 16;
	private static final int m_marginWidth = 30;
	
	public HourRadarChart(JSONObject element,JSONObject legend,int imageWidth,int imageHeight) {
		super(element,legend,imageWidth,imageHeight);
	}
	
	
	public void draw(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		Point2D.Double centerPoint = new Point2D.Double(m_imageWidth/2,m_imageHeight - m_marginHeight);
		g2.translate(centerPoint.x,centerPoint.y);
		
		int shapeMaxHeight = m_imageHeight - m_marginHeight * 2 ;
		int shapeMaxWidth =  m_imageWidth / 2 - m_marginWidth ;
		if (shapeMaxHeight < shapeMaxWidth){
			m_coordinateRadius = shapeMaxHeight;
		}else{
			m_coordinateRadius = shapeMaxWidth;
		}
		
		int calibration = 7;	
		int gridGap = m_coordinateRadius / calibration;
		
		drawGrid(g2 ,gridGap,calibration);
		drawAxis(g2);
		drawMark(g2);
		drawValue(g2 , gridGap );
		
		g2.translate(-centerPoint.x,-centerPoint.y);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	
	private void drawGrid(Graphics2D g2 ,int gridGap ,int calibration){
		BasicStroke dashed = new BasicStroke(1f,
	            BasicStroke.CAP_BUTT,
	            BasicStroke.JOIN_MITER,
	            10.0f, new float[]{1.1f}, 0.0f);
		
		String zebraColor = m_element.getString("zebra-color");
		float zebraAlpha = m_element.getFloatValue("zebra-alpha");
		Color newColor = ChartUtil.getColor(zebraColor, zebraAlpha);
		for (int i = 0; i < calibration; i++) {
			int currentRadius = m_coordinateRadius - i * gridGap;
			
			if( i % 2 == 1){
				g2.setColor(newColor);
				g2.fillArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, 0, 180);
				g2.setColor(this.m_backgroundColor);
				g2.fillArc(-currentRadius+gridGap, -currentRadius+gridGap,(currentRadius-gridGap) * 2, (currentRadius-gridGap) * 2, 0, 180);
			}
			g2.setColor(Color.LIGHT_GRAY);
			g2.setStroke(dashed);
			g2.drawArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, 0, 180);
		}
		
	}
	
	
	private void drawAxis(Graphics2D g2){
		g2.setStroke(new BasicStroke(1));
		int degreeGap = 180 / 12;
		for (int i = 0; i < 12; i++) {
			g2.rotate(Math.toRadians(-degreeGap));
			if (i % 2 == 0) {
				g2.setColor(new Color(240,240,240));
			} else {
				g2.setColor(new Color(225,225,225));
			}
			g2.drawLine(0, 0, m_coordinateRadius, 0);
		}
		g2.rotate(Math.toRadians(180));
		g2.setColor(new Color(230,230,230));
		g2.drawLine(0, 0, m_coordinateRadius, 0);
	}
	
	private void drawMark(Graphics2D g2){
		int degreeGap = 180 / 6;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g2.setColor(Color.GRAY);
		int time = 50;
		g2.drawString("8:00", m_coordinateRadius + 4, 0);
		for (int i = 0; i < 6; i++) {
			int currentTime = time - i * 10;
			int currentAngle = (i + 1)* degreeGap;
			String currentTimeStr = "7:"+ currentTime;
			if (currentTime == 0){
				currentTimeStr += "0";
			}
			double pointX = Math.cos(Math.toRadians(-currentAngle)) * (m_coordinateRadius + 4) ;
			double pointY = Math.sin(Math.toRadians(-currentAngle)) * (m_coordinateRadius + 4) ;
			double offsetX =  (1-Math.cos(Math.toRadians(-currentAngle)) ) * 10;
			
			g2.drawString(currentTimeStr, (int)(pointX - offsetX) , (int)pointY );
			
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	private void drawValue(Graphics2D g2 , int gridGap){
		g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int i = 0; i < 6; i++) {
			Color xcolor = new Color( (int)(Math.random()*255) ,(int)(Math.random()*255) ,(int)(Math.random()*255));
			g2.setColor( xcolor );
			int currentRadius = m_coordinateRadius - i * gridGap - 4;
			int startAngle =  (int)(Math.random()*180);
			int angle = (int)(Math.random()*(180 - startAngle));
			g2.drawArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, startAngle, angle);
			
			
			double pointX = Math.cos(Math.toRadians(-startAngle)) * currentRadius ;
			double pointY = Math.sin(Math.toRadians(-startAngle)) * currentRadius ;
			g2.setColor(this.m_backgroundColor);
			g2.fillOval((int)pointX -5 , (int)pointY -5 , 10, 10);
			g2.setColor(xcolor);
			g2.fillOval((int)pointX -3 , (int)pointY -3 , 6, 6);
			
			int endAngle = startAngle + angle;
			pointX = Math.cos(Math.toRadians(-endAngle)) * currentRadius;
			pointY = Math.sin(Math.toRadians(-endAngle)) * currentRadius;
			g2.setColor(this.m_backgroundColor);
			g2.fillOval((int)pointX -5 , (int)pointY -5 , 10, 10);
			g2.setColor(xcolor);
			g2.fillOval((int)pointX -3 , (int)pointY -3 , 6, 6);
			
		}
	}
		
	

}
