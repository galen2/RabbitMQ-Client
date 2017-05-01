package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 *连接MQ创建连接对象
 */
public class BrokerConnectionFactory {

	private ConnectionFactory _connectionFactory;
	private ArrayList<Address> _serverPortAddress;
	public BrokerConnectionFactory(ConnectionFactory connectionFactory,ArrayList<Address> serverPortAddress){
		this._connectionFactory = connectionFactory;
		this._serverPortAddress = serverPortAddress;
	}
	
	public Connection createConnection() throws IOException, TimeoutException{
		return _connectionFactory.newConnection(_serverPortAddress);
	}
	
	/*public Channel createChannel(Connection conn) throws IOException{
		return conn.createChannel();
	}*/
	
	@Override
    public String toString() {
        return this.getClass().getName();
    }
}
