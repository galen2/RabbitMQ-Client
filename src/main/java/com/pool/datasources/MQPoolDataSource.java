package com.pool.datasources;


import com.pool.DelegatingChannel;
import com.pool.PoolableChannel;
import com.rabbitmq.client.Channel;


public class MQPoolDataSource<C> {
	
    private final PoolChanelObjectManager _channelObjectManager;


    public MQPoolDataSource(PoolChanelObjectManager channelObjectManager){
    	this._channelObjectManager = channelObjectManager;
    }
	
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
	
	
	private class PoolGrardChannelWrapper extends DelegatingChannel<Channel>{
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
