package com.pool;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class DelegatingConnection<C extends Connection> implements Connection{
	
	//为了安全封装真实的server连接，防止外部访问
	private volatile C _conn = null;

	public DelegatingConnection(C conn){
		this._conn = conn;
	}
	
	public Connection getDelegate() {
		return getDelegateInternal();
	}
	//为了保护连接，只有子类可以访问这个真实连接，
	protected Connection getDelegateInternal() {
		return _conn;
	}
	
	//此方法不对外开发
	public Channel createChannel() throws IOException {
		throw new RuntimeException("forbid create");
//		return null;
	}
	
	
    protected final void setDelegateInternal(C conn) {
    	this._conn = conn;
    }
	
	public void addShutdownListener(ShutdownListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void removeShutdownListener(ShutdownListener listener) {
		// TODO Auto-generated method stub
		
	}

	public ShutdownSignalException getCloseReason() {
		// TODO Auto-generated method stub
		return null;
	}

	public void notifyListeners() {
		// TODO Auto-generated method stub
		
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public InetAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getChannelMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFrameMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeartbeat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map<String, Object> getClientProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getServerProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	

	public Channel createChannel(int channelNumber) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void close(int closeCode, String closeMessage) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void close(int timeout) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void close(int closeCode, String closeMessage, int timeout)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void abort() {
		// TODO Auto-generated method stub
		
	}

	public void abort(int closeCode, String closeMessage) {
		// TODO Auto-generated method stub
		
	}

	public void abort(int timeout) {
		// TODO Auto-generated method stub
		
	}

	public void abort(int closeCode, String closeMessage, int timeout) {
		// TODO Auto-generated method stub
		
	}

	public void addBlockedListener(BlockedListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeBlockedListener(BlockedListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearBlockedListeners() {
		// TODO Auto-generated method stub
		
	}

	public ExceptionHandler getExceptionHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
