package com.liequ.rabbitmq.factory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;

public class ConnChannelFactory {
	
	private final BrokerConnectionFactory _connFactory;
	public ConnChannelFactory(BrokerConnectionFactory connFactory){
		this._connFactory = connFactory;
	}
	
	public ConnectionObject makeObject(ConnectionObjectManager manager) throws Exception{
		Connection conn = _connFactory.createConnection();
		if(conn == null){
			throw new IllegalStateException("BrokerConnectionFactory return null from createConnection");
		}
		try {
			initializeConnection(conn);
		} catch (BrokerException e) {
			try {
				conn.close();
			} catch (Exception e2) {
			}
			throw e;
		}
		BrokerConnection pc = new BrokerConnection(manager,conn);
        return new ConnectionObject(pc);
	}
	
	
	protected void initializeConnection(Connection conn) throws BrokerException{
		if(!conn.isOpen()){
			throw new BrokerException("initializeConnection: connection closed");
		}
	}
	
	public BrokerChannel makeObject(BrokerChanelObjectManager manager,BrokerConnection pconn) throws Exception{
		BrokerChannel pc = pconn.createChannel();
		pc.setObjectManager(manager);
		return pc;
	}
	
	public boolean validateConnObject(ConnectionObject object) {
		BrokerConnection conn = object.getPoolableConn();
		return !conn.isClosed();
	}
	
	public void passivateObject(ConnectionObject conn) {
		conn.getPoolableConn().passivate();
	}
	
	public void activateObject(ConnectionObject conn) {
		conn.getPoolableConn().activateObject();
	}
	
	public void destroyObject(ConnectionObject conn) throws IOException{
		conn.getPoolableConn().closeInternal();
	}
	
	
	public boolean validateChannelObject(ChannelObject object){
		BrokerChannel channle = object.getPoolableChannel();
		return !channle.isClosed();
	}
	public void passivateObject(ChannelObject conn) {
		conn.getPoolableChannel().passivate();
	}
	
	public void activateObject(ChannelObject conn) {
		conn.getPoolableChannel().activateObject();
	}
	
	public void destroyObject(ChannelObject conn) throws IOException, TimeoutException{
		conn.getPoolableChannel().closeInternal();
	}
}
