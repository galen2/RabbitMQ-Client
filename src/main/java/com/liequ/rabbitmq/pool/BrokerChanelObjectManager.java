package com.liequ.rabbitmq.pool;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.NoSuchElementException;
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


public class BrokerChanelObjectManager  extends BaseObjectManager<BrokerChannel> implements ObjectManagerMXBean{

    private final ConnChannelFactory factory;
    
    private final LinkedBlockingDeque<ChannelObject> idleChannelObjects;
    
    private final ConnectionObjectManager poolConnObjectManager;
    
    private  long maxWaitMillis ;
    
    private  int maxChannelCountPerConn ;
    
    private  boolean blockWhenExhausted ;
    
    private final AtomicLong createChannelCount = new AtomicLong(0);


    private final Map<IdentityWrapper<BrokerChannel>, ChannelObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<BrokerChannel>, ChannelObject>();
    
    private static MBeanServer MBEAN_SERVER = null;

    static {
        try {
            MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
        } catch (Exception ex) {
        }
    }
   
    private ObjectName jmxName;
    
	public BrokerChanelObjectManager(ConnChannelFactory pooledConnectionFactory,ConnectionObjectManager poolConnObjectManager,ObjectName dataSource){
		this.factory = pooledConnectionFactory;
		idleChannelObjects = new LinkedBlockingDeque<ChannelObject>();
		this.poolConnObjectManager = poolConnObjectManager;
		try {
			jmxName = new ObjectName(dataSource.toString()+",objectManager=channelManager");
			MBEAN_SERVER.registerMBean(this, jmxName);
		} catch (MalformedObjectNameException |InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
			e.printStackTrace();
		} 
	}
	
	public ObjectName getJmaxName() {
		return jmxName;
	}
	
	
	public BrokerChannel borrowObject() throws Exception{
		return borrowObject(getMaxWaitMillis());
	}
	
	/**
	 * 创建连接池的channel
	 * @param pooledConnObject
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public BrokerChannel borrowObject(long borrowMaxWaitMillis) throws Exception{
		ChannelObject pchan = null;
		while (pchan == null) {
			pchan = idleChannelObjects.pollFirst();
			if (pchan == null ){
				pchan = create();
				
				if (pchan == null) {
			        boolean blockWhenExhausted = getBlockWhenExhausted();
					if(blockWhenExhausted){
						if(borrowMaxWaitMillis < 0) {
							pchan = idleChannelObjects.take();
						} else {
							pchan = idleChannelObjects.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
						}
						if (pchan == null) {
							throw new NoSuchElementException("Timeout waiting for idle object");
						}
					} else {
						throw new NoSuchElementException(" channel pool exhausted");
					}
				}
			}
			
			if (pchan != null) {
				boolean validate = factory.validateChannelObject(pchan);
				if (!validate) {
					destory(pchan);
					
					pchan = null;
				}
			}
		}
		
		return pchan.getPoolableChannel();
	}
	
	private void destory(ChannelObject pchan){
		ConnectionObject cnnObj = pchan.getPooledConnObject();
		cnnObj.getChannelCount().incrementAndGet();
		allObjects.remove(new IdentityWrapper<BrokerChannel>(pchan.getPoolableChannel()));
	}
	
	
	private ChannelObject create() throws Exception{
		ConnectionObject pooledConnObject = borrowConnValidObject();
		if (pooledConnObject == null) {
			return null;
		}
		AtomicLong chanelCount = pooledConnObject.getChannelCount();
		chanelCount.incrementAndGet();
		final ChannelObject p;
		try {
			BrokerConnection pooledConn = pooledConnObject.get_poolableConn();
			BrokerChannel channel = factory.makeObject(this,pooledConn);
			p = new ChannelObject(channel, pooledConnObject);
//			BrokerChannel channel = p.getPoolableChannel();
			createChannelCount.incrementAndGet();
			allObjects.put(new IdentityWrapper<BrokerChannel>(channel), p);
		} catch (Exception e) {
			chanelCount.decrementAndGet();
			throw e;
		} finally {
			if (idleChannel2Conn(pooledConnObject)) {
				poolConnObjectManager.returnObject(pooledConnObject.get_poolableConn());
			}
		}
		return p;
	}
	
	private ConnectionObject borrowConnValidObject() throws Exception{
		ConnectionObject pooledConnObject = null;
		for (;;) {
			pooledConnObject = poolConnObjectManager.borrowConnObject();
			if (pooledConnObject == null){
				return null;
			}
			if (idleChannel2Conn(pooledConnObject)) {
				break;
			}
		}
		return pooledConnObject;
	}
	
	private boolean idleChannel2Conn (ConnectionObject pooledConnObject){
		AtomicLong count = pooledConnObject.getChannelCount();
		int maxChannelCountToConn = getMaxChannelCountToConn();
		return count.get() < maxChannelCountToConn;
	}
	
	public void addObject() throws Exception{
		ChannelObject p = create();
		idleChannelObjects.addFirst(p);
	}
	
	
	protected void returnObject(BrokerChannel obj){
		ChannelObject p = allObjects.get(new IdentityWrapper<BrokerChannel>(obj));
		idleChannelObjects.addLast(p);
	}
	

	public boolean getBlockWhenExhausted() {
		return blockWhenExhausted;
	}

	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}

	public int getMaxChannelCountToConn() {
		return maxChannelCountPerConn;
	}

	public void setMaxChannelCountToConn(int maxChannelCountPerConn) {
		this.maxChannelCountPerConn = maxChannelCountPerConn;
	}
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	@Override
	public long getObjectSum() {
		return createChannelCount.get();
	}
	
}
