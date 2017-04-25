package com.pool.datasources;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolableChannel;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledChannelObject;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolChanelObjectManager<T> {
    private final PooledConnChannelFactory factory;
    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    private final AtomicLong createConnCount = new AtomicLong(0);
    private final PoolConnObjectManager poolConnObjectManager;
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxChannelConnTotal = BasePoolConfig.DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN;

    private final Map<IdentityWrapper<PoolableChannel>, MQPooledChannelObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<PoolableChannel>, MQPooledChannelObject>();
    
    
	public PoolChanelObjectManager(PooledConnChannelFactory pooledConnectionFactory,PollObjectMangerConfig config,PoolConnObjectManager manager){
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
				if(pchan == null){
					throw new NoSuchElementException("channel pool  exhausted");
				}
			}
		}
		return pchan.get_poolableChannel();
	}
	
	private MQPooledConnObject boorowConnValidObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledConnObject pooledConnObject = null;
		for(;;){
			pooledConnObject = poolConnObjectManager.borrowConnObject(borrowMaxWaitMillis);
			AtomicLong count = pooledConnObject.getChannelCount();
			if(count.get()<=maxChannelConnTotal){
				poolConnObjectManager.returnObject(pooledConnObject.get_poolableConn());
				break;
			}
		}
		return pooledConnObject;
	}
	
	private MQPooledChannelObject createPooledChannelObject(MQPooledConnObject pooledConnObject) throws Exception{
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
	
	public void returnObject(PoolableChannel obj){
		MQPooledChannelObject p = allObjects.get(new IdentityWrapper<PoolableChannel>(obj));
		idleChannelObjects.addFirst(p);
	}
	
	static class IdentityWrapper<T>{
		T instances;
		public IdentityWrapper(T instances){
			this.instances = instances;
		}
		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return System.identityHashCode(instances);
		}
		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return ((IdentityWrapper)obj).instances == instances;
		}
		
	}
	
}
