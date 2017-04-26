package com.pool.datasources;

import com.pool.PoolConfig;
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
			createPooledConnChannelFactory(brokerConnectionFactory);
			
		}
		
		return null;
		
	}
	
	private void createPoolConfig(){
		config = new PoolConfig();
		
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
	
	private ConnectionFactory crateConnectionFactory(){
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setUsername(config.getUserName());
	    factory.setPassword(config.getPasssWord());
	    factory.setVirtualHost(config.getVirtualHost());
	    return factory;
	    
	}
	
}
