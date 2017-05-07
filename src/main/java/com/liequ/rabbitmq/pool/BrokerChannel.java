package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class BrokerChannel extends DelegatedChannel<Channel>{
	private  BrokerChanelObjectManager objectManager;
	
	public BrokerChannel(Channel channel,DelegatedConnection<Connection> conn){
		super(channel, conn);
	}

	public BrokerChanelObjectManager getObjectManager() {
		return objectManager;
	}
	

	public void setObjectManager(BrokerChanelObjectManager objectManager) {
		this.objectManager = objectManager;
	}


	@Override
	public void close() throws IOException, TimeoutException {
		if (isClosedInternal()) {
			return;
		}
		objectManager.returnObject(this);
	}
	
	@Override
	public void close(int closeCode, String closeMessage) throws IOException,TimeoutException {
		close();
	}
	@Override
	public void abort() throws IOException {
		if (isClosedInternal()) {
			return;
		}
		objectManager.returnObject(this);
	}
	
	@Override
	public void abort(int closeCode, String closeMessage) throws IOException {
		abort();
	}
}
