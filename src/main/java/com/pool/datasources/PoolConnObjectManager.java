package com.pool.datasources;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolConfig;
import com.pool.imp.MQPooledChannelObject;
import com.pool.imp.MQPooledConnObject;
import com.pool.imp.PollObjectMangerConfig;

public class PoolConnObjectManager {
    private final PooledConnectionFactory factory;
    private final LinkedBlockingDeque<MQPooledConnObject> idleConnObjects;
    private final LinkedBlockingDeque<MQPooledChannelObject> idleChannelObjects;
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private volatile long maxWaitMillis = BasePoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    
    private volatile int maxTotal = BasePoolConfig.DEFAULT_MAX_CONN_TOTAL;

	public PoolConnObjectManager(PooledConnectionFactory pooledConnectionFactory,PollObjectMangerConfig config){
		this.factory = pooledConnectionFactory;
		idleConnObjects = new LinkedBlockingDeque<MQPooledConnObject>();
		idleChannelObjects = new LinkedBlockingDeque<MQPooledChannelObject>();
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
			}
		}
		return pcnn;
	}
	
	private MQPooledConnObject createPooledConnObject() throws Exception{
		final MQPooledConnObject p;
		try {
			p = factory.makeObject();
		} catch (Exception e) {
			throw e;
		}
		return p;
	}
	
	
	public void setConfig(PoolConfig poolConfig){
		
	}
	
	
}
