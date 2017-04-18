package com.pool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.management.ObjectName;

import com.pool.datasources.PoolConnObjectManager;
import com.rabbitmq.client.Connection;

public class PoolableConnection extends  DelegatingConnection<Connection>{
	 private final ObjectName _jmxName;
	 private final PoolConnObjectManager manager;

	public PoolableConnection(PoolConnObjectManager manager,Connection conn,ObjectName jmx){
		super(conn);
		this._jmxName = jmx;
		this.manager  = manager;
	}

	public ObjectName get_jmxName() {
		return _jmxName;
	}
	
	@Override
	public void close() throws IOException {
		manager.returnObject(this);
	}

	
	
	
}
