package com.xtwsoft.webchart.vmlChart;

import java.math.BigDecimal;
import java.util.Hashtable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class BaseVmlChart {
	protected int m_gridMarginLeft = 0;//网格与图片边界距离
	protected int m_gridMarginRight = 0;
	protected int m_gridMarginTop = 0;
	protected int m_gridMarginBottom = 0;
 
	protected int m_gridX = 0;//网格x
	protected int m_gridY = 0;//网格y
	protected int m_gridW = 0;//网格宽
	protected int m_gridH = 0;//网格高
	
    protected float m_xMin = 0;
    protected float m_xMax = 0;
	
    protected float m_yMin = 0;
    protected float m_yMax = 0;
	
	protected JSONObject m_legend;

	protected JSONObject m_xAxis;
	protected JSONArray m_xLabels = null;

	protected JSONObject m_yAxis;
	protected JSONArray m_yLabels = null;
	protected JSONArray m_keys = null;//图例标题数组
	protected JSONArray m_values = null;
	protected int m_width;//图片宽
	protected int m_height;//图片高
	protected JSONObject m_chartData = null;
	protected int m_subXBarWidth = 0;
	protected int m_subYBarWidth = 0;
	protected float m_barWidthRate = 0;

	
	public BaseVmlChart(JSONObject chartData,int width,int height) {
		m_chartData = chartData;
		m_width = width;
		m_height = height;
		m_gridMarginLeft = getInt(chartData,"grid-margin-left");
		m_gridMarginRight = getInt(chartData,"grid-margin-right");
		m_gridMarginTop = getInt(chartData,"grid-margin-top");
		m_gridMarginBottom = getInt(chartData,"grid-margin-bottom");
		
		
		m_legend = chartData.getJSONObject("legend");
		
		m_values = chartData.getJSONArray("values");
		m_keys = chartData.getJSONArray("keys");
		
		m_xAxis = chartData.getJSONObject("x-axis");
		if(m_xAxis != null) {
			m_xLabels = m_xAxis.getJSONArray("labels");

			if(m_xAxis.get("min") == null || m_xAxis.get("max") == null) {
	            this.m_xMin = this.m_xLabels.getJSONObject(0).getFloatValue("x");
	            this.m_xMax = this.m_xLabels.getJSONObject(this.m_xLabels.size() - 1).getFloatValue("x");
			} else {
				m_xMin = this.m_xAxis.getFloat("min");
				m_xMax = this.m_xAxis.getFloat("max");
			}
		}
		
		
		m_yAxis = chartData.getJSONObject("y-axis");
		if(m_yAxis != null) {
			m_yLabels = m_yAxis.getJSONArray("labels");
			
			if(m_yAxis.get("min") == null || m_yAxis.get("max") == null) {
	            this.m_yMin = this.m_yLabels.getJSONObject(0).getFloatValue("y");
	            this.m_yMax = this.m_yLabels.getJSONObject(this.m_yLabels.size() - 1).getFloatValue("y");
			} else {
				m_yMin = this.m_yAxis.getFloat("min");
				m_yMax = this.m_yAxis.getFloat("max");
			}
		}
		
		m_gridX = m_gridMarginLeft;
		m_gridY = m_gridMarginTop;
		m_gridW = width - m_gridMarginLeft - m_gridMarginRight;
		m_gridH = height - m_gridMarginTop - m_gridMarginBottom;
		
		m_barWidthRate = getFloat(chartData,"bar-width",0.5f);
	}
	
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
	
	public float getFloat(Object value) {
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).floatValue();
		} else if(value instanceof Double) {
			return (Float)value;
		} else if(value instanceof Float) {
			return (Float)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		}
		return 0;
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
	
	public boolean getBoolean(JSONObject object,String name,boolean defaultValue) {
		Object value = object.get(name);
		if(value == null) {
			return defaultValue;
		}
		if(value instanceof BigDecimal) {
			return ((BigDecimal)value).intValue() != 0;
		} else if(value instanceof Double) {
			return ((Double)value).intValue() != 0;
		} else if(value instanceof Float) {
			return ((Float)value).intValue() != 0;
		} else if(value instanceof Integer) {
			return (Integer)value != 0;
		} else if(value instanceof Boolean) {
			return (Boolean)value;
		}
		return defaultValue;
	}		
	
	public String getString(JSONObject object,String name,String defaultValue) {
		String value = object.getString(name);
		if(value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}		
	
	protected int getXPos(float value) {
		double rate = 1.0 * (value - m_xMin) / (this.m_xMax - this.m_xMin);
		return (int)(this.m_gridX + rate * this.m_gridW);
	}

	protected int getYPos(float value) {
        double rate = 1.0 * (value - m_yMin) / (this.m_yMax - this.m_yMin);
        return (int)(m_gridY + m_gridH - rate * m_gridH);
    }

    protected void drawLine(StringBuffer strBuff,int x1,int y1,int x2,int y2,String color,String strokeWeight,VmlDash dash) {
    	String from = x1 + "," + y1;
    	String to = x2 + "," + y2;
    	strBuff.append("<v:line from = '" + from + "' to = '" + to + "' strokecolor = '" + color + "' strokeweight = '" + strokeWeight + "'>\r\n");
    	if(dash != null && dash.hasDash()) {
    		strBuff.append("<v:stroke dashstyle = '" + dash.getVmlDash() + "'>\r\n");
			strBuff.append("</v:stroke>\r\n");
    	}                
    	strBuff.append("</v:line>\r\n");
    }
    
    protected void drawLabel(StringBuffer strBuff,int x,int y,String text,String fontSize) {
    	if(text == null || text.length() == 0) {
    		return;
    	}
    	int width = 0;
    	if(text.charAt(0) > 256) {
    		width = text.length() * 30;
    	} else {
    		width = text.length() * 20;
    	}
    	
    	strBuff.append("<v:rect style=' WIDTH: " + width + "px;  HEIGHT: 23px; TOP: " + y + "px; LEFT: " + x + "px' >\r\n");
		strBuff.append("<v:stroke opacity = '0'>\r\n");
		strBuff.append("</v:stroke>\r\n");
		strBuff.append("<v:fill opacity = '0'>\r\n");
		strBuff.append("</v:fill>\r\n");
		strBuff.append("<v:textbox style=' FONT: " + fontSize + "pt Calibri; '>" + text + "</v:textbox>\r\n");
		strBuff.append("</v:rect>\r\n");
     }    
    
	protected void drawLegend(StringBuffer strBuff) {
		Object visible = m_legend.get("visible");
		if(visible instanceof Boolean) {//不设visible，或者设置为非false，显示。设置为false，不显示。
			if(!(Boolean)visible) {
				return;
			}
		}
		strBuff.append("<div style='width: " + m_width + "px;position:relative;margin:0 auto;text-align:center;'>\r\n");
		String legendIcon = this.m_legend.getString("icon");
		String iconStyle = "display: inline-block;width: 23px;height: 16px;vertical-align: top;background-image: url(/images/" + legendIcon + ".png);background-repeat: no-repeat;";
		for(int i=0;i<m_keys.size();i++) {
			JSONObject key = m_keys.getJSONObject(i);
			String colour = key.getString("colour");
			String text = key.getString("text");

			strBuff.append("<i style='" + iconStyle + "background-color:" + colour + "'></i><span>" + text + "</span>\r\n");
		}		
		strBuff.append("</div>\r\n");
	}
	
	public String buildChart() {
		StringBuffer strBuff = new StringBuffer();
		drawLegend(strBuff);
		strBuff.append("<div style='width:" + m_width + "px;position:relative;margin:0 auto;'>\r\n");
		strBuff.append("<v:group style='text-align:left;WIDTH: " + m_width + "px; HEIGHT: " + m_height + "px' coordsize = '" + m_width + "," + m_height + "'>\r\n");
		drawChart(strBuff);
		strBuff.append("</v:group>\r\n");
		strBuff.append("</div>\r\n");
		
		return strBuff.toString();
	}
	
	protected JSONObject getXLabel(String xLabelName) {
		Hashtable labelHash = (Hashtable)this.m_xAxis.get("labelHash");
        if(labelHash != null) {
            return (JSONObject)labelHash.get(xLabelName);
        }
        return null;
	}
	
	protected void drawXAxisAndLabels(StringBuffer strBuff) {
    	if(m_xLabels != null) {
            int poleLength = (int)this.m_xAxis.getFloatValue("pole-length");
            String gridColour = this.m_xAxis.getString("grid-colour");
            String poleColour = this.m_xAxis.getString("pole-colour");
            String colour = this.m_xAxis.getString("colour");
            
            int xNum = m_xLabels.size();
            double xSpace = 1.0 * m_gridW / (xNum + 1);
            
            m_subXBarWidth = (int)(xSpace * m_barWidthRate * 0.5 + 0.5);
            int y0 = m_gridY;
            int y1 = m_gridY + m_gridH;
            
            Object gridVisible = m_xAxis.get("grid-visible");
            if(gridVisible == null) {
                gridVisible = true; 
             }
            
            Hashtable labelHash = new Hashtable(); 
            this.m_xAxis.put("labelHash", labelHash);
            for(int i=0;i<xNum;i++) {
				JSONObject xLabel = m_xLabels.getJSONObject(i);
				
				Object objShow = xLabel.get("show");
				boolean show = true;
				if(objShow == null ) {//缺省为空时，显示标题
					show = true;
				} else if(objShow instanceof Boolean) {
					show = ((Boolean)objShow).booleanValue();
				}
				
                boolean xGridVisible = true;
                if(gridVisible instanceof Boolean) {
                	xGridVisible = (Boolean)gridVisible;
                }
                if(!show && "onlyShow".equals(gridVisible)) {
                    xGridVisible = false;
                }
				
                int xx = (int)(this.getXPos(xLabel.getFloatValue("x")));
                xLabel.put("xPos", xx);
                
				String xText = xLabel.getString("text");
				if(xText != null) {
					labelHash.put(xText, xLabel);
				}
                if(xGridVisible) {
                    String fillStyle = xLabel.getString("fill-style");
                    if(fillStyle != null) {
                    	VmlColor vmlColor = new VmlColor(fillStyle);
                    	strBuff.append("<v:rect style=' WIDTH: " + (this.m_subXBarWidth * 2) + "px;  HEIGHT: " + this.m_gridH + "px; TOP: " + this.m_gridY + "px; LEFT: " + (xx - this.m_subXBarWidth) + "px' title=" + xText + " coordsize = '21600,21600' fillColor='" + vmlColor.getColor() + "'>\r\n");
                    	strBuff.append("<v:stroke opacity = '0'>\r\n");
            			strBuff.append("</v:stroke>\r\n");
            			if(vmlColor.getVmlAlpha() != null) {
                			strBuff.append("<v:fill opacity = '" + vmlColor.getVmlAlpha() + "'>\r\n");
        					strBuff.append("</v:fill>\r\n");
            			}
                    	strBuff.append("</v:rect>\r\n");
                    	
                    }
                	Object lineDash = xLabel.get("line-dash");
                	VmlDash dash = new VmlDash(lineDash);
                	String strokeStyle = xLabel.getString("stroke-style");
                    if(strokeStyle != null) {
                    	VmlColor vmlColor = new VmlColor(fillStyle);
                    	drawLine(strBuff,xx,y0,xx,y1 + poleLength,vmlColor.getColor(),"0.5pt",dash);
                    } else {
        				drawLine(strBuff,xx,y0,xx,y1,gridColour,"0.5pt",dash);
        				drawLine(strBuff,xx,y1,xx,y1 + 3,poleColour,"0.5pt",dash);
                    }
                }
				
				
				if(show) {
//					判断是否为中文
					if(xText.length() > 0) {
						int offsetLeft = 0;
						if(xText.charAt(0) > 256) {//中文
							offsetLeft = (int)(10 * xText.length());
						} else {//英文
							offsetLeft = (int)(5 * xText.length());
						}
					    drawLabel(strBuff,xx - offsetLeft,y1,xText,"8");
					}
				}
            }
    	}
    }
	
	protected void drawYAxisAndLabels(StringBuffer strBuff) {
    	if(m_yLabels != null) {
            boolean gridVisible = getBoolean(m_xAxis,"grid-visible",true);

            String gridColour = this.m_yAxis.getString("grid-colour");
            String colour = this.m_yAxis.getString("colour");
            
            int yNum = m_yLabels.size();
            double ySpace = 1.0 * this.m_gridH / (yNum + 1);
            m_subYBarWidth = (int)(ySpace * this.m_barWidthRate * 0.5);

            for(int i=0;i<yNum;i++) {
                JSONObject yLabel = m_yLabels.getJSONObject(i);
                int yy = (int)(this.getYPos(yLabel.getFloatValue("y")));
                if(gridVisible) {
                	Object lineDash = yLabel.get("line-dash");
                	VmlDash dash = new VmlDash(lineDash);
                    drawLine(strBuff,m_gridX,yy,m_gridX + m_gridW,yy,gridColour,"0.5pt",dash);
                }
                
                drawLabel(strBuff,-5,yy-12,yLabel.getString("text"),"8");
            }
    	}
	}
	
    public abstract void drawChart(StringBuffer strBuff);
}
