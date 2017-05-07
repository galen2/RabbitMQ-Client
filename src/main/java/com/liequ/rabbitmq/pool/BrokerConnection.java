package com.liequ.rabbitmq.pool;

import java.io.IOException;

import com.rabbitmq.client.Connection;

public class BrokerConnection extends  DelegatedConnection<Connection>{
	private final ConnectionObjectManager manager;
	
	public BrokerConnection(ConnectionObjectManager manager,Connection conn){
		super(conn);
		this.manager  = manager;
	}

	@Override
	public void close() throws IOException {
		if (isOpen()) {
			manager.returnObject(this);
		}
	}
	
	@Override
	public void close(int closeCode, String closeMessage) throws IOException {
		close();
	}
	@Override
	public void close(int timeout) throws IOException {
		close();
	}
	@Override
	public void close(int closeCode, String closeMessage, int timeout)
			throws IOException {
		close();
	}
	
}
