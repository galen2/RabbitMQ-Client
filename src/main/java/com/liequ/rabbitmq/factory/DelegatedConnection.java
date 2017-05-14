package com.liequ.rabbitmq.factory;

import java.io.IOException;

import java.net.InetAddress;
import java.util.Map;

import com.liequ.rabbitmq.exception.ConnException;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
@SuppressWarnings("unchecked")
public class DelegatedConnection<C extends Connection> implements Connection{
	private volatile C _conn = null;

    private volatile boolean _closed = false;

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
	 
	 protected boolean isClosedInternal() {
	      return _closed;
	 }

    protected void setClosedInternal(boolean closed) {
        this._closed = closed;
    }
    
    protected final void closeInternal() throws IOException {
    	if (_conn != null) {
    		try {
				_conn.close();
			} finally {
				_closed = true;
			}
    	} else {
    		_closed = true;
    	}
    }
    
    public void close() throws IOException {
    	if (_conn != null) {
    		_conn.close();
    	} 
	}

	public void close(int closeCode, String closeMessage) throws IOException {
		close();
	}

	public void close(int timeout) throws IOException {
		close();
	}

	public void close(int closeCode, String closeMessage, int timeout)
			throws IOException {
		close();
	}

	/**
	 * Temporarily not supported
	 */
	public void abort() {
	}
	/**
	 * Temporarily not supported
	 */
	public void abort(int closeCode, String closeMessage) {
	}
	/**
	 * Temporarily not supported
	 */
	public void abort(int timeout) {
	}
	/**
	 * Temporarily not supported
	 */
	public void abort(int closeCode, String closeMessage, int timeout) {
	}

	    
    protected final void setDelegateInternal(C conn) {
    	this._conn = conn;
    }
	
	public void addShutdownListener(ShutdownListener listener) {
		checkOpen();
		_conn.addShutdownListener(listener);
	}

	public void removeShutdownListener(ShutdownListener listener) {
		checkOpen();
		_conn.removeShutdownListener(listener);
	}

	public ShutdownSignalException getCloseReason() {
		checkOpen();
		return _conn.getCloseReason();
	}

	public void notifyListeners() {
		checkOpen();
		_conn.notifyListeners();
	}

	protected void checkOpen() { 
		if (!isOpen()) {
			throw new ConnException("connection is closed");
		}
	}

	public boolean isOpen() {
		if (_conn != null) {
			return _conn.isOpen();
		} 
		return  false;
	}

	public InetAddress getAddress() {
		checkOpen();
		return _conn.getAddress();
	}

	public int getPort() {
		checkOpen();
		return _conn.getPort();
	}

	public int getChannelMax() {
		checkOpen();
		return _conn.getChannelMax();
	}

	public int getFrameMax() {
		checkOpen();
		return _conn.getFrameMax();
	}

	public int getHeartbeat() {
		checkOpen();
		return _conn.getHeartbeat();
	}

	public Map<String, Object> getClientProperties() {
		checkOpen();
		return _conn.getClientProperties();
	}

	public Map<String, Object> getServerProperties() {
		checkOpen();
		return _conn.getServerProperties();
	}
	
	public void addBlockedListener(BlockedListener listener) {
		checkOpen();
		_conn.addBlockedListener(listener);
	}

	public boolean removeBlockedListener(BlockedListener listener) {
		checkOpen();
		return _conn.removeBlockedListener(listener);
	}

	public void clearBlockedListeners() {
		checkOpen();
		_conn.clearBlockedListeners();
	}

	public ExceptionHandler getExceptionHandler() {
		checkOpen();
		return _conn.getExceptionHandler();
	}
	

}
