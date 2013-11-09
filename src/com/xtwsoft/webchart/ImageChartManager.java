package com.xtwsoft.webchart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.charts.HourRadarChart;
import com.xtwsoft.webchart.charts.LoopChart;
import com.xtwsoft.webchart.charts.PieChart;
import com.xtwsoft.webchart.charts.ServiceChart;
import com.xtwsoft.webchart.charts.WeekCircleChart;

public class ImageChartManager {
	private static ImageChartManager m_instance = null;
	
	public static ImageChartManager getInstance() {
		if(m_instance == null) {
			m_instance = new ImageChartManager();
		}
		return m_instance;
	}
	
	private ImageChartManager() {
	}
	
	public void drawChart(Graphics2D g2,JSONObject chartData,int imageWidth,int imageHeight) {
		Color bgColor = null;
		if(chartData != null) {
			bgColor = ChartUtil.getColor(chartData.getString("bg-colour"));
		}
		if(bgColor == null) {
			bgColor = Color.white;
		}
		g2.setColor(bgColor);
		g2.fillRect(0, 0, imageWidth, imageHeight);
		if(chartData == null) {
			return;
		}
		
		JSONObject legend = chartData.getJSONObject("legend");
		
		String type = chartData.getString("type");
		if("pie".equals(type)) {
			new PieChart(chartData,legend,imageWidth,imageHeight).draw(g2);
		} else if("weekCircle".equals(type)) {
			new WeekCircleChart(chartData,legend,imageWidth,imageHeight).draw(g2);
		} else if("service".equals(type)) {
			new ServiceChart(chartData,legend,imageWidth,imageHeight).draw(g2);
		} else if("loop".equals(type)) {
			new LoopChart(chartData,legend,imageWidth,imageHeight).draw(g2);
		} else if("hourRadar".equals(type)) {
			new HourRadarChart(chartData,legend,imageWidth,imageHeight).draw(g2);
		} 
	}

	public byte[] buildImage(int imageWidth,int imageHeight,JSONObject chartData) {
        ChartImage chartImage = null;
        try {
        	chartImage = (ChartImage) ChartImagePool.pool.borrowObject();
        	chartImage.resetSize(imageWidth, imageHeight);

            chartImage.resetGraphics();
            chartImage.setTranslate(0, 0);
            Graphics2D g2 = chartImage.getGraphics();
            
            drawChart(g2,chartData,imageWidth,imageHeight);

            BufferedImage image = chartImage.getImage();

            byte[] imageData = convertImage(image);

            return imageData;
        }
        catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (chartImage != null) {
                	ChartImagePool.pool.returnObject(chartImage);
                }
            }
            catch (Exception e) {
                //do nothing
            }
        }		
	}	
	
    protected byte[] convertImage(BufferedImage buffImage) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buffImage, "png", baos);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }	
}
