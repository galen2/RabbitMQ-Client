package com.liequ.rabbitmq.pool;

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
	
	public boolean validateChannelObject(ChannelObject object){
		BrokerChannel channle = object.getPoolableChannel();
		return channle.isOpen();
	}
	
}
