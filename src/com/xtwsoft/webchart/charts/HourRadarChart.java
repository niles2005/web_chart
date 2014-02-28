package com.xtwsoft.webchart.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;

public class HourRadarChart extends BaseChart {
	private int m_coordinateRadius ; 
	private static final int m_marginHeight = 16;
	private static final int m_marginWidth = 30;
	private String m_startTime;
	private String m_endTime;
	
	public HourRadarChart(JSONObject chartData,JSONObject legend,int imageWidth,int imageHeight) {
		super(chartData,legend,imageWidth,imageHeight);
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
		
		m_startTime = m_chartData.getString("startTime");
		m_endTime = m_chartData.getString("endTime");
		
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
		
		String zebraColor = m_chartData.getString("zebra-color");
		Color newColor = ChartUtil.getColor(zebraColor);
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
		int originhour = Integer.parseInt(m_endTime.substring(0 , m_endTime.indexOf(":")));
		int hour = originhour;
		int time = Integer.parseInt(m_endTime.substring(m_endTime.indexOf(":")+1));
		g2.drawString(m_endTime, m_coordinateRadius + 4, 0);
		for (int i = 0; i < 6; i++) {
			
			int currentTime = time - ( i + 1 ) * 10;
			if( currentTime < 0 ){
				currentTime = 60 + currentTime;
				if (originhour == hour) {
					originhour = hour - 1;
				}
			}
			int currentAngle = (i + 1)* degreeGap;
			String currentTimeStr = originhour + ":"+ currentTime;
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
		for(int i=0;i<this.m_values.size();i++) {
			if(i >= 7) {
				return;
			}
			JSONArray arr = m_values.getJSONArray(i);
			if(arr.size() == 3) {
				try {
					int typeIndex = Integer.parseInt(arr.getString(0).trim()) - 1;
					if(typeIndex >= 0 && typeIndex < this.m_keys.size()) {
						
					} else {
						continue;
					}
					String time1 = arr.getString(1);
					String time2 = arr.getString(2);
					int pos1 = time1.indexOf(":");
					int pos2 = time2.indexOf(":");
					int minute1 = -1;
					int minute2 = -1;
					if(pos1 != -1 && pos2 != -1) {
						minute1 = Integer.parseInt(time1.substring(pos1 + 1).trim());
						minute2 = Integer.parseInt(time2.substring(pos2 + 1).trim());
					}
					if(minute1 >= 0 && minute1 <= 60 && minute2 >= 0 && minute2 <= 60 ) {
						
					} else {
						continue;
					}
					JSONObject key = m_keys.getJSONObject(typeIndex);
					Color color = ChartUtil.getColor(key.getString("colour"));
					g2.setColor(color);
					
					int currentRadius = m_coordinateRadius - i * gridGap - 4;
					int startAngle = 180 - minute1 * 3;
					int endAngle = 180 - minute2 * 3;
					g2.drawArc(-currentRadius, -currentRadius,currentRadius * 2, currentRadius * 2, startAngle, (endAngle - startAngle));
					
					double pointX = Math.cos(Math.toRadians(-startAngle)) * currentRadius ;
					double pointY = Math.sin(Math.toRadians(-startAngle)) * currentRadius ;
					g2.setColor(this.m_backgroundColor);
					g2.fillOval((int)pointX -5 , (int)pointY -5 , 10, 10);
					g2.setColor(color);
					g2.fillOval((int)pointX -3 , (int)pointY -3 , 6, 6);
					
					pointX = Math.cos(Math.toRadians(-endAngle)) * currentRadius;
					pointY = Math.sin(Math.toRadians(-endAngle)) * currentRadius;
					g2.setColor(this.m_backgroundColor);
					g2.fillOval((int)pointX -5 , (int)pointY -5 , 10, 10);
					g2.setColor(color);
					g2.fillOval((int)pointX -3 , (int)pointY -3 , 6, 6);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
		
	

}
