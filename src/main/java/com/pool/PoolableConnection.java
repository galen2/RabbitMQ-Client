package com.pool;

import javax.management.ObjectName;

import com.rabbitmq.client.Connection;

public class PoolableConnection extends  DelegatingMQConnection{
	 private final ObjectName _jmxName;

	public PoolableConnection(Connection conn,ObjectName jmx){
		super(conn);
		this._jmxName = jmx;
	}

	public ObjectName get_jmxName() {
		return _jmxName;
	}

	
	
	
}
