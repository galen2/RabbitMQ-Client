package com.liequ.rabbitmq.pool;

import java.io.IOException;

import javax.management.ObjectName;

import com.rabbitmq.client.Connection;

public class BrokerConnection extends  DelegatedConnection<Connection>{
	 private final ObjectName _jmxName;
	 private final ConnectionObjectManager manager;

	public BrokerConnection(ConnectionObjectManager manager,Connection conn,ObjectName jmx){
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
