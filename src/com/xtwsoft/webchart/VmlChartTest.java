package com.xtwsoft.webchart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.xtwsoft.utils.Split;

public class VmlChartTest {
	public VmlChartTest() {
		try {
			File file = new File("D:\\mywork\\jszt\\workspace\\wuproject\\WebContent\\templates\\vmlchart.htm");
			StringBuffer strBuff = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = reader.readLine();
			while(line != null) {
				strBuff.append(line);
				strBuff.append("\r\n");
				line = reader.readLine();
			}
			reader.close();
			workContent(strBuff.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void workContent(String content) {
		List list = Split.split(content, "><");
		for(int i=0;i<list.size();i++) {
			String item = (String)list.get(i);
			item = item.trim();
			if(item.length() > 0) {
				if(item.startsWith("<")) {
					item = item + ">";
				} else if(item.endsWith(">")) {
					item = "<" + item;
				} else {
					item = "<" + item + ">";
				}
				System.err.println(item);
			}
		}
	}
	
	public static void main(String[] args) {
		new VmlChartTest();
	}

}
