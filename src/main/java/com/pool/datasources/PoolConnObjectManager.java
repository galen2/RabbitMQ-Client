package com.pool.datasources;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolConfig;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolConnObjectManager<T> {
    private final PooledConnChannelFactory factory;
    private final LinkedBlockingDeque<MQPooledConnObject> idleConnObjects;
//    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxConnTotal = BasePoolConfig.DEFAULT_MAX_CONN_TOTAL;
    
    private volatile boolean blockWhenExhausted = BasePoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

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
		boolean blockWhenExhausted = getBlockWhenExhausted();
		while(pcnn==null){
			pcnn = idleConnObjects.pollFirst();
			if(pcnn == null ){
				pcnn = createPooledConnObject();
				
				if(pcnn == null){
					if(blockWhenExhausted){
						if(borrowMaxWaitMillis < 0) {
							pcnn = idleConnObjects.take();
						} else {
							pcnn = idleConnObjects.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
						}
						
						if(pcnn == null){
							throw new NoSuchElementException("Timeout waiting for idle object");
						}
					} else {
						throw new NoSuchElementException("conn pool exhausted");
					}
				}
			}
		}
		return pcnn;
	}
	
	private MQPooledConnObject createPooledConnObject() throws Exception{
		final MQPooledConnObject p;
		try {
			int localtMaxTotal = getMaxConnTotal();
			long newCreateCount = createConnCount.incrementAndGet();
			if(newCreateCount > localtMaxTotal){
				createConnCount.decrementAndGet();
				return null;
			}
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
		idleConnObjects.addLast(pp);
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



	public int getMaxConnTotal() {
		return maxConnTotal;
	}



	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}



	public boolean getBlockWhenExhausted() {
		return blockWhenExhausted;
	}



	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}
	
	
	
	
	
}
