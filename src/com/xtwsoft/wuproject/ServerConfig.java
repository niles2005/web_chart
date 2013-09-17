package com.xtwsoft.wuproject;

import java.io.File;

public class ServerConfig {
	private static ServerConfig m_instance = null;
	private File m_appPath = null;
	private File m_WEBINFPath = null;
	private File m_datasPath = null;
	private File m_configPath = null;
//	private File m_pagesPath = null;
	private File m_jsonsPath = null;

	private ServerConfig(File appPath) {
		m_appPath = appPath;
		m_WEBINFPath = new File(appPath, "WEB-INF");
		if (m_WEBINFPath.exists()) {
			m_datasPath = new File(m_WEBINFPath, "datas");
			m_configPath = new File(m_WEBINFPath, "config");
			if (!m_configPath.exists()) {
				m_configPath.mkdir();
			}
//			m_pagesPath = new File(m_WEBINFPath, "pages");
//			if (!m_pagesPath.exists()) {
//				m_pagesPath.mkdir();
//			}
			m_jsonsPath = new File(m_appPath, "jsons");
			if (!m_jsonsPath.exists()) {
				m_jsonsPath.mkdir();
			}
		} else {
			System.err.println("WEB-INF path not found!");
		}
	}

	public static ServerConfig getInstance() {
		return m_instance;
	}

	public static void initInstance(String path) {
		if (m_instance != null) {
			return;
		}
		m_instance = new ServerConfig(new File(path));
	}

	public File getDatasPath() {
		return m_datasPath;
	}

	public File getConfigPath() {
		return m_configPath;
	}
	
	public File getJsonsPath() {
		return m_jsonsPath;
	}
	
	public File getAppPath() {
		return m_appPath;
	}

	public File getPagesPath() {
		return m_appPath;
	}
}
