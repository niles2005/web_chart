package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import com.alibaba.fastjson.JSONObject;

public class HourRadarChart extends BaseChart {
	private int m_coordinateRadius ; 
	private static final int m_marginHeight = 18;
	private static final int m_marginWidth = 40;
	private static final float dash1[] = {1.2f};
	private static final BasicStroke dashed = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
	
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
		
		
		
		int gridGap = m_coordinateRadius / 6;
		for (int i = 0; i < 6; i++) {
			int currentRadius = m_coordinateRadius - i * gridGap;
			
			if( i % 2 == 0){
				g2.setColor(new Color(0,0,0,10));
				g2.fillArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, 0, 180);
				g2.setColor(this.m_backgroundColor);
				g2.fillArc(-currentRadius+gridGap, -currentRadius+gridGap,(currentRadius-gridGap) * 2, (currentRadius-gridGap) * 2, 0, 180);
			}
			g2.setColor(Color.LIGHT_GRAY);
			g2.setStroke(dashed);
			g2.drawArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, 0, 180);
		}
		
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.LIGHT_GRAY);
		
		int degreeGap = 180 / 6;
		int time = 50;
		for (int i = 0; i < 6; i++) {
			g2.rotate(Math.toRadians(-degreeGap));
			g2.drawLine(0, 0, m_coordinateRadius, 0);
		}
		g2.rotate(Math.toRadians(180));
		
		g2.setColor(Color.GRAY);
		g2.drawLine(0, 0, m_coordinateRadius, 0);
		g2.drawString("8:00", m_coordinateRadius + 4, 0);
		for (int i = 0; i < 6; i++) {
			int currentTime = time - i * 10;
			int currentAngle = (i + 1)* degreeGap;
			String currentTimeStr = "7:"+ currentTime;
			double pointX = Math.cos(Math.toRadians(-currentAngle)) * (m_coordinateRadius + 4) ;
			double pointY = Math.sin(Math.toRadians(-currentAngle)) * (m_coordinateRadius + 4) ;
//			double offsetX =  Math.cos(Math.toRadians(-currentAngle)) * 20;
//			double offsetY =  Math.sin(Math.toRadians(-currentAngle)) * 20;
			
			g2.drawString(currentTimeStr, (int)pointX , (int)pointY );
			
		}
		
		
		
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
		
		
		
		
		
		
		g2.translate(-centerPoint.x,-centerPoint.y);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	private void drawGrid(Graphics2D g2){
		
		
	}
	
	

}
