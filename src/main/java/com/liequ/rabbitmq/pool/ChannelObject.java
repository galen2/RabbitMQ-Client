package com.liequ.rabbitmq.pool;


public class ChannelObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastUseTime = createTime;
    private volatile long lastReturnTime = createTime;
    private volatile long borrowedCount = 0;
    private final brokerChannel _poolableChannel;
    
    public ChannelObject(brokerChannel poolableChannel){
    	this._poolableChannel = poolableChannel;
    }

	public brokerChannel get_poolableChannel() {
		return _poolableChannel;
	}

	
	
	
    
}
