package com.xtwsoft.webchart;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class WebChartTest {
	public static void main(String[] args) {
		File chartFile = new File("D:\\mywork\\jszt\\workspace\\webchart\\pie.json");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(chartFile));
			StringBuffer strBuff = new StringBuffer();
			String line = reader.readLine();
			while(line != null) {
				strBuff.append(line);
				strBuff.append("\r\n");
				line = reader.readLine();
			}
//			System.err.println(strBuff.toString());
			
			JSONObject chartData = JSON.parseObject(strBuff.toString());
			String jsonConent = JSON.toJSONString(chartData, true);
			
			System.err.println(jsonConent);
			
			long t0 = System.currentTimeMillis();
			byte[] imageData = WebChartManager.getInstance().buildImage(290, 200,chartData);
			File f = new File("pie1.png");
			System.err.println(f.getAbsolutePath());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			bos.write(imageData);
			bos.flush();
			bos.close();
			System.err.println(System.currentTimeMillis() - t0);
		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
}
