package com.xtwsoft.wuproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

public class JsonStore {
	private static JsonStore m_instance = null;
	
	public static JsonStore getInstance() {
		if(m_instance == null) {
			m_instance = new JsonStore();
		}
		return m_instance;
	}
	
	private JsonStore() {
	}
	
	public String addJson(String jsonName,String json) {
		File jsonFile = new File(ServerConfig.getInstance().getJsonsPath(),jsonName + ".json");
		try {
			String retMessage = null;
			if(jsonFile.exists() && jsonFile.isFile()) {
				retMessage = WebUtil.warnning("json file is already exist,and replace it with new json!");
			}
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(jsonFile),"UTF-8"));
			writer.write(json);
			writer.flush();
			writer.close();
			return retMessage;
		} catch(Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}
	}
	
	public String getJson(String jsonName) {
		File jsonFile = new File(ServerConfig.getInstance().getJsonsPath(),jsonName + ".json");
		try {
			if(jsonFile.exists() && jsonFile.isFile()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile),"UTF-8"));
				StringBuffer strBuff = new StringBuffer();
				String line = reader.readLine();
				while(line != null) {
					strBuff.append(line);
					line = reader.readLine();
				}
				reader.close();			
				return strBuff.toString();
			} else {
				return "can not find json :" + jsonName;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}
	}
	
	public String listAllJson() {
		File path = ServerConfig.getInstance().getJsonsPath();
		if(path.exists()) {
			ArrayList<String> list = new ArrayList<String>();
			
			File[] files = path.listFiles();
			for(int i=0;i<files.length;i++) {
				String fileName = files[i].getName();
				if(fileName.endsWith(".json")) {
					list.add(fileName.substring(0,fileName.length() - 5));
				}
			}
			return JSON.toJSONString(list);
		} else {
			return "jsons path is not exist!";
		}
	}
	
}
