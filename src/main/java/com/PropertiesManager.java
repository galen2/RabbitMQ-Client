package com;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
/**
 * 属性文件加载管理类，单例模式
 * @author shijin
 *
 */
public class PropertiesManager {
	private static Properties pro = new Properties();
	private PropertiesManager(){}
	static {
		try {
			pro.load(PropertiesManager.class.getClassLoader().getResourceAsStream("config" + File.separator + "DB.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key) {
		return pro.getProperty(key);
	}
	
	public static String getProperty(String key,String defaultValue) {
		//找不到key用defaultValue，而不是说后面为空字符串采用defaultValue
		return pro.getProperty(key, defaultValue);
	}
	
	public static Enumeration<?> propertiesNames() {
		return pro.propertyNames();
	}
}
