package com.xtwsoft.webchart.vmlChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.webchart.vmlChart.VmlChartManager;


@WebServlet(name = "VmlChartServlet", urlPatterns = { "/vmlChart" })
public class VmlChartServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doWork(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doWork(request, response);
	}
	
	public void doWork(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		ServletOutputStream sos = response.getOutputStream();

		try {
			String data = getParam(request,"chartData");
			if(data != null) {
				String strWidth = getParam(request,"width");
				String strHeight = getParam(request,"height");
				if(strWidth == null) {
					strWidth = "200";
				}
				if(strHeight == null) {
					strHeight = "200";
				}
				boolean isWrapHtml = false;
				String strHtml = getParam(request,"html");
				if(strHtml != null) {
					isWrapHtml = true;
				}
				int width = Integer.parseInt(strWidth);
				int height = Integer.parseInt(strHeight);
				
				String jsonContent = null;
				if(data.endsWith(".json") || data.endsWith(".jsonx")) {
//					jsonContent = new JsonBuilder().buildJson(request,data);	
				} else {//load data from post
					jsonContent = getPostContent(request);
				}
				
				if(jsonContent != null) {
					jsonContent = jsonContent.trim();
					if(jsonContent.startsWith("[")) {
					} else {
						JSONObject chartData = JSON.parseObject(jsonContent);
						if(chartData != null) {
							String retInfo = VmlChartManager.getInstance().buildChart(width,height,chartData);
							if(retInfo != null) {
								if(isWrapHtml) {
									sos.write(wrapHtml1.getBytes("UTF-8"));
								}
								sos.write(retInfo.getBytes("UTF-8"));
								if(isWrapHtml) {
									sos.write(wrapHtml2.getBytes("UTF-8"));
								}
							}
						}
					}
				}
				return;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	String wrapHtml1 = "<!DOCTYPE html><html><head><title></title>\r\n" +
			"<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\r\n" +
			"<script>\r\n" +
			"var isIELess9 = (navigator.userAgent.indexOf('MSIE') >= 0) && (navigator.userAgent.indexOf('Opera') < 0);\r\n" + 
			"if(isIELess9) {//VML\r\n" + 
			"   if (document.namespaces['v'] == null) {\r\n" +
			"       var e = ['shape', 'shapetype', 'group', 'background', 'path', 'formulas', 'handles', 'fill', 'stroke', 'shadow', 'textbox', 'textpath', 'imagedata', 'line', 'polyline', 'curve', 'roundrect', 'oval', 'rect', 'arc', 'image'], s = document.createStyleSheet();\r\n" +
			"       for (var i = 0; i < e.length; i++) {\r\n" +
			"           s.addRule('v\\\\:' + e[i], 'behavior: url(#default#VML); display: inline-block;');\r\n" +
			"       }\r\n" +
			"       document.namespaces.add('v', 'urn:schemas-microsoft-com:vml');\r\n" +
			"   }\r\n" +
			"}\r\n" +
			"</script>\r\n" +
			"</head>\r\n" +
			"<body>\r\n";

	String wrapHtml2 = "</body>\r\n</html>";
	
	private String getParam(HttpServletRequest request, String key) {
		String str = (String)request.getAttribute(key);
		if(str == null) {
			str = request.getParameter(key);
		}
		return str;
	}
	
	public String getPageContent(File pageFile) {
		if(pageFile.exists()) {
			StringBuffer strBuff = new StringBuffer();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pageFile),"UTF-8"));
				String line = reader.readLine();
				while(line != null) {
					strBuff.append(line);
					strBuff.append("\r\n");
					line = reader.readLine();
				}
				reader.close();
				return strBuff.toString();
			} catch(Exception ex) {
				ex.printStackTrace();
				return ex.getMessage();
			}
		} else {
			return null;
		}
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
	
	
}
