package com.pool.datasources;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolableChannel;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledChannelObject;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolChanelObjectManager<T>  extends BasePoolObjectManager<T>{
    private final PooledConnChannelFactory factory;
    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private final PoolConnObjectManager<T> poolConnObjectManager;
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxChannelCountToConn = BasePoolConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;
    
    private volatile boolean blockWhenExhausted = BasePoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

    private final Map<IdentityWrapper<PoolableChannel>, MQPooledChannelObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<PoolableChannel>, MQPooledChannelObject>();
    
    
	public PoolChanelObjectManager(PooledConnChannelFactory pooledConnectionFactory,PollObjectMangerConfig config,PoolConnObjectManager<T> manager){
		this.factory = pooledConnectionFactory;
		idleChannelObjects = new LinkedBlockingDeque<MQPooledChannelObject>();
		this.poolConnObjectManager = manager;
	}
	
	
	/**
	 * 创建连接池的channel
	 * @param pooledConnObject
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public PoolableChannel borrowObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledChannelObject pchan = null;
		while (pchan == null) {
			pchan = idleChannelObjects.pollFirst();
			if(pchan == null ){
				pchan = create();
				if (pchan == null) {
			        boolean blockWhenExhausted = getBlockWhenExhausted();
					if(blockWhenExhausted){
						if(borrowMaxWaitMillis < 0) {
							pchan = idleChannelObjects.take();
						} else {
							pchan = idleChannelObjects.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
						}
						if (pchan == null) {
							throw new NoSuchElementException("Timeout waiting for idle object");
						}
					} else {
						throw new NoSuchElementException(" channel pool exhausted");
					}
				}
			}
		}
		return pchan.get_poolableChannel();
	}
	
	
	private MQPooledChannelObject create() throws Exception{
		MQPooledConnObject pooledConnObject = borrowConnValidObject();
		if (pooledConnObject == null) {
			return null;
		}
		final MQPooledChannelObject p;
		try {
			PoolableConnection pooledConn = pooledConnObject.get_poolableConn();
			p = factory.makeObject(this,pooledConn);
			PoolableChannel channel = p.get_poolableChannel();
			allObjects.put(new IdentityWrapper<PoolableChannel>(channel), p);
		} catch (Exception e) {
			throw e;
		}
		return p;
	}
	
	private MQPooledConnObject borrowConnValidObject() throws Exception{
		MQPooledConnObject pooledConnObject = null;
		for (;;) {
			pooledConnObject = poolConnObjectManager.borrowConnObject();
			if(pooledConnObject == null){
				return null;
			}
			AtomicLong count = pooledConnObject.getChannelCount();
			int maxChannelCountToConn = getMaxChannelCountToConn();
			if (count.get() <= maxChannelCountToConn) {
				poolConnObjectManager.returnObject(pooledConnObject.get_poolableConn());
				break;
			}
		}
		return pooledConnObject;
	}
	
	
	public void returnObject(PoolableChannel obj){
		MQPooledChannelObject p = allObjects.get(new IdentityWrapper<PoolableChannel>(obj));
		idleChannelObjects.addLast(p);
	}
	
	public void addObject() throws Exception{
		MQPooledChannelObject p = create();
		idleChannelObjects.addFirst(p);
	}
	

	public boolean getBlockWhenExhausted() {
		return blockWhenExhausted;
	}


	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}


	public int getMaxChannelCountToConn() {
		return maxChannelCountToConn;
	}


	public void setMaxChannelCountToConn(int maxChannelCountToConn) {
		this.maxChannelCountToConn = maxChannelCountToConn;
	}
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	


	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}



	
	public PoolableChannel borrowObject() throws Exception{
		return borrowObject(getMaxWaitMillis());
	}
	
}
