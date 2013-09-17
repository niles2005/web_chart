package com.xtwsoft.webchart;

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
import com.alibaba.fastjson.JSONObject;
import com.xtwsoft.wuproject.JsonBuilder;


@WebServlet(name = "ChartServlet", urlPatterns = { "/chart" }, loadOnStartup=0)
public class ChartServlet extends HttpServlet {
	public void init(ServletConfig servletConfig) throws ServletException {
        try {
        	String realPath = servletConfig.getServletContext().getRealPath("");
        	ChartUtil.intWebAppPath(realPath);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
	}

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
		ServletOutputStream sos = response.getOutputStream();

		try {
			String data = request.getParameter("getChartImage");
			if(data != null) {
				int width = Integer.parseInt(request.getParameter("width"));
				int height = Integer.parseInt(request.getParameter("height"));
				String jsonConent = new JsonBuilder().buildJson(data);
				if(jsonConent != null) {
					JSONObject chartData = JSON.parseObject(jsonConent);
					if(chartData != null) {
						byte[] datas = WebChartManager.getInstance().buildImage(width,height,chartData);
						if(datas != null) {
							response.setContentType("image/png");
							sos.write(datas);
						}
					}
				}
				return;
			}
			
			data = request.getParameter("getChartJson");
			if(data != null) {
				String jsonConent = new JsonBuilder().buildJson(data);
				if(jsonConent != null) {
					response.setContentType("text/html; charset=UTF-8");
					sos.write(jsonConent.getBytes("UTF-8"));
				}
				return;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
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
