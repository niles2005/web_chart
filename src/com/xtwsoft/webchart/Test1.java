package com.xtwsoft.webchart;

public class Test1 {

	public static void main(String[] args) {
		int angle = 90;
		int r = 100;
		double x =  r * Math.cos(angle * Math.PI / 180);
		double y =  r * Math.sin(angle * Math.PI / 180);
		System.err.println(x);
		System.err.println(y);
	}

}
