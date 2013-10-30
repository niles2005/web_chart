package com.xtwsoft.webchart.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.ChartUtil;

public abstract class BaseChart {
	protected JSONObject m_element = null;
	protected JSONObject m_legend = null;
	protected JSONArray m_keys = null;
	protected JSONArray m_values = null;
	protected int m_imageWidth;
	protected int m_imageHeight;
	protected Color m_backgroundColor = Color.WHITE;
	public BaseChart(JSONObject element,JSONObject legend,int imageWidth,int imageHeight) {
		m_element = element;
		m_legend = legend;
		m_keys = m_element.getJSONArray("keys");
		m_values = element.getJSONArray("values");
		m_imageWidth = imageWidth;
		m_imageHeight = imageHeight;
		Color bgColor = ChartUtil.getColor(element.getString("bg-colour"));
		if(bgColor != null) {
			m_backgroundColor = bgColor;
		}
	}
	public abstract void draw(Graphics2D g2);
	
	public double getDouble(JSONObject object,String name) {
		return getDouble(object,name,0);
	}
	
	public double getDouble(JSONObject object,String name,double defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).doubleValue();
		} else if(value instanceof Double) {
			return (Double)value;
		} else if(value instanceof Float) {
			return (Double)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}	
	public float getFloat(JSONObject object,String name) {
		return getFloat(object,name,0);
	}
	

	public float getFloat(JSONObject object,String name,float defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).floatValue();
		} else if(value instanceof Double) {
			return (Float)value;
		} else if(value instanceof Float) {
			return (Float)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}	
	
	public int getInt(JSONObject object,String name) {
		return getInt(object,name,0);
	}
	

	public int getInt(JSONObject object,String name,int defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).intValue();
		} else if(value instanceof Double) {
			return ((Double)value).intValue();
		} else if(value instanceof Float) {
			return ((Float)value).intValue();
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return defaultValue;
	}		
	

	public Color getColor(JSONObject object,String name,Color defaultColor) {
		Color color = ChartUtil.getColor(object.getString(name));		
		if(color == null) {
			color = defaultColor;
		}
		return color;
	}
	
	//图标的宽度，缺省为10
	float m_legendIconWidth = 0;
	public float getLegendIconWidth() {
		if(m_legendIconWidth <= 0) {
			m_legendIconWidth = m_legend.getFloatValue("icon-width");
			if(m_legendIconWidth <= 0) {
				m_legendIconWidth = 10;
			}
		}
		return m_legendIconWidth;
	}
	
	//图标与标题之间的距离,缺省为3
	float m_legendIconLabelMargin = 0;
	public float getLegendIconLabelMargin() {
		if(m_legendIconLabelMargin <= 0) {
			m_legendIconLabelMargin = m_legend.getFloatValue("icon-label-margin");
			if(m_legendIconLabelMargin <= 0) {
				m_legendIconLabelMargin = 3;
			}
		}
		return m_legendIconLabelMargin;
	}
	
	//竖排label时，上一个label和下一个label的间距,缺省为1
	float m_legendOffset = 0;
	public float getLegendOffset() {
		if(m_legendOffset <= 0) {
			m_legendOffset = m_legend.getFloatValue("offset");
			if(m_legendOffset <= 0) {
				m_legendOffset = 1;
			}
		}
		return m_legendOffset;
	}
	
	private int m_fontFamily = -1;
	public int getFontFamily() {
		if(m_fontFamily == -1) {
			String strFontFalimy = m_element.getString("font-family");
			if("BOLD".equalsIgnoreCase(strFontFalimy)) {
				m_fontFamily = Font.BOLD;
			} else if("ITALIC".equalsIgnoreCase(strFontFalimy)) {
				m_fontFamily = Font.ITALIC;
			} else {
				m_fontFamily = Font.PLAIN;
			}
		}
		return m_fontFamily;
	}
	
	protected void drawLegend(Graphics2D g2,int rectX,int rectY,int rectWidth,int rectHeight,double legendWidth,double legendHeight,int graphWidth,int graphHeight) {
		if(m_keys == null) {
			return;
		}
		g2.setFont(new java.awt.Font("SimSun",java.awt.Font.PLAIN,12));//宋体
		int rr = (int)this.getLegendIconWidth();
		double drawPartWidth = legendWidth + this.getLegendIconWidth() + this.getLegendIconLabelMargin();   

		int x = rr /2;
		int y = 0;
		FontMetrics fm = g2.getFontMetrics();
		
		double labelX = rectX + rectWidth / 2 - drawPartWidth / 2;
		double labelY = rectHeight / 2-legendHeight /2;
		/**
		 * 对于摆右侧（position：right）的图例，缺省为align=middle，图例在中间
		 * align=top,图例在顶
		 * align=middle，图例在中间
		 * align=bottom，图例在底部
		 */
		String align = m_legend.getString("align");
		if("top".equals(align)) {
			labelY = rectHeight / 2 - graphHeight / 2;
		} else if("bottom".equals(align)) {
			labelY = rectHeight / 2 + graphHeight / 2 - legendHeight;
		} else {//default center 
			labelY = rectHeight / 2-legendHeight /2;
		}
		g2.translate(labelX, labelY);
		
		int labelHeight = 0;
		int ascent = fm.getAscent();
		for(int i=0;i<m_keys.size();i++) {
			JSONObject key = m_keys.getJSONObject(i);
			g2.setColor(ChartUtil.getColor(key.getString("colour")));
			String text = key.getString("text");
			if(labelHeight == 0) {
				Rectangle2D rect = fm.getStringBounds(text, g2);
				labelHeight = (int)rect.getHeight();
			}
			g2.setFont(g2.getFont().deriveFont(this.getFloat(key, "font-size", 12)));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fillOval(x, (int)(y + labelHeight  - rr), rr, rr);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
			g2.drawString(text, x + this.getLegendIconWidth() + this.getLegendIconLabelMargin() , y+ ascent);
			 
			y += labelHeight + this.getLegendOffset();
		}		
	}


	
}
