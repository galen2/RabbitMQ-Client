package com.liequ.rabbitmq.util;

import j.Env;

public class Envm {
	public static final String LINE_SEP = System.getProperty("line.separator");
    
	public static final String ROOT = root();

    private static String root()
    {
    	return Env.class.getResource("/").getFile();
    }
}
