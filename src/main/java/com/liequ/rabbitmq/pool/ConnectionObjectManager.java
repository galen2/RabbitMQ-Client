package com.liequ.rabbitmq.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionObjectManager extends BaseObjectManager<BrokerConnection>{
    private final ConnChannelFactory factory;
    private final LinkedBlockingDeque<ConnectionObject> idleConnObjects;
    
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private volatile int maxConnTotal = BaseConfig.DEFAULT_MAX_CONN_TOTAL;
    
    private final Map<IdentityWrapper<BrokerConnection>, ConnectionObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<BrokerConnection>, ConnectionObject>();
    

	public ConnectionObjectManager(ConnChannelFactory pooledConnectionFactory){
		this.factory = pooledConnectionFactory;
		idleConnObjects = new LinkedBlockingDeque<ConnectionObject>();
	}
	

	/**
	 * 创建连接池的连接 此方法不对外开放，客户端没权限访问此对象
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	protected ConnectionObject borrowConnObject() throws Exception{
		ConnectionObject pcnn = null;
		while (pcnn == null) {
			pcnn = idleConnObjects.pollFirst();
			if (pcnn == null ) {
				pcnn = create();
				return pcnn;
			}
		}
		return pcnn;
	}
	
	private ConnectionObject create() throws Exception{
		final ConnectionObject p;
		try {
			int localtMaxTotal = getMaxConnTotal();
			long newCreateCount = createConnCount.incrementAndGet();
			if (newCreateCount > localtMaxTotal){
				createConnCount.decrementAndGet();
				return null;
			}
			p = factory.makeObject(this);
		} catch (Exception e) {
			throw e;
		}
		BrokerConnection cnn = p.get_poolableConn();
		allObjects.put(new IdentityWrapper<BrokerConnection>(cnn), p);
		return p;
	}
	
	public void returnObject(BrokerConnection  cnn){
		ConnectionObject pp = allObjects.get(new IdentityWrapper<BrokerConnection>(cnn));
		idleConnObjects.addLast(pp);
	}
	
	
	protected void addObject() throws Exception{
		ConnectionObject p = create();
		idleConnObjects.addFirst(p);
	}
	

	public int getMaxConnTotal() {
		return maxConnTotal;
	}


	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}
	
}
