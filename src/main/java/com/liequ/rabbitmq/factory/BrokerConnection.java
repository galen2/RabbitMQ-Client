package com.liequ.rabbitmq.factory;

import java.io.IOException;
import java.sql.SQLException;

import com.rabbitmq.client.Connection;

public class BrokerConnection extends  DelegatedConnection<Connection>{
	private final ConnectionObjectManager manager;
	
	public BrokerConnection(ConnectionObjectManager manager,Connection conn){
		super(conn);
		this.manager  = manager;
	}

	@Override
	public void close() throws IOException {
		if (isClosedInternal()) {
			return;
		}
		manager.returnObject(this);
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
