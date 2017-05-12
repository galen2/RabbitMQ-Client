package com.liequ.rabbitmq.pool;

import java.util.ArrayList;

import com.rabbitmq.client.Address;

public class BrokerConfig {
	private String userName;
	private String passsWord;
	private String virtualHost;
	private ArrayList<Address> serverPortAddress;
	private int maxConnTotal = BaseConfig.DEFAULT_MAX_CONN_TOTAL;
	private int initialSize = BaseConfig.DEFAULT_INITIAL_CONN_SIZE;
	private  long maxWaitMillis = BaseConfig.DEFAULT_MAX_WAIT_MILLIS_CHANNEL;
	private  int maxChannelCountPerConn = BaseConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;
    private  boolean blockWhenExhausted = BaseConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPasssWord(String passsWord) {
		this.passsWord = passsWord;
	}


	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}


	public void setServerPortAddress(ArrayList<Address> serverPortAddress) {
		this.serverPortAddress = serverPortAddress;
	}


	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}


	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}


	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}


	public void setMaxChannelCountPerConn(int maxChannelCountPerConn) {
		this.maxChannelCountPerConn = maxChannelCountPerConn;
	}


	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}


    
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public int getMaxChannelCountPerConn() {
		return maxChannelCountPerConn;
	}
	public int getMaxConnTotal() {
		return maxConnTotal;
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

	
	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}
    
}
