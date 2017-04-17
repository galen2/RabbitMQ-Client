package com.pool.imp;

import java.util.concurrent.atomic.AtomicLong;

import com.pool.PoolableConnection;

public class MQPooledConnObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastUseTime = createTime;
    private volatile long lastReturnTime = createTime;
    private volatile long borrowedCount = 0;
    
    public final AtomicLong channelCount = new AtomicLong(0);//已经创建的channel数

    private final PoolableConnection _poolableConn;

    public MQPooledConnObject(PoolableConnection poolableConnection){
    	this._poolableConn = poolableConnection;
    }

	public PoolableConnection get_poolableConn() {
		return _poolableConn;
	}
	
	public AtomicLong getChannelCount(){
		return channelCount;
	}
    
}
