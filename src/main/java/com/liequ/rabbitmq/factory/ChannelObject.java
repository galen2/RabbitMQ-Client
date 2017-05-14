package com.liequ.rabbitmq.factory;

/**
 *做一些针对该对象数据统计记录，
 *后续完善
 */
public class ChannelObject {
	private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastReturnTime = createTime;
    private final BrokerChannel poolableChannel;
    private ConnectionObject pooledConnObject;
    public ChannelObject(BrokerChannel poolableChannel,ConnectionObject pooledConnObject){
    	this.poolableChannel = poolableChannel;
    	this.pooledConnObject = pooledConnObject;
    }

	public BrokerChannel getPoolableChannel() {
		return poolableChannel;
	}

	public ConnectionObject getPooledConnObject() {
		return pooledConnObject;
	}
	
	
	
	

}
