package com.xtwsoft.webchart.vmlChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 仅在违法统计页面中显示的饼图。比较PieChart相对简单，图例在上部，不放在右侧。
 * 
 * @author Administrator
 * 
 */
public class VmlLinePieChart extends BaseVmlChart {
	private String[] m_colors = null;
	private int m_valueCount = 0;

	public VmlLinePieChart(JSONObject chartData, int width, int height) {
		super(chartData, width, height);

		m_colors = DefaultColors;
		// JSONArray colors = this.m_chartData.getJSONArray("colours");
		// if(colors == null) {
		// } else {
		//
		// for(int i=0;i<colors.size();i++) {
		//
		// }
		// }
		m_valueCount = this.m_values.size();
		if (m_valueCount > 10) {
			m_valueCount = 10;
		}

	}

	static String[] DefaultColors = new String[] { "#6ADFCC", "#FFFF99",
			"#E95150", "#1C99D3", "#9B54BE", "#EC7A56", "#33CC33", "#FDAD3C",
			"#9999FF", "#FF66FF" };

	public String createPie(double sa, double ea, String color, String label) {
		int r = 2000;
		double fs = Math.PI * 2 * (sa / 360);
		double fe = Math.PI * 2 * (ea / 360);
		int sx = (int) (r * Math.sin(fs));
		int sy = (int) (-r * Math.cos(fs));
		int ex = (int) (r * Math.sin(fe));
		int ey = (int) (-r * Math.cos(fe));
		String ss = "<v:shape title='" + label + "' style='width:" + 2 * r
				+ ";height:" + 2 * r
				+ "' CoordSize='780,355' strokeweight='1pt' fillcolor='"
				+ color + "' strokecolor='black' path='m0,0 l " + sx + "," + sy
				+ " ar -390,-177,390,177," + ex + "," + ey + "," + sx + ","
				+ sy + " l0,0 x e' />";
		return ss;
	}

	public void drawChart(StringBuffer strBuff) {
		double totalValue = 0;
		for (int i = 0; i < m_valueCount; i++) {
			JSONArray arr = this.m_values.getJSONArray(i);
			totalValue += arr.getDouble(1);
		}
		int r = 2000;
		if (totalValue == 0) {
			double fs = Math.PI * 2 * (0); // 角度转换成弧度
			double fe = Math.PI * 2 * (360 / 360);
			double sx = (int) (r * Math.sin(fs));
			double sy = (int) (-r * Math.cos(fs)); // 注意这里有个负号，因为VML的坐标第四像限相当于数学中的第一像限
			double ex = (int) (r * Math.sin(fe));
			double ey = (int) (-r * Math.cos(fe));
			String ss = "<v:shape  style='position:absolute;width:1000px;height:1000px' CoordSize='4000,4000' strokeweight='1pt' fillcolor='#D3D3D3' strokecolor='#D3D3D3' path='m0,0 l 0,-2000 ar -2000,-2000,2000,2000,"
					+ ex + "," + ey + "," + sx + "," + sy + " l0,0 x e' />";
			strBuff.append(ss);
		} else {
			int startAngle0 = (int) getDouble(m_chartData, "start-angle", 0);
//			startAngle0 = 0;
			int startAngle = 90 + startAngle0;
			int totalAngle = 90 + 360 + startAngle0;

			
			for (int i = 0; i < m_valueCount; i++) {
				String colour = m_colors[i];
				JSONArray arr = this.m_values.getJSONArray(i);
				String label = arr.getString(0);
				double value = arr.getDouble(1);

				double angle = 360.0 * value / totalValue;
				int endAngle = (int) (startAngle + angle);
				if (i == m_valueCount - 1) {
					endAngle = totalAngle;
				}
				double fs = Math.PI * 2 * (1.0 * startAngle / 360); // 角度转换成弧度
				double fe = Math.PI * 2 * (1.0 * endAngle / 360);
				int sx = (int) (r * Math.sin(fs));
				int sy = (int) (-r * Math.cos(fs)); // 注意这里有个负号，因为VML的坐标第四像限相当于数学中的第一像限
				int ex = (int) (r * Math.sin(fe));
				int ey = (int) (-r * Math.cos(fe));

				String ss = "<v:shape title='"
						+ label
						+ "' style='position:absolute;width:1000px;height:1000px' CoordSize='4000,4000' strokeweight='1pt' fillcolor='"
						+ colour + "' strokecolor='" + colour
						+ "' path='m0,0  ar -2000,-2000,2000,2000,"
						+ ex + "," + ey + "," + sx + "," + sy + " l0,0 x e' />";
				strBuff.append(ss);
				strBuff.append("\r\n");

				startAngle += angle;
			}
		}

	}

	protected void drawLegend(StringBuffer strBuff) {// for line-pie ,the color
														// is in xLabel
		// 不检测visible
		// Object visible = m_legend.get("visible");
		// if(visible instanceof Boolean)
		// {//不设visible，或者设置为非false，显示。设置为false，不显示。
		// if(!(Boolean)visible) {
		// return;
		// }
		// }
		strBuff.append("<div style='width: "
				+ m_width
				+ "px;margin-bottom:20px;position:relative;margin:0 auto;text-align:center;'>\r\n");
		String legendIcon = this.m_legend.getString("icon");
		String iconStyle = "display: inline-block;width: 23px;height: 16px;vertical-align: top;background-image: url(/images/"
				+ legendIcon + ".png);background-repeat: no-repeat;";
		for (int i = 0; i < m_valueCount; i++) {
			JSONObject xLabel = m_xLabels.getJSONObject(i);
			String colour = m_colors[i];
			String text = xLabel.getString("text");

			strBuff.append("<i style='" + iconStyle + "background-color:"
					+ colour + "'></i><span>" + text + "</span>\r\n");
		}
		strBuff.append("</div>\r\n");
	}

	public String buildChart() {
		StringBuffer strBuff = new StringBuffer();
		drawLegend(strBuff);
		strBuff.append("<div style='width:" + m_height
				+ "px;position:relative;margin:20px auto;height:" + m_height
				+ "px'>\r\n");
		strBuff.append("<v:group style='width: " + m_height + "px; height: "
				+ m_height + "px' >\r\n");
		drawChart(strBuff);
		strBuff.append("</v:group>\r\n");
		strBuff.append("</div>\r\n");

		return strBuff.toString();
	}
}
