package com.pool;

import javax.management.ObjectName;

import com.DelegatingMQChannel;
import com.rabbitmq.client.Channel;

public class PoolableChannel extends DelegatingMQChannel{
	 private final ObjectName _jmxName;
	 
	public PoolableChannel(Channel channel,DelegatingMQConnection conn,ObjectName jmx){
		super(channel, conn);
		this._jmxName = jmx;
	}

	public ObjectName get_jmxName() {
		return _jmxName;
	}

	
	
}
