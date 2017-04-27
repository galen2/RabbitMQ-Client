package com.pool;

import java.util.ArrayList;

import com.rabbitmq.client.Address;

public class PoolConfig {
	private String userName;
	private String passsWord;
	private String virtualHost;
	private ArrayList<Address> serverPortAddress;
	private int maxConnTotal;//最大连接数
	private int initialSize = 0;//初始化连接数
	private int minIdle;
	private int maxIdel;
	
	//获取channel最大等待时间
	private  long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
	 
	//单个连接创建最大channel数
    private  int maxChannelCountToConn = BasePoolConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;
    
    private  boolean blockWhenExhausted = BasePoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;
    
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}
	public int getMaxChannelCountToConn() {
		return maxChannelCountToConn;
	}
	public void setMaxChannelCountToConn(int maxChannelCountToConn) {
		this.maxChannelCountToConn = maxChannelCountToConn;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPasssWord() {
		return passsWord;
	}
	public void setPasssWord(String passsWord) {
		this.passsWord = passsWord;
	}
	public String getVirtualHost() {
		return virtualHost;
	}
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	
	
	public int getInitialSize() {
		return initialSize;
	}
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}
	public ArrayList<Address> getServerPortAddress() {
		return serverPortAddress;
	}
	public void setServerPortAddress(ArrayList<Address> serverPortAddress) {
		this.serverPortAddress = serverPortAddress;
	}
	
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public int getMaxIdel() {
		return maxIdel;
	}
	public void setMaxIdel(int maxIdel) {
		this.maxIdel = maxIdel;
	}
	public int getMaxConnTotal() {
		return maxConnTotal;
	}
	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}
	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}
	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}
	
    
}
