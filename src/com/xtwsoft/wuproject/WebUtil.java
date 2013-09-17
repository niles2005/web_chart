package com.xtwsoft.wuproject;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {
	private static String mesage(String status,String message) {
		return "{\"stauts\":\"" + status + "\",\"message\":\"" + message + "\"}";
	}
	
	public static String success(String info) {
		return mesage("success",info);
	}

	public static String alert(String info) {
		return mesage("alert",info);
	}
	
	public static String warnning(String info) {
		return mesage("warnning",info);
	}
	
	public static String info(String info) {
		return mesage("info",info);
	}
	
	public static String warnning(Exception ex) {
		return warnning(ex.getMessage());
	}
	
	public static String alert(Exception ex) {
		return alert(ex.getMessage());
	}
	
	public static String oKJSON() {
    	return mesage("success","ok");
	}
	
	public static String getUTFString(HttpServletRequest request,String name) {
		try {
			String value = request.getParameter(name);
			if(value == null) {
				return null;
			}
			return new String(value.getBytes("ISO-8859-1"),"UTF-8");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
