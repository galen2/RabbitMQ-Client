package com.pool.datasources;


import com.DelegatingMQChannel;
import com.pool.PoolableChannel;
import com.rabbitmq.client.Channel;


public class MQPoolingDataSource {
    private final PoolConnObjectManager _objectManger;
    private final PoolChanelObjectManager _channelObjectManager;


    public MQPoolingDataSource(PoolConnObjectManager objectManager,PoolChanelObjectManager channelObjectManager){
    	this._objectManger = objectManager;
    	this._channelObjectManager = channelObjectManager;
    }
    /*
	public Connection getConnection() throws Exception{
		try {
			MQPooledConnObject conn = _objectManger.borrowObject();
			if(conn == null){
				return null;
			}
			return new PoolGrardConnectionWrapper(conn.get_poolableConn());
		} catch (RuntimeException e) {
			throw e;
		}
	}*/
	
	
	
	
	
	/**
	 * 为了某些方法安全考虑，可以重写某些方法，
	 *
	 */
	/*private class PoolGrardConnectionWrapper extends DelegatingMQConnection{
		public PoolGrardConnectionWrapper(PoolableConnection conn){
			super(conn);
		}
		 @Override
        public void close() throws SQLException {
            if (getDelegateInternal() != null) {
                super.close();
                super.setDelegateInternal(null);
            }
        }
	}*/
	
	
	public Channel getChannel() throws Exception{
		try {
			PoolableChannel channel = _channelObjectManager.borrowObject();
			if(channel == null){
				return null;
			}
			return new PoolGrardChannelWrapper(channel);
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	
	private class PoolGrardChannelWrapper extends DelegatingMQChannel{
		public PoolGrardChannelWrapper(PoolableChannel channel){
			super(channel,channel.getDelegatingMQConnection());
		}
		/* @Override
        public void close() throws SQLException {
            if (getDelegateInternal() != null) {
                super.close();
                super.setDelegateInternal(null);
            }
        }*/
	}
}
