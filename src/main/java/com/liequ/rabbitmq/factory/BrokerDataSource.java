package com.liequ.rabbitmq.factory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BrokerDataSource {
	
	private volatile BrokerBaseSource dataSource = null;
	private BrokerConfig config = null;
	private final String brokerName;
	public BrokerDataSource(String brokerName){
		this.brokerName = brokerName;
	}

	private BrokerBaseSource createDataSource() throws Exception {
		
		if (dataSource != null) {
			return dataSource;
		}

		synchronized (this) {
			if (dataSource != null) {
				return dataSource;
			}
			
			createJmx();

			config = BrokerConfigManager.getInstance().getBrokerConfig(brokerName);
			
			BrokerConnectionFactory brokerConnectionFactory = createBrokerConnectionFactory();
			ConnChannelFactory pooledConnChannelFactory = createPooledConnChannelFactory(brokerConnectionFactory);

			ConnectionObjectManager poolConnObjectManager = createPoolConnObjectManager(pooledConnChannelFactory);
			BrokerChanelObjectManager poolChanelObjectManager = createPoolChanelObjectManager(
					pooledConnChannelFactory, poolConnObjectManager);

			BrokerBaseSource newDataSource = new BrokerBaseSource(
					poolChanelObjectManager);

			try {
				int initialSize = config.getInitialSize();
				for (int i = 0; i < initialSize; i++) {
					poolConnObjectManager.addObject();
					poolChanelObjectManager.addObject();
				}
			} catch (Exception e) {
				throw new BrokerException("Error preloading poolConnObject",
						e);
			}
			dataSource = newDataSource;
		}
		return dataSource;
	}

	private BrokerChanelObjectManager createPoolChanelObjectManager(
			ConnChannelFactory pooledConnChannelFactory,
			ConnectionObjectManager poolConnObjectManager) {
		BrokerChanelObjectManager poolChanelObjectManager = new BrokerChanelObjectManager(
				pooledConnChannelFactory, poolConnObjectManager,registeredJmxName);
		poolChanelObjectManager.setBlockWhenExhausted(config
				.isBlockWhenExhausted());
		poolChanelObjectManager.setMaxChannelCountToConn(config
				.getMaxChannelCountPerConn());
		poolChanelObjectManager.setMaxWaitMillis(config.getMaxWaitMillis());
		return poolChanelObjectManager;
	}

	private ConnectionObjectManager createPoolConnObjectManager(
			ConnChannelFactory pooledConnChannelFactory) {
		ConnectionObjectManager connObjectManager = new ConnectionObjectManager(
				pooledConnChannelFactory,registeredJmxName);
		connObjectManager.setMaxConnTotal(config.getMaxConnTotal());
		connObjectManager.setMaxWaitMillis(config.getMaxWaitMillis());
		return connObjectManager;
	}
    private ObjectName registeredJmxName = null;

    private void createJmx() {
        try {
        	registeredJmxName = new ObjectName("RabbitMQ_Client_"+brokerName+":name=BrokerDataSource");
        } catch (MalformedObjectNameException e) {
            return;
        }
    }
    
	private ConnChannelFactory createPooledConnChannelFactory(
			BrokerConnectionFactory brokerConnectionFactory) {
		ConnChannelFactory factory = new ConnChannelFactory(
				brokerConnectionFactory);
		return factory;
	}

	private BrokerConnectionFactory createBrokerConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(config.getUserName());
		connectionFactory.setPassword(config.getPasssWord());
		connectionFactory.setVirtualHost(config.getVirtualHost());
		BrokerConnectionFactory brokerFactory = new BrokerConnectionFactory(
				connectionFactory, config.getServerPortAddress());
		return brokerFactory;
	}

	public Channel createChannel() throws Exception {
		return createDataSource().getChannel();
	}
	public Connection getConnection(Channel channel) throws Exception{
		
		return createDataSource().getConnection(channel);
	}
}
