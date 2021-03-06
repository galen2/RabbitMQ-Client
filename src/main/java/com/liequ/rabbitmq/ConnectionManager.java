package com.liequ.rabbitmq;

import java.util.HashMap;

import com.liequ.rabbitmq.factory.BrokerDataSource;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class ConnectionManager {
	
	private HashMap<String, BrokerDataSource> brokers = new HashMap<String, BrokerDataSource>(4);
	
	private ConnectionManager(){
	}

	private static class innerClass{
		private final static ConnectionManager instance = new ConnectionManager();
	}
	
	public static ConnectionManager getInstance(){
		return innerClass.instance;
	}
	
	public  Channel getChannel(String brokerName) throws Exception{
		if (!brokers.containsKey(brokerName)) {
			synchronized (this) {
				if (!brokers.containsKey(brokerName)){
					BrokerDataSource dataSource = new BrokerDataSource(brokerName);
					brokers.put(brokerName, dataSource);
				}
			}
		}
		
		BrokerDataSource dataSource = brokers.get(brokerName);
		try {
			return dataSource.createChannel();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Connection getConnection(String brokerName,Channel channel) throws Exception{
		BrokerDataSource dataSource = brokers.get(brokerName);
		return dataSource.getConnection(channel);
	}
}

