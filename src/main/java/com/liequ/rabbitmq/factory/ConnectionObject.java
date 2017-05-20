package com.liequ.rabbitmq.factory;

import java.util.concurrent.atomic.AtomicLong;

public class ConnectionObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    
    public final AtomicLong channelCount = new AtomicLong(0);//已经创建的channel数

    private final BrokerConnection poolableConn;

    public ConnectionObject(BrokerConnection poolableConnection){
    	this.poolableConn = poolableConnection;
    }

	public BrokerConnection getPoolableConn() {
		return poolableConn;
	}
	
	public AtomicLong getChannelCount(){
		return channelCount;
	}
    
}
