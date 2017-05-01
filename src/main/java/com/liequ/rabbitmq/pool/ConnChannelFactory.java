package com.liequ.rabbitmq.pool;

import javax.management.ObjectName;

import com.rabbitmq.client.Channel;
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
		// XXX ObjectName connJmxName;
		ObjectName connJmxName = new ObjectName("");
		BrokerConnection pc = new BrokerConnection(manager,conn, connJmxName);
        return new ConnectionObject(pc);
	}
	
	
	protected void initializeConnection(Connection conn) throws BrokerException{
		if(!conn.isOpen()){
			throw new BrokerException("initializeConnection: connection closed");
		}
	}
	
	public ChannelObject makeObject(BrokerChanelObjectManager manager,BrokerConnection pconn) throws Exception{
		brokerChannel pc = pconn.createChannel();
		ObjectName channelJmxName = new ObjectName("");
		pc.set_jmxName(channelJmxName);
		pc.setObjectManager(manager);
		return new ChannelObject(pc);
	}
}
