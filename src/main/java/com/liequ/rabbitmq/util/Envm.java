package com.liequ.rabbitmq.util;


public class Envm {
	public static final String LINE_SEP = System.getProperty("line.separator");
    
	public static final String ROOT = root();

    private static String root()
    {
    	return Envm.class.getResource("/").getFile();
    }
}
