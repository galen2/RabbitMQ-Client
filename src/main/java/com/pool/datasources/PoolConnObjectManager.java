package com.pool.datasources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import com.pool.BasePoolConfig;
import com.pool.PoolableConnection;
import com.pool.imp.MQPooledConnObject;

public class PoolConnObjectManager extends BasePoolObjectManager<PoolableConnection>{
    private final PooledConnChannelFactory factory;
    private final LinkedBlockingDeque<MQPooledConnObject> idleConnObjects;
    
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private volatile int maxConnTotal = BasePoolConfig.DEFAULT_MAX_CONN_TOTAL;
    
    private final Map<IdentityWrapper<PoolableConnection>, MQPooledConnObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<PoolableConnection>, MQPooledConnObject>();
    

	public PoolConnObjectManager(PooledConnChannelFactory pooledConnectionFactory){
		this.factory = pooledConnectionFactory;
		idleConnObjects = new LinkedBlockingDeque<MQPooledConnObject>();
	}
	

	/**
	 * 创建连接池的连接 此方法不对外开放，客户端没权限访问此对象
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public MQPooledConnObject borrowConnObject() throws Exception{
		MQPooledConnObject pcnn = null;
		while (pcnn == null) {
			pcnn = idleConnObjects.pollFirst();
			if (pcnn == null ) {
				pcnn = create();
				return pcnn;
			}
		}
		return pcnn;
	}
	
	private MQPooledConnObject create() throws Exception{
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
	
	
	protected void addObject() throws Exception{
		MQPooledConnObject p = create();
		idleConnObjects.addFirst(p);
	}
	
	

	public int getMaxConnTotal() {
		return maxConnTotal;
	}


	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}


	
	
	
	
}
