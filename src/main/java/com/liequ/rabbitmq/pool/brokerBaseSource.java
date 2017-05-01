package com.liequ.rabbitmq.pool;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.IllegalClassException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;


public class brokerBaseSource {
	
    private final BrokerChanelObjectManager _channelObjectManager;


    public brokerBaseSource(BrokerChanelObjectManager channelObjectManager){
    	this._channelObjectManager = channelObjectManager;
    }
	
	public Channel getChannel() throws Exception{
		try {
			brokerChannel channel = _channelObjectManager.borrowObject();
			return new PoolGrardChannelWrapper(channel);
		} catch (RuntimeException e) {
			throw e;
		}
	}
	@SuppressWarnings("unchecked")
	public Connection getConnection(Channel channel) throws Exception{
		try {
			if (channel instanceof DelegatedChannel){
				throw new IllegalArgumentException("Unrecoginise channel");
			}
			DelegatedChannel<Channel> delegateChannel = (DelegatedChannel<Channel>)channel;
			DelegatedConnection<?> conn = delegateChannel.getDelegateConnectionInterval();
			return new PoolGrardConnWrapper(conn);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 屏蔽一些禁止操作方法
	 *
	 */
	private class PoolGrardConnWrapper extends DelegatedConnection<Connection>{

		public PoolGrardConnWrapper(Connection conn) {
			super(conn);
		}

		@Override
		public brokerChannel createChannel() throws IOException {
			throw new BrokerException(" forbid crateChannel");
		}

		@Override
		public Channel createChannel(int channelNumber) throws IOException {
			throw new BrokerException(" forbid crateChannel");

		}
	}
	
	/**
	 * 随后扩展屏蔽一些方法
	 *
	 */
	private class PoolGrardChannelWrapper extends DelegatedChannel<Channel>{
		
		public PoolGrardChannelWrapper(brokerChannel channel){
			super(channel,channel.getDelegateConnectionInterval());
		}
	/*	//做对象包装，可依据配置来确定是否允许访问底层的代理对象
		public DelegatedConnection<Connection> getDelegateConnection(){
			if (isAccessToUnderlyingConnectionAllowed()){
				return super.getDelegateConnection();
			}
			return null;
		}*/
		 @Override
        public void close() throws  IOException, TimeoutException {
            if (getDelegateInternal() != null) {
                super.close();
                super.setDelegate(null);
            }
        }
	}
}
