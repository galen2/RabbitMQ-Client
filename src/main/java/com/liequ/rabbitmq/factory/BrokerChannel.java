package com.liequ.rabbitmq.factory;

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
	
	protected void passivate() {
	     setClosedInternal(true);
	}
	
	protected void activateObject(){
		setClosedInternal(false);
	}
	
	public boolean isClosed()  {
        if (isClosedInternal()) {
            return true;
        }
        
        if (!getDelegateInternal().isOpen()) {
            return true;
        }
        return false;
    }
}
