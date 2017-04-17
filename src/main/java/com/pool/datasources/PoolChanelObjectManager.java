package com.pool.datasources;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolableChannel;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledChannelObject;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolChanelObjectManager {
    private final PooledConnectionFactory factory;
    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    private final AtomicLong createConnCount = new AtomicLong(0);
    private final PoolConnObjectManager poolConnObjectManager;
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxChannelConnTotal = BasePoolConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;


	public PoolChanelObjectManager(PooledConnectionFactory pooledConnectionFactory,PollObjectMangerConfig config,PoolConnObjectManager manager){
		this.factory = pooledConnectionFactory;
		idleChannelObjects = new LinkedBlockingDeque<MQPooledChannelObject>();
		this.poolConnObjectManager = manager;
	}
	
	 
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}



	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}


	/*public MQPooledConnObject borrowObject() throws Exception{
		return borrowObject(getMaxWaitMillis());
	}*/

	
	public PoolableChannel borrowObject() throws Exception{
		return borrowObject(getMaxWaitMillis());
	}
	
	
/*	public PoolableChannel borrowObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledConnObject pooledConnObject = null;
		for(;;){
			pooledConnObject = poolConnObjectManager.borrowConnObject(borrowMaxWaitMillis);
			AtomicLong count = pooledConnObject.getChannelCount();
			if(count.get()<=maxChannelConnTotal){
				break;
			}
		}
		return borrowObject(pooledConnObject, borrowMaxWaitMillis);
	}
	*/

	/*private boolean enableCreateChannel(MQPooledConnObject pooledConnObject){
		AtomicLong count = pooledConnObject.getChannelCount();
		return count.get()<=maxChannelConnTotal;
	}*/
	
	/**
	 * 创建连接池的channel
	 * @param pooledConnObject
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public PoolableChannel borrowObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledChannelObject pchan = null;
		long waitTime = System.currentTimeMillis();
		while(pchan==null){
			pchan = idleChannelObjects.pollFirst();
			if(pchan == null ){
				MQPooledConnObject pooledConnObject = boorowConnValidObject(borrowMaxWaitMillis);
				pchan = createPooledChannelObject(pooledConnObject);
			}
		}
		return pchan.get_poolableChannel();
	}
	
	public MQPooledConnObject boorowConnValidObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledConnObject pooledConnObject = null;
		for(;;){
			pooledConnObject = poolConnObjectManager.borrowConnObject(borrowMaxWaitMillis);
			AtomicLong count = pooledConnObject.getChannelCount();
			if(count.get()<=maxChannelConnTotal){
				break;
			}
		}
		return pooledConnObject;
	}
	
	private MQPooledChannelObject createPooledChannelObject(MQPooledConnObject pooledConnObject) throws Exception{
		final MQPooledChannelObject p;
		try {
			PoolableConnection pooledConn = pooledConnObject.get_poolableConn();
			p = factory.makeObject(pooledConn);
			pooledConnObject.getChannelCount().incrementAndGet();
		} catch (Exception e) {
			throw e;
		}
		return p;
	}
	
	
}
