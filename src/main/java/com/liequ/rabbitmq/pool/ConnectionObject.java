package com.liequ.rabbitmq.pool;

import java.util.concurrent.atomic.AtomicLong;

public class ConnectionObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastReturnTime = createTime;
    
    public final AtomicLong channelCount = new AtomicLong(0);//已经创建的channel数

    private final BrokerConnection _poolableConn;

    public ConnectionObject(BrokerConnection poolableConnection){
    	this._poolableConn = poolableConnection;
    }

	public BrokerConnection get_poolableConn() {
		return _poolableConn;
	}
	
	public AtomicLong getChannelCount(){
		return channelCount;
	}
    
}
