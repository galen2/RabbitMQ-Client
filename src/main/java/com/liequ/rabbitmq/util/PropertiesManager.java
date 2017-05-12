package com.liequ.rabbitmq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liequ.rabbitmq.exception.ConfigException;

public class PropertiesManager {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesManager.class);

	 public static Properties getProperties(String fileName) throws ConfigException {
         Properties prop = new Properties();
    	 String path = Envm.ROOT.concat(fileName);

        try {
    		 File configFile = new File(path);
    	        LOG.info("Reading configuration from: " + configFile);

            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString()
                        + " file is missing");
            }

            FileInputStream in = new FileInputStream(configFile);
            try {
            	prop.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new ConfigException("Error processing " + path, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Error processing " + path, e);
        }
        return prop;
    }
	 
	 
	    
}
