package com.xtwsoft.wuproject;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@WebServlet(name = "JsonServlet", urlPatterns = { "/json" }, loadOnStartup=0)
public class JsonServlet extends HttpServlet {
	public void init(ServletConfig servletConfig) throws ServletException {
        try {
        	String realPath = servletConfig.getServletContext().getRealPath("");
        	ServerConfig.initInstance(realPath);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doPage(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doPage(request, response);
	}
	
	public void doPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		ServletOutputStream sos = response.getOutputStream();
		response.setContentType("text/html; charset=UTF-8");
		
		String retInfo = doWork(request);
		
		if(retInfo != null) {
			String callback = request.getParameter("callback");
			if(callback != null) {
				retInfo = callback + "(" + retInfo + ");";
			}
			
			sos.write(retInfo.getBytes("UTF-8"));
		}
	}
	
	private String doWork(HttpServletRequest request) {
		String data = request.getParameter("buildJson");
		if(data != null) {
			return new JsonBuilder().buildJson(data);
		}

		String addJsonName = request.getParameter("addJson");
		if(addJsonName != null) {
			try {
				String jsonContent = getPostContent(request);
				if(jsonContent != null) {
					jsonContent = this.validJsonContent(jsonContent);
					if(jsonContent != null) {
						String strInfo = JsonStore.getInstance().addJson(addJsonName,jsonContent);
						if(strInfo == null) {
							return WebUtil.oKJSON();
						} else {
							return strInfo;
						}
					} else {
						return WebUtil.alert("valid json error!");
					}
				} else {
					return WebUtil.alert("parse json failed!");	
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				return WebUtil.alert(ex);
			}
		} 
		String getJsonName = request.getParameter("getJson");
		if(getJsonName != null) {
			String str = JsonStore.getInstance().getJson(getJsonName);
			if(str != null) {
				return str;
			}
		}
		
		String listJsonName = request.getParameter("listJson");
		if(listJsonName != null) {
			String str = JsonStore.getInstance().listAllJson();
			if(str != null) {
				return str;
			}
		}
		return null;
	}
	
	private String getPostContent(HttpServletRequest request) throws Exception {
		BufferedReader reader = request.getReader();
		String str = reader.readLine();
		if(str == null) {
			return null;
		}
		StringBuilder strBuff = new StringBuilder();
		while(str != null) {
			strBuff.append(str);
			str = reader.readLine();
		}
		return strBuff.toString();
	}
	
	private JSON getJsonFromRequest(HttpServletRequest request) throws Exception {
		String strPostContent = getPostContent(request);
		if(strPostContent == null) {
			return null;
		}
		strPostContent = strPostContent.trim();
		if(strPostContent.startsWith("[")) {
			JSONArray jsonArray = JSON.parseArray(strPostContent);
			return jsonArray;
		} else {
			JSONObject jsonObject = JSON.parseObject(strPostContent);
			return jsonObject;
		}
	}
	
	private String validJsonContent(String jsonContent) throws Exception {
		jsonContent = jsonContent.trim();
		JSON json = null;
		if(jsonContent.startsWith("[")) {
			json = JSON.parseArray(jsonContent);
		} else {
			json = JSON.parseObject(jsonContent);
		}
		if(json == null) {
			return null;
		}
		return json.toJSONString();
	}
}
