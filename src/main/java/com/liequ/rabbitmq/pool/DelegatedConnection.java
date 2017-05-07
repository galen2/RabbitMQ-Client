package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
@SuppressWarnings("unchecked")
public class DelegatedConnection<C extends Connection> implements Connection{
	private volatile C _conn = null;

	public DelegatedConnection(C conn){
		this._conn = conn;
	}
	
	protected Connection getDelegateInternal() {
		return _conn;
	}
	
	public BrokerChannel createChannel() throws IOException {
		Channel cn = getDelegateInternal().createChannel();
		BrokerChannel poolChannel = new BrokerChannel(cn, (DelegatedConnection<Connection>) this);
		return poolChannel;
	}
	 
	 public Channel createChannel(int channelNumber) throws IOException {
		Channel cn = _conn.createChannel(channelNumber);
		BrokerChannel poolChannel = new BrokerChannel(cn, (DelegatedConnection<Connection>) this);
		return poolChannel;
	}
	 
	
    protected final void setDelegateInternal(C conn) {
    	this._conn = conn;
    }
	
	public void addShutdownListener(ShutdownListener listener) {
		_conn.addShutdownListener(listener);
		
	}

	public void removeShutdownListener(ShutdownListener listener) {
		_conn.removeShutdownListener(listener);
	}

	public ShutdownSignalException getCloseReason() {
		return _conn.getCloseReason();
	}

	public void notifyListeners() {
		_conn.notifyListeners();
	}

	public boolean isOpen() {
		return _conn.isOpen();
	}

	public InetAddress getAddress() {
		return _conn.getAddress();
	}

	public int getPort() {
		return _conn.getPort();
	}

	public int getChannelMax() {
		return _conn.getChannelMax();
	}

	public int getFrameMax() {
		return _conn.getFrameMax();
	}

	public int getHeartbeat() {
		return _conn.getHeartbeat();
	}

	public Map<String, Object> getClientProperties() {
		return _conn.getClientProperties();
	}

	public Map<String, Object> getServerProperties() {
		return _conn.getServerProperties();
	}
	
	public void close() throws IOException {
		_conn.close();
	}

	public void close(int closeCode, String closeMessage) throws IOException {
		_conn.close(closeCode, closeMessage);
	}

	public void close(int timeout) throws IOException {
		_conn.close(timeout);
	}

	public void close(int closeCode, String closeMessage, int timeout)
			throws IOException {
		_conn.close(closeCode, closeMessage, timeout);
	}

	public void abort() {
		_conn.abort();
	}

	public void abort(int closeCode, String closeMessage) {
		_conn.abort(closeCode, closeMessage);
	}

	public void abort(int timeout) {
		_conn.abort(timeout);
	}

	public void abort(int closeCode, String closeMessage, int timeout) {
		_conn.abort(closeCode, closeMessage, timeout);
	}

	public void addBlockedListener(BlockedListener listener) {
		_conn.addBlockedListener(listener);
	}

	public boolean removeBlockedListener(BlockedListener listener) {
		return _conn.removeBlockedListener(listener);
	}

	public void clearBlockedListeners() {
		_conn.clearBlockedListeners();
	}

	public ExceptionHandler getExceptionHandler() {
		return _conn.getExceptionHandler();
	}
	

}
