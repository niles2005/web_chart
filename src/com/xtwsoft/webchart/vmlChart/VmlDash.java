package com.xtwsoft.webchart.vmlChart;

public class VmlDash {
	private String m_vmlDash = null;
	public VmlDash(Object dash) {
		if(dash != null) {
			String ss = "" + dash;
			ss = ss.trim();
			if(ss.equals("[1]")) {
				m_vmlDash = "dot";
			} else {
				m_vmlDash = "LongDash";
			}
		}
	}
	
	public String getVmlDash() {
		return m_vmlDash;
	}
	
	public boolean hasDash() {
		return m_vmlDash != null;
	}
}
