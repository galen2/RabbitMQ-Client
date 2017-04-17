package com.pool.imp;

import com.pool.PoolableChannel;
import com.pool.PoolableConnection;

public class MQPooledChannelObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastUseTime = createTime;
    private volatile long lastReturnTime = createTime;
    private volatile long borrowedCount = 0;
    private final PoolableChannel _poolableChannel;
    
    public MQPooledChannelObject(PoolableChannel poolableChannel){
    	this._poolableChannel = poolableChannel;
    }

	public PoolableChannel get_poolableChannel() {
		return _poolableChannel;
	}

	
	
	
    
}
