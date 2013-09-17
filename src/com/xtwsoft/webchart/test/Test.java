package com.xtwsoft.webchart.test;

public class Test {

	public static void main(String[] args) {
		try {
			String str=java.net.URLEncoder.encode("json?getJson=pie0", "UTF-8");
			System.err.println(str);
			
			String s0 = java.net.URLDecoder.decode(str,"UTF-8");
			System.err.println(s0);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
