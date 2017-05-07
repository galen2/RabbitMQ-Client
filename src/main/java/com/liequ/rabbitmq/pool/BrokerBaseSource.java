package com.liequ.rabbitmq.pool;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;


public class BrokerBaseSource {
	
    private final BrokerChanelObjectManager _channelObjectManager;

    public BrokerBaseSource(BrokerChanelObjectManager channelObjectManager){
    	this._channelObjectManager = channelObjectManager;
    }
	
	public Channel getChannel() throws Exception{
		try {
			BrokerChannel channel = _channelObjectManager.borrowObject();
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
			DelegatedConnection<?> conn = delegateChannel.getDelegateConnection();
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
		public BrokerChannel createChannel() throws IOException {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public Channel createChannel(int channelNumber) throws IOException {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void close() throws IOException {
			throw new BrokerException(" forbid operate");

		}

		@Override
		public void close(int closeCode, String closeMessage)
				throws IOException {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void close(int timeout) throws IOException {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void close(int closeCode, String closeMessage, int timeout)
				throws IOException {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void abort() {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void abort(int closeCode, String closeMessage) {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void abort(int timeout) {
			throw new BrokerException(" forbid operate");
		}

		@Override
		public void abort(int closeCode, String closeMessage, int timeout) {
			throw new BrokerException(" forbid operate");
		}
		
	}
	
	/**
	 * 随后扩展屏蔽一些方法
	 */
	private class PoolGrardChannelWrapper extends DelegatedChannel<Channel>{
		
		public PoolGrardChannelWrapper(BrokerChannel channel){
			super(channel,channel.getDelegateConnection());
		}
		
		 @Override
        public void close() throws  IOException, TimeoutException {
            if (getDelegateInternal() != null) {
                super.close();
                super.setDelegate(null);
            }
        }
		  @Override
		 public void close(int closeCode, String closeMessage)  throws IOException,TimeoutException{
			  close();
		 }

		@Override
		public void abort() throws IOException {
			if (getDelegateInternal() != null) {
                super.abort();
                super.setDelegate(null);
            }
		}

		@Override
		public void abort(int closeCode, String closeMessage)
				throws IOException {
			 abort();
		}
		  
	}
}
