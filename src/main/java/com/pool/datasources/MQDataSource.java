package com.pool.datasources;

import com.pool.PoolConfig;
import com.pool.PoolableChannel;
import com.pool.PoolableConnection;
import com.rabbitmq.client.ConnectionFactory;

public class MQDataSource {
	private volatile MQPoolDataSource dataSource = null;
	private PoolConfig config = null;
	protected MQPoolDataSource createDataSource(){
		if (dataSource !=null ){
			return dataSource;
		}
		
		synchronized (this) {
			if (dataSource != null) {
                return dataSource;
            }
		
			createPoolConfig();
			BrokerConnectionFactory brokerConnectionFactory = createBrokerConnectionFactory();
			PooledConnChannelFactory pooledConnChannelFactory = createPooledConnChannelFactory(brokerConnectionFactory);
			
			PoolConnObjectManager poolConnObjectManager = createPoolConnObjectManager(pooledConnChannelFactory);
			PoolChanelObjectManager poolChanelObjectManager = createPoolChanelObjectManager(pooledConnChannelFactory, poolConnObjectManager);
			
			MQPoolDataSource<PoolableChannel> newDataSource = new MQPoolDataSource<PoolableChannel>(poolChanelObjectManager);
			
			try {
				int initialSize = config.getInitialSize();
				for (int i = 0 ; i < initialSize; i++) {
					poolConnObjectManager.addObject();
				}
			} catch (Exception e) {
				throw new PoolConnException("Error preloading poolConnObject",e);
			}
			dataSource = newDataSource;
		}
		return dataSource;
	}
	
	private void createPoolConfig(){
		config = new PoolConfig();
		
	}
	
	
	private PoolChanelObjectManager createPoolChanelObjectManager(PooledConnChannelFactory pooledConnChannelFactory, PoolConnObjectManager poolConnObjectManager){
		PoolChanelObjectManager poolChanelObjectManager = new PoolChanelObjectManager(pooledConnChannelFactory, poolConnObjectManager);
		poolChanelObjectManager.setBlockWhenExhausted(config.isBlockWhenExhausted());
		poolChanelObjectManager.setMaxChannelCountToConn(config.getMaxChannelCountToConn());
		poolChanelObjectManager.setMaxWaitMillis(config.getMaxWaitMillis());
		return poolChanelObjectManager;
	}
	
	private PoolConnObjectManager createPoolConnObjectManager(PooledConnChannelFactory pooledConnChannelFactory){
		PoolConnObjectManager connObjectManager = new PoolConnObjectManager(pooledConnChannelFactory);
		connObjectManager.setMaxConnTotal(config.getMaxConnTotal());
		return connObjectManager;
	}
	
	private PooledConnChannelFactory createPooledConnChannelFactory(BrokerConnectionFactory brokerConnectionFactory){
		PooledConnChannelFactory factory = new PooledConnChannelFactory(brokerConnectionFactory);
		return factory;
	}
	 
	private BrokerConnectionFactory createBrokerConnectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(config.getUserName());
		connectionFactory.setPassword(config.getPasssWord());
		connectionFactory.setVirtualHost(config.getVirtualHost());
	    BrokerConnectionFactory brokerFactory = new BrokerConnectionFactory(connectionFactory, config.getServerPortAddress());
	    return brokerFactory;
	}
	
	
}
