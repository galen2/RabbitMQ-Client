package com.liequ.rabbitmq.pool;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liequ.rabbitmq.exception.ConfigException;
import com.liequ.rabbitmq.util.PropertiesManager;
import com.rabbitmq.client.Address;

public class brokerConfig {
	
    private static final Logger LOG = LoggerFactory.getLogger(brokerConfig.class);

	private String userName;
	private String passsWord;
	private String virtualHost;
	private ArrayList<Address> serverPortAddress;
	private int maxConnTotal;//最大连接数
	private int initialSize = 0;//初始化连接数
	private int minIdle;
	private int maxIdel;
	
	//获取channel最大等待时间
	private  long maxWaitMillis = BaseConfig.DEFAULT_MAX_WAIT_MILLIS;
	//单个连接创建最大channel数
    private  int maxChannelCountPerConn = BaseConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;
    
    private  boolean blockWhenExhausted = BaseConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;
    
    private final static String CONFIG_FILE_NAME = "rabbitmq.properties";
    
  
    public void parse(String brokerName)
    	    throws ConfigException {
    	 Properties prop = PropertiesManager.getProperties(CONFIG_FILE_NAME);
    	 
    	 for (Entry<Object, Object> entry : prop.entrySet()) {
    		 String key = entry.getKey().toString().trim();
             String value = entry.getValue().toString().trim();
             if (key.equals(brokerName + ".userName")) {
            	 userName = value;
             } else if (key.equals(brokerName + ".passsWord")) {
            	 passsWord = value;
             } else if (key.equals(brokerName + ".maxConnTotal")) {
            	 maxConnTotal = Integer.parseInt(value);
             } else if (key.equals(brokerName + ".initialSize")) {
            	 initialSize = Integer.parseInt(value);
             } else if (key.equals(brokerName + ".maxWaitMillis")) {
            	 maxWaitMillis = Long.parseLong(value);
             } else if (key.equals(brokerName + ".maxChannelCountPerConn")) {
            	 maxChannelCountPerConn = Integer.parseInt(value);
             } else if (key.equals(brokerName + ".blockWhenExhausted")) {
            	 blockWhenExhausted = Boolean.getBoolean(value);
             } else if (key.equals(brokerName + ".server")) {
            	 String[] servers = value.split(",");
            	 if (servers.length == 0){
            		 throw new ConfigException("server must be seted");
            	 }
            	 
            	 for (String server : servers) {
            		 ArrayList<Address> addressList = new ArrayList<Address>(3);
            		 String[] addStr = server.split(":");
            		 if (addStr.length !=2 ) {
                		 throw new ConfigException("Unrecognise server "+server);
            		 }
            		 String host = addStr[0];
            		 int port = Integer.parseInt(addStr[1]);
            		 Address address = new Address(host,port);
            		 addressList.add(address);
            	 }
             }/* else {
            	 System.setProperty(key, value);
             }*/
    	 }
    	
    }
    
  
    
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public int getMaxChannelCountPerConn() {
		return maxChannelCountPerConn;
	}


	public String getUserName() {
		return userName;
	}
	public String getPasssWord() {
		return passsWord;
	}
	public String getVirtualHost() {
		return virtualHost;
	}

	
	public int getInitialSize() {
		return initialSize;
	}
	public ArrayList<Address> getServerPortAddress() {
		return serverPortAddress;
	}
	
	public int getMinIdle() {
		return minIdle;
	}
	public int getMaxIdel() {
		return maxIdel;
	}
	public int getMaxConnTotal() {
		return maxConnTotal;
	}
	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}
    
}
