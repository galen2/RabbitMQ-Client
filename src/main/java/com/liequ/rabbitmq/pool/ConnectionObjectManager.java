package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class ConnectionObjectManager extends BaseObjectManager<BrokerConnection>  implements ObjectManagerMXBean{
    private final ConnChannelFactory factory;
    
    private final LinkedBlockingDeque<ConnectionObject> idleConnObjects;
    
    private final AtomicLong createConnCount = new AtomicLong(0);
    
    private  int maxConnTotal ;

    private  long maxWaitMillis ;

    private final Map<IdentityWrapper<BrokerConnection>, ConnectionObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<BrokerConnection>, ConnectionObject>();
    
    private static MBeanServer MBEAN_SERVER = null;

    static {
        try {
            MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
        } catch (Exception ex) {
        }
    }
    private ObjectName jmxName;
	public ConnectionObjectManager(ConnChannelFactory pooledConnectionFactory,ObjectName dataSource){
		this.factory = pooledConnectionFactory;
		idleConnObjects = new LinkedBlockingDeque<ConnectionObject>();
		try {
			jmxName = new ObjectName(dataSource.toString()+",objectManager=connectionManager");
			MBEAN_SERVER.registerMBean(this, jmxName);
		} catch (MalformedObjectNameException |InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
		} 
	}
	

	protected ConnectionObject borrowConnObject() throws Exception{
		ConnectionObject pcnn = null;
		while (pcnn == null) {
			pcnn = idleConnObjects.pollFirst();
			if (pcnn == null ) {
				pcnn = create();
				if (pcnn == null) {
					pcnn = idleConnObjects.pollFirst(getMaxWaitMillis(), TimeUnit.MILLISECONDS);
				}
				return pcnn;
			}
			
			if (pcnn != null) {
				factory.activateObject(pcnn);
			}
			
			if (pcnn !=null) {
				boolean validate = factory.validateConnObject(pcnn);
				if (!validate) {
					try {
						destroy(pcnn);
					} catch (Exception e) {
					}
					pcnn = null;
				}
			}
		}
		return pcnn;
	}
	
	private void destroy(ConnectionObject pcnn) throws IOException{
		factory.destroyObject(pcnn);
		allObjects.remove(new IdentityWrapper<BrokerConnection>(pcnn.getPoolableConn()));
		createConnCount.decrementAndGet();
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
			createConnCount.decrementAndGet();
			throw e;
		}
		BrokerConnection cnn = p.getPoolableConn();
		allObjects.put(new IdentityWrapper<BrokerConnection>(cnn), p);
		return p;
	}
	
	public void returnObject(BrokerConnection  cnn){
		ConnectionObject conn = allObjects.get(new IdentityWrapper<BrokerConnection>(cnn));
		idleConnObjects.addLast(conn);
		factory.passivateObject(conn);
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
	

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}


	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}


	@Override
	public long getObjectSum() {
		return createConnCount.get();
	}
	
}
