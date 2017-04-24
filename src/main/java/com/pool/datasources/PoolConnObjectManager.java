package com.pool.datasources;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolConfig;
import com.pool.PoolableConnection;
import com.pool.datasources.PoolChanelObjectManager.IdentityWrapper;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolConnObjectManager<T> {
    private final PooledConnChannelFactory factory;
    private final LinkedBlockingDeque<MQPooledConnObject> idleConnObjects;
//    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxTotal = BasePoolConfig.DEFAULT_MAX_CONN_TOTAL;
    
    private final Map<IdentityWrapper<PoolableConnection>, MQPooledConnObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<PoolableConnection>, MQPooledConnObject>();
    

	public PoolConnObjectManager(PooledConnChannelFactory pooledConnectionFactory,PollObjectMangerConfig config){
		this.factory = pooledConnectionFactory;
		idleConnObjects = new LinkedBlockingDeque<MQPooledConnObject>();
//		idleChannelObjects = new LinkedBlockingDeque<MQPooledChannelObject>();
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

	/**
	 * 创建连接池的连接 此方法不对外开放，客户端没权限访问此对象
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public MQPooledConnObject borrowConnObject(long borrowMaxWaitMillis) throws Exception{
		MQPooledConnObject pcnn = null;
		long waitTime = System.currentTimeMillis();
		while(pcnn==null){
			pcnn = idleConnObjects.pollFirst();
			if(pcnn == null ){
				pcnn = createPooledConnObject();
				if(pcnn == null){
					throw new NoSuchElementException("conn pool exhausted");
				}
			}
		}
		return pcnn;
	}
	
	private MQPooledConnObject createPooledConnObject() throws Exception{
		final MQPooledConnObject p;
		try {
			p = factory.makeObject(this);
		} catch (Exception e) {
			throw e;
		}
		PoolableConnection cnn = p.get_poolableConn();
		allObjects.put(new IdentityWrapper<PoolableConnection>(cnn), p);
		return p;
	}
	
	public void returnObject(PoolableConnection  cnn){
		MQPooledConnObject pp = allObjects.get(new IdentityWrapper<PoolableConnection>(cnn));
		idleConnObjects.addFirst(pp);
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
	
	public void setConfig(PoolConfig poolConfig){
		
	}
	
	
	
	
}
