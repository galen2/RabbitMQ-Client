package com.pool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.management.ObjectName;

import com.pool.datasources.PoolChanelObjectManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class PoolableChannel extends DelegatingChannel<Channel>{
	 private final ObjectName _jmxName;
	 private final PoolChanelObjectManager objectManager;
	 
	public PoolableChannel(PoolChanelObjectManager objectManager,Channel channel,DelegatingConnection<Connection> conn,ObjectName jmx){
		super(channel, conn);
		this._jmxName = jmx;
		this.objectManager = objectManager;
	}

	public ObjectName get_jmxName() {
		return _jmxName;
	}

	@Override
	public void close() throws IOException, TimeoutException {
		objectManager.returnObject(this);
	}

	
	
	
}
