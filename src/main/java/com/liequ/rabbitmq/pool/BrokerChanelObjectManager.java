package com.liequ.rabbitmq.pool;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class BrokerChanelObjectManager  extends BaseObjectManager<brokerChannel>{
	
    private final ConnChannelFactory factory;
    
    private final LinkedBlockingDeque<ChannelObject> idleChannelObjects;
    
    private final ConnectionObjectManager poolConnObjectManager;
    
    private  long maxWaitMillis ;
    
    private  int maxChannelCountPerConn ;
    
    private  boolean blockWhenExhausted ;

    private final Map<IdentityWrapper<brokerChannel>, ChannelObject> allObjects =
            new ConcurrentHashMap<IdentityWrapper<brokerChannel>, ChannelObject>();
    
    
	public BrokerChanelObjectManager(ConnChannelFactory pooledConnectionFactory,ConnectionObjectManager poolConnObjectManager){
		this.factory = pooledConnectionFactory;
		idleChannelObjects = new LinkedBlockingDeque<ChannelObject>();
		this.poolConnObjectManager = poolConnObjectManager;
	}
	
	
	public brokerChannel borrowObject() throws Exception{
		return borrowObject(getMaxWaitMillis());
	}
	
	/**
	 * 创建连接池的channel
	 * @param pooledConnObject
	 * @param borrowMaxWaitMillis
	 * @return
	 * @throws Exception
	 */
	public brokerChannel borrowObject(long borrowMaxWaitMillis) throws Exception{
		ChannelObject pchan = null;
		while (pchan == null) {
			pchan = idleChannelObjects.pollFirst();
			if(pchan == null ){
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
		}
		return pchan.get_poolableChannel();
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
			p = factory.makeObject(this,pooledConn);
			brokerChannel channel = p.get_poolableChannel();
			allObjects.put(new IdentityWrapper<brokerChannel>(channel), p);
		} catch (Exception e) {
			chanelCount.decrementAndGet();
			throw e;
		}
		return p;
	}
	
	private ConnectionObject borrowConnValidObject() throws Exception{
		ConnectionObject pooledConnObject = null;
		for (;;) {
			pooledConnObject = poolConnObjectManager.borrowConnObject();
			if(pooledConnObject == null){
				return null;
			}
			AtomicLong count = pooledConnObject.getChannelCount();
			int maxChannelCountToConn = getMaxChannelCountToConn();
			if (count.get() < maxChannelCountToConn) {
				poolConnObjectManager.returnObject(pooledConnObject.get_poolableConn());
				break;
			}
		}
		return pooledConnObject;
	}
	
	public void addObject() throws Exception{
		ChannelObject p = create();
		idleChannelObjects.addFirst(p);
	}
	
	
	protected void returnObject(brokerChannel obj){
		ChannelObject p = allObjects.get(new IdentityWrapper<brokerChannel>(obj));
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
	
	
	
}
