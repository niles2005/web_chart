package com.xtwsoft.webchart.vmlChart;

import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.vmlChart.VmlBarStackChart;
import com.xtwsoft.webchart.vmlChart.VmlLineChart;


public class VmlChartManager {
	private static VmlChartManager m_instance = null;
	
	public static VmlChartManager getInstance() {
		if(m_instance == null) {
			m_instance = new VmlChartManager();
		}
		return m_instance;
	}
	
	private VmlChartManager() {
	}
	
	public String buildChart(int width,int height,JSONObject chartData) {
		if(chartData == null) {
			return null;
		}
		String type = chartData.getString("type");
		if("line".equals(type)) {
			return new VmlLineChart(chartData,width,height).buildChart();
		} else if("bar-stack".equals(type)) {
			return new VmlBarStackChart(chartData,width,height).buildChart();
		} else if("h-bar-stack".equals(type)) {
			return new VmlHBarStackChart(chartData,width,height).buildChart();
		}
		return null;
	}
	

}
