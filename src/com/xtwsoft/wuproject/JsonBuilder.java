package com.xtwsoft.wuproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.utils.Split;

public class JsonBuilder {
	private Hashtable m_localHash = new Hashtable();
	public JsonBuilder() {
	}
	
	/**
	 * 从模板文件的头部，读取///url=.....的部分,
	 * 
	 * @param templateFile
	 * @return
	 */
	private JSON loadOriginJsonData(File templateFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(templateFile));
			String line = reader.readLine();
			while(line != null) {
				line = line.trim();
				
				if(line.startsWith("///{") && line.endsWith("}")) {//url=
					this.makeLocalValues(line.substring(4,line.length() - 1), false);
				} else if(line.startsWith("///[") && line.endsWith("]")) {//url=
					this.makeLocalValues(line.substring(4,line.length() - 1), true);
				}
				line = reader.readLine();
			}
			reader.close();
			
			String originUrl = (String)m_localHash.get("url");
			
//			System.err.println(originUrl);
			if(originUrl != null && originUrl.startsWith("http")) {
				URL url = new URL(originUrl);
				reader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
				StringBuffer strBuff = new StringBuffer();
				line = reader.readLine();
				while(line != null) {
					strBuff.append(line);
					strBuff.append("\r\n");
					line = reader.readLine();
				}
				reader.close();
				String jsonConent = strBuff.toString().trim();
				if(jsonConent.startsWith("[")) {
					JSONArray jsonArray = JSON.parseArray(jsonConent);
					return jsonArray;
				} else {
					JSONObject jsonObject = JSON.parseObject(jsonConent);
					return jsonObject;
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private void makeLocalValues(String line,boolean isArray) {
		int pos = line.indexOf("=");
		if(pos == -1) {
			return;
		}
		String key = line.substring(0,pos).trim();
		String value = line.substring(pos + 1);
		if(isArray) {
			String[] arr = value.split(",");
			m_localHash.put(key, arr);
		} else {
			m_localHash.put(key, value);
		}
	}
	
	public String buildJson(String dataPath) {
		try {
			File templateFile = new File(ServerConfig.getInstance().getAppPath(),dataPath);
			if(templateFile.exists() && templateFile.isFile()) {
				
			} else {
				return null;
			}
			JSON originData = loadOriginJsonData(templateFile);
			
			StringBuffer strBuff = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(templateFile));
			String line = reader.readLine();
			while(line != null) {
				workLine(strBuff,line,originData);
				line = reader.readLine();
			}
			reader.close();

			return strBuff.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private void workLine(StringBuffer strBuff,String line,JSON originData) {
		if(line.indexOf("///") != -1) {//以///开头的行，前面有空白也算，不写入最终输出
			String newLine = line.trim();
			if(newLine.startsWith("///")) {
				return;
			}
		}
		int index = line.indexOf("***");
		if(index == -1) {
			strBuff.append(line);
			strBuff.append("\r\n");
			return;
		}
		String newLine = line.trim();
		if(newLine.startsWith("***")) {

		} else {
			strBuff.append(line);
			strBuff.append("\r\n");
			return;
		}
//		System.err.println(line);
		line = line.substring(0,index) + line.substring(index + 3);
//		System.err.println(line);
		
		List list = Split.split(line, "$$");
		if(list.size() <= 1) {
			strBuff.append(line);
			strBuff.append("\r\n");
			return;
		}
		if(originData == null) {
			doEachData(strBuff,list,null,0,1);
		} else if(originData instanceof JSONObject) {
			doEachData(strBuff,list,(JSONObject)originData,0,1);
		} else if(originData instanceof JSONArray) {
			JSONArray array = (JSONArray)originData;
			int count = array.size();
			for(int i=0;i<count;i++) {
				doEachData(strBuff,list,array.getJSONObject(i),i,count);
			}
		}
	}
	
	private void doEachData(StringBuffer strBuff,List list,JSONObject data,int dataIndex,int dataCount) {
		int lastIndex = (list.size() / 2) * 2;
		strBuff.append((String)list.get(0));
		for(int i=1;i<list.size();i+=2) {
			String key = (String)list.get(i);//template filed
			String str = fetchData(data,key,dataIndex);
			strBuff.append(str);


			if(i < list.size() - 1) {
				String ss = (String)list.get(i + 1);
				if(i + 1 == lastIndex && dataIndex == dataCount - 1) {//去除数组的最后一个逗号，在fastjson中解析不会出错，但在JsonValidate中会出错
					int pos = ss.lastIndexOf(",");
					if(pos != -1) {
						ss = ss.substring(0,pos);
					}
					strBuff.append(ss);
				} else {
					strBuff.append(ss);
				}
			}
		}
		strBuff.append("\r\n");
		
	}
	
	public String fetchData(JSONObject data,String key,int dataIndex) {
		if(key == null) {
			return "";
		}
		int pos = key.indexOf("|");
		String defaultValue = "";
		if(pos != -1) {
			defaultValue = key.substring(pos + 1);
			key = key.substring(0,pos);
		}
		String value = null;
		if(data != null) {
			value = fetchTheData(data,key);
		}
		if(value == null) {//get from local hash
			value = fetchFromLocal(key,dataIndex);
		}
		if(value == null || value.length() == 0) {
			return defaultValue;
		}
		return value;
	}

	private String fetchTheData(JSONObject data,String key) {
		if(key.indexOf(".") != -1) {
			List list = Split.split(key, ".");
			JSONObject currObject = data;
			for(int i=0;i<list.size();i++) {
				Object value = currObject.get((String)list.get(i));
				if(i == list.size() - 1) {
					if(value != null) {
						return "" + value;
					}
					return null;
				} else {
					if(value instanceof JSONObject) {
						currObject = (JSONObject)value;
					} else {
						return null;
					}
				}
			}
		} else {
			Object value = data.get(key);
			if(value != null) {
				return "" + value;
			}
		}
		return null;
	}	
	
	private String fetchFromLocal(String key,int dataIndex) {
		if(key.indexOf(".") != -1) {//local 仅支持 arr.$index
			List list = Split.split(key, ".");
			if(list.size() == 2 && "$index".equals(list.get(1))) {
				String name = (String)list.get(0);
				Object obj = m_localHash.get(name);
				if(obj instanceof String[]) {
					String v = ((String[])obj)[dataIndex];
					return v;
				}
			}
		} else {
			Object obj = m_localHash.get(key);
			if(obj instanceof String) {
				return obj.toString();
			} else if(obj instanceof String[]) {
				String[] arr = (String[])obj;
				String ss = "";
				for(int i=0;i<arr.length;i++) {
					if(i > 0) {
						ss += ",";
					}
					ss += arr[i];
				}
				return ss;
			}
		}
		
		return null;
	}
}
