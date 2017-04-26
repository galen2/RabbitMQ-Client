package com.pool;

import java.util.ArrayList;

import com.rabbitmq.client.Address;

public class PoolConfig {
	private String userName;
	private String passsWord;
	private String virtualHost;
	private ArrayList<Address> serverPortAddress;
//	private int maxChannelTotal;//总共最大channel数
	private int maxChannelConn = 2;//单个连接创建最大channel数
	private int maxConnTotal;//最大连接数

	
	
	private int minIdle;
	private int maxIdel;
	private volatile boolean blockWhenExhausted = BasePoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;
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

	
	
	public ArrayList<Address> getServerPortAddress() {
		return serverPortAddress;
	}
	public void setServerPortAddress(ArrayList<Address> serverPortAddress) {
		this.serverPortAddress = serverPortAddress;
	}
	public int getMaxChannelConn() {
		return maxChannelConn;
	}
	public void setMaxChannelConn(int maxChannelConn) {
		this.maxChannelConn = maxChannelConn;
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
