package com.xtwsoft.webchart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.images.GIF;
import com.xtwsoft.images.ImageFormat;
import com.xtwsoft.images.MinimapImage;
import com.xtwsoft.images.PNG8;
import com.xtwsoft.webchart.charts.WeekCircleChart;
import com.xtwsoft.webchart.charts.ServiceChart;
import com.xtwsoft.webchart.charts.PieChart;

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
	
	protected ImageFormat m_imageFormat = new ImageFormat();
	
	public void drawChart(Graphics2D g2,JSONObject chartData,int imageWidth,int imageHeight) {
		Color bgColor = null;
		if(chartData != null) {
			bgColor = ChartUtil.getColor(chartData.getString("bg_colour"));
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
		
		JSONArray jsonArray = chartData.getJSONArray("elements");
		if(jsonArray.size() > 0) {
			JSONObject element = jsonArray.getJSONObject(0);
			String type = element.getString("type");
			if("pie".equals(type)) {
				new PieChart(element,legend,imageWidth,imageHeight).draw(g2);
			} else if("weekCircle".equals(type)) {
				new WeekCircleChart(element,legend,imageWidth,imageHeight).draw(g2);
			} else if("service".equals(type)) {
				new ServiceChart(element,legend,imageWidth,imageHeight).draw(g2);
			}
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
			MinimapImage image = null;
			if (m_imageFormat.getType() == ImageFormat.PNG) {
				image = new PNG8(buffImage);
			} else if (m_imageFormat.getType() == ImageFormat.GIF) {
                image = new GIF(buffImage);
			} else {
                return null;
			}
			return image.getFileBytes();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
}
