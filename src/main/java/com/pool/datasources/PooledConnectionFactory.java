package com.pool.datasources;

import javax.management.ObjectName;

import com.pool.ConnException;
import com.pool.PoolableChannel;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledChannelObject;
import com.pool.imp.MQPooledConnObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class PooledConnectionFactory {
	
	private final BrokerConnectionFactory _connFactory;
	public PooledConnectionFactory(BrokerConnectionFactory connFactory){
		this._connFactory = connFactory;
	}
	
	public MQPooledConnObject makeObject() throws Exception{
		Connection conn = _connFactory.createConnection();
		if(conn == null){
			throw new IllegalStateException("BrokerConnectionFactory return null from createConnection");
		}
		try {
			initializeConnection(conn);
		} catch (ConnException e) {
			// make sure connection is close
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
			throw e;
		}
		// XXX ObjectName connJmxName;
		ObjectName connJmxName = new ObjectName("");
		PoolableConnection pc = new PoolableConnection(conn, connJmxName);
        return new MQPooledConnObject(pc);
	}
	
	
	protected void initializeConnection(Connection conn) throws ConnException{
		if(!conn.isOpen()){
			throw new ConnException("initializeConnection: connection closed");
		}
	}
	
	public MQPooledChannelObject makeObject(PoolableConnection pconn) throws Exception{
		Connection conn = pconn.getDelegate();
		Channel channel = _connFactory.createChannel(conn);
		ObjectName channelJmxName = new ObjectName("");
		PoolableChannel pc = new PoolableChannel(channel, pconn,channelJmxName);
		return new MQPooledChannelObject(pc);
	}

}
