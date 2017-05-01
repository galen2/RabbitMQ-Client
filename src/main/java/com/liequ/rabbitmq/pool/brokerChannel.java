package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.management.ObjectName;

import com.liequ.rabbitmq.pool.BrokerChanelObjectManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class brokerChannel extends DelegatedChannel<Channel>{
	 private  ObjectName _jmxName;
	 private  BrokerChanelObjectManager objectManager;
	 
	public brokerChannel(Channel channel,DelegatedConnection<Connection> conn){
		super(channel, conn);
	}

	public BrokerChanelObjectManager getObjectManager() {
		return objectManager;
	}

	
	public void set_jmxName(ObjectName _jmxName) {
		this._jmxName = _jmxName;
	}

	public void setObjectManager(BrokerChanelObjectManager objectManager) {
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
