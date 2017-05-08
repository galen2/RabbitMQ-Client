package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.liequ.rabbitmq.exception.ConnException;
import com.rabbitmq.client.AMQP.Basic.RecoverOk;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Exchange.BindOk;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;
import com.rabbitmq.client.AMQP.Exchange.UnbindOk;
import com.rabbitmq.client.AMQP.Queue.PurgeOk;
import com.rabbitmq.client.AMQP.Tx.CommitOk;
import com.rabbitmq.client.AMQP.Tx.RollbackOk;
import com.rabbitmq.client.AMQP.Tx.SelectOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Command;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.FlowListener;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class DelegatedChannel<C extends Channel> implements Channel{
	private volatile C _channel;
	private DelegatedConnection<Connection> _conn ;
    private volatile boolean _closed = false;

	public DelegatedChannel(C channel,DelegatedConnection<Connection> conn){
		this._channel = channel;
		this._conn = conn;
	}
	
	protected Channel getInternalChannel(){
		return _channel;
	}

	protected final C getDelegateInternal(){
		return _channel;
	}
	
	public void setDelegate(C c) {
	  _channel = c;
    }

	protected DelegatedConnection<Connection> getDelegateConnection(){
		return _conn;
	}
	
	/**
	 * open to client
	 */
	public Connection getConnection() {
		return getDelegateConnection();
	}

	  protected void setClosedInternal(boolean closed) {
	      this._closed = closed;
	  }
  
	protected boolean isClosedInternal() {
        return _closed;
    }
	public void close() throws IOException, TimeoutException {
		if (!_closed) {
			closeInternal();
		}
	}
	
	public void close(int closeCode, String closeMessage) throws IOException,
			TimeoutException {
		if (!_closed) { 
			closeInternal();
		}
	}
	 protected final void closeInternal() throws TimeoutException, IOException {
    	if (_channel != null) {
    		try {
    			_channel.close();
			} finally {
				_closed = true;
			}
    	} else {
    		_closed = true;
    	}
    }
 
	
	/**
	 * Temporarily not supported
	 */
	public void abort() throws IOException {
	}
	/**
	 * Temporarily not supported
	 */
	public void abort(int closeCode, String closeMessage) throws IOException {
	}
	
	protected void checkOpen() { 
		if (_closed) {
			throw new ConnException("connection is closed");
		}
	}
	  
	
	public void addShutdownListener(ShutdownListener listener) {
		checkOpen();
		_channel.addShutdownListener(listener);
	}

	public void removeShutdownListener(ShutdownListener listener) {
		checkOpen();
		_channel.removeShutdownListener(listener);
		
	}

	public ShutdownSignalException getCloseReason() {
		checkOpen();
		return _channel.getCloseReason();
	}

	public void notifyListeners() {
		checkOpen();
		_channel.notifyListeners();
	}

	public boolean isOpen() {
		return _channel.isOpen();
	}

	public int getChannelNumber() {
		checkOpen();
		return _channel.getChannelNumber();
	}

	

	@SuppressWarnings("deprecation")
	public boolean flowBlocked() {
		return true;
	}


	public void addReturnListener(ReturnListener listener) {
		checkOpen();
		_channel.addReturnListener(listener);
	}

	public boolean removeReturnListener(ReturnListener listener) {
		checkOpen();
		return _channel.removeReturnListener(listener);
	}

	public void clearReturnListeners() {
		checkOpen();
		_channel.clearReturnListeners();
	}
	
	@Deprecated
	public void addFlowListener(FlowListener listener) {
	}
	
	@Deprecated
	public boolean removeFlowListener(FlowListener listener) {
		return false;
	}

	@Deprecated
	public void clearFlowListeners() {
	}

	public void addConfirmListener(ConfirmListener listener) {
		checkOpen();
		_channel.addConfirmListener(listener);
	}

	public boolean removeConfirmListener(ConfirmListener listener) {
		checkOpen();
		return _channel.removeConfirmListener(listener);
	}

	public void clearConfirmListeners() {
		checkOpen();
		_channel.clearConfirmListeners();
	}

	public Consumer getDefaultConsumer() {
		checkOpen();
		return _channel.getDefaultConsumer();
	}

	public void setDefaultConsumer(Consumer consumer) {
		checkOpen();
		_channel.setDefaultConsumer(consumer);
	}

	public void basicQos(int prefetchSize, int prefetchCount, boolean global)
			throws IOException {
		checkOpen();
		_channel.basicQos(prefetchSize, prefetchCount, global);
		
	}

	public void basicQos(int prefetchCount, boolean global) throws IOException {
		checkOpen();
		_channel.basicQos( prefetchCount,  global);
	}

	public void basicQos(int prefetchCount) throws IOException {
		checkOpen();
		_channel.basicQos(prefetchCount);
	}

	public void basicPublish(String exchange, String routingKey,
			BasicProperties props, byte[] body) throws IOException {
		checkOpen();
		_channel.basicPublish(exchange, routingKey, props, body);
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, BasicProperties props, byte[] body)
			throws IOException {
		checkOpen();
		_channel.basicPublish(exchange, routingKey, mandatory, props, body);
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, boolean immediate, BasicProperties props,
			byte[] body) throws IOException {
		checkOpen();
		_channel.basicPublish(exchange, routingKey, mandatory, immediate, props, body);
	}

	public DeclareOk exchangeDeclare(String exchange, String type)
			throws IOException {
		checkOpen();
		return _channel.exchangeDeclare(exchange, type);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable) throws IOException {
		checkOpen();
		return _channel.exchangeDeclare(exchange, type, durable);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		return _channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		checkOpen();
		return _channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments);
	}

	public void exchangeDeclareNoWait(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		checkOpen();
		_channel.exchangeDeclareNoWait(exchange, type, durable, autoDelete, internal, arguments);
	}

	public DeclareOk exchangeDeclarePassive(String name) throws IOException {
		checkOpen();
		return _channel.exchangeDeclarePassive(name);
	}

	public DeleteOk exchangeDelete(String exchange, boolean ifUnused)
			throws IOException {
		checkOpen();
		return _channel.exchangeDelete(exchange);
	}

	public void exchangeDeleteNoWait(String exchange, boolean ifUnused)
			throws IOException {
		checkOpen();
		_channel.exchangeDeleteNoWait(exchange, ifUnused);
	}

	public DeleteOk exchangeDelete(String exchange) throws IOException {
		checkOpen();
		return _channel.exchangeDelete(exchange);
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey) throws IOException {
		checkOpen();
		return _channel.exchangeBind(destination, source, routingKey);
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		return _channel.exchangeBind(destination, source, routingKey, arguments);
	}

	public void exchangeBindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		_channel.exchangeBindNoWait(destination, source, routingKey, arguments);
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey) throws IOException {
		checkOpen();
		return _channel.exchangeUnbind(destination, source, routingKey);
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		return _channel.exchangeUnbind(destination, source, routingKey);
	}

	public void exchangeUnbindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		_channel.exchangeUnbindNoWait(destination, source, routingKey, arguments);
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare()
			throws IOException {
		checkOpen();
		return _channel.queueDeclare();
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare(String queue,
			boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> arguments) throws IOException {
		checkOpen();
		return _channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
	}

	public void queueDeclareNoWait(String queue, boolean durable,
			boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		_channel.queueDeclareNoWait(queue, durable, exclusive, autoDelete, arguments);
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclarePassive(
			String queue) throws IOException {
		checkOpen();
		return _channel.queueDeclarePassive(queue);
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue)
			throws IOException {
		checkOpen();
		return _channel.queueDelete(queue);
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue,
			boolean ifUnused, boolean ifEmpty) throws IOException {
		checkOpen();
		return _channel.queueDelete(queue, ifUnused, ifEmpty);
	}

	public void queueDeleteNoWait(String queue, boolean ifUnused,
			boolean ifEmpty) throws IOException {
		checkOpen();
		_channel.queueDeleteNoWait(queue, ifUnused, ifEmpty);
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey) throws IOException {
		checkOpen();
		return _channel.queueBind(queue, exchange, routingKey);
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		return _channel.queueBind(queue, exchange, routingKey, arguments);
	}

	public void queueBindNoWait(String queue, String exchange,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		_channel.queueBindNoWait(queue, exchange, routingKey, arguments);
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey) throws IOException {
		checkOpen();
		return _channel.queueUnbind(queue, exchange, routingKey);
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		checkOpen();
		return _channel.queueUnbind(queue, exchange, routingKey, arguments);
	}

	public PurgeOk queuePurge(String queue) throws IOException {
		checkOpen();
		return _channel.queuePurge(queue);
	}

	public GetResponse basicGet(String queue, boolean autoAck)
			throws IOException {
		checkOpen();
		return _channel.basicGet(queue, autoAck);
	}

	public void basicAck(long deliveryTag, boolean multiple) throws IOException {
		checkOpen();
		_channel.basicAck(deliveryTag, multiple);
	}

	public void basicNack(long deliveryTag, boolean multiple, boolean requeue)
			throws IOException {
		checkOpen();
		_channel.basicNack(deliveryTag, multiple, requeue);
	}

	public void basicReject(long deliveryTag, boolean requeue)
			throws IOException {
		checkOpen();
		_channel.basicReject(deliveryTag, requeue);
	}

	public String basicConsume(String queue, Consumer callback)
			throws IOException {
		checkOpen();
		return _channel.basicConsume(queue, callback);
	}

	public String basicConsume(String queue, boolean autoAck, Consumer callback)
			throws IOException {
		checkOpen();
		return _channel.basicConsume(queue, autoAck, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		checkOpen();
		return _channel.basicConsume(queue, autoAck, arguments, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, Consumer callback) throws IOException {
		checkOpen();
		return _channel.basicConsume(queue, autoAck,consumerTag, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, boolean noLocal, boolean exclusive,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		checkOpen();
		return _channel.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments, callback);
	}

	public void basicCancel(String consumerTag) throws IOException {
		checkOpen();
		_channel.basicCancel(consumerTag);
	}

	public RecoverOk basicRecover() throws IOException {
		checkOpen();
		return _channel.basicRecover();
	}

	public RecoverOk basicRecover(boolean requeue) throws IOException {
		checkOpen();
		return _channel.basicRecover(requeue);
	}

	public SelectOk txSelect() throws IOException {
		checkOpen();
		return _channel.txSelect();
	}

	public CommitOk txCommit() throws IOException {
		checkOpen();
		return _channel.txCommit();
	}

	public RollbackOk txRollback() throws IOException {
		checkOpen();
		return _channel.txRollback();
	}

	public com.rabbitmq.client.AMQP.Confirm.SelectOk confirmSelect()
			throws IOException {
		checkOpen();
		return _channel.confirmSelect();
	}

	public long getNextPublishSeqNo() {
		checkOpen();
		return _channel.getNextPublishSeqNo();
	}

	public boolean waitForConfirms() throws InterruptedException {
		checkOpen();
		return _channel.waitForConfirms();
	}

	public boolean waitForConfirms(long timeout) throws InterruptedException,
			TimeoutException {
		checkOpen();
		return _channel.waitForConfirms(timeout);
	}

	public void waitForConfirmsOrDie() throws IOException, InterruptedException {
		checkOpen();
		_channel.waitForConfirmsOrDie();
	}

	public void waitForConfirmsOrDie(long timeout) throws IOException,
			InterruptedException, TimeoutException {
		checkOpen();
		_channel.waitForConfirmsOrDie(timeout);
	}

	public void asyncRpc(Method method) throws IOException {
		checkOpen();
		_channel.asyncRpc(method);
	}

	public Command rpc(Method method) throws IOException {
		checkOpen();
		return _channel.rpc(method);
	}

	public long messageCount(String queue) throws IOException {
		checkOpen();
		return _channel.messageCount(queue);
	}

	public long consumerCount(String queue) throws IOException {
		checkOpen();
		return _channel.consumerCount(queue);
	}

}
