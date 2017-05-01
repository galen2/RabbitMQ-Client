package com.liequ.rabbitmq.pool;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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

	protected DelegatedConnection<Connection> getDelegateConnectionInterval(){
		return _conn;
	}

	
   /**
    * 获取底层连接对象：框架使用
    * @return
    */
	protected final Channel getInnermostDelegateInternal() {
	  Channel c = _channel;
        while(c != null && c instanceof DelegatedChannel) {
            c = ((DelegatedChannel<?>)c).getDelegateInternal();
            if(this == c) {
                return null;
            }
        }
        return c;
    }
	
	public void close() throws IOException, TimeoutException {
		_channel.close();
	}

	public void close(int closeCode, String closeMessage) throws IOException,
			TimeoutException {
		_channel.close(closeCode, closeMessage);
	}
	
	
	public void addShutdownListener(ShutdownListener listener) {
		_channel.addShutdownListener(listener);
	}

	public void removeShutdownListener(ShutdownListener listener) {
		_channel.removeShutdownListener(listener);
		
	}

	public ShutdownSignalException getCloseReason() {
		return _channel.getCloseReason();
	}

	public void notifyListeners() {
		_channel.notifyListeners();
	}

	public boolean isOpen() {
		return _channel.isOpen();
	}

	public int getChannelNumber() {
		return _channel.getChannelNumber();
	}

	public Connection getConnection() {
		return _channel.getConnection();
	}

	

	public boolean flowBlocked() {
		return _channel.flowBlocked();
	}

	public void abort() throws IOException {
		_channel.abort();
	}

	public void abort(int closeCode, String closeMessage) throws IOException {
		_channel.abort(closeCode, closeMessage);
	}

	public void addReturnListener(ReturnListener listener) {
		_channel.addReturnListener(listener);
	}

	public boolean removeReturnListener(ReturnListener listener) {
		return _channel.removeReturnListener(listener);
	}

	public void clearReturnListeners() {
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
		_channel.addConfirmListener(listener);
	}

	public boolean removeConfirmListener(ConfirmListener listener) {
		return _channel.removeConfirmListener(listener);
	}

	public void clearConfirmListeners() {
		_channel.clearConfirmListeners();
	}

	public Consumer getDefaultConsumer() {
		return _channel.getDefaultConsumer();
	}

	public void setDefaultConsumer(Consumer consumer) {
		_channel.setDefaultConsumer(consumer);
	}

	public void basicQos(int prefetchSize, int prefetchCount, boolean global)
			throws IOException {
		_channel.basicQos(prefetchSize, prefetchCount, global);
		
	}

	public void basicQos(int prefetchCount, boolean global) throws IOException {
		_channel.basicQos( prefetchCount,  global);
	}

	public void basicQos(int prefetchCount) throws IOException {
		_channel.basicQos(prefetchCount);
	}

	public void basicPublish(String exchange, String routingKey,
			BasicProperties props, byte[] body) throws IOException {
		_channel.basicPublish(exchange, routingKey, props, body);
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, BasicProperties props, byte[] body)
			throws IOException {
		_channel.basicPublish(exchange, routingKey, mandatory, props, body);
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, boolean immediate, BasicProperties props,
			byte[] body) throws IOException {
		_channel.basicPublish(exchange, routingKey, mandatory, immediate, props, body);
	}

	public DeclareOk exchangeDeclare(String exchange, String type)
			throws IOException {
		return _channel.exchangeDeclare(exchange, type);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable) throws IOException {
		return _channel.exchangeDeclare(exchange, type, durable);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		return _channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		return _channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments);
	}

	public void exchangeDeclareNoWait(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		_channel.exchangeDeclareNoWait(exchange, type, durable, autoDelete, internal, arguments);
	}

	public DeclareOk exchangeDeclarePassive(String name) throws IOException {
		return _channel.exchangeDeclarePassive(name);
	}

	public DeleteOk exchangeDelete(String exchange, boolean ifUnused)
			throws IOException {
		return _channel.exchangeDelete(exchange);
	}

	public void exchangeDeleteNoWait(String exchange, boolean ifUnused)
			throws IOException {
		_channel.exchangeDeleteNoWait(exchange, ifUnused);
	}

	public DeleteOk exchangeDelete(String exchange) throws IOException {
		return _channel.exchangeDelete(exchange);
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey) throws IOException {
		return _channel.exchangeBind(destination, source, routingKey);
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		return _channel.exchangeBind(destination, source, routingKey, arguments);
	}

	public void exchangeBindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		_channel.exchangeBindNoWait(destination, source, routingKey, arguments);
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey) throws IOException {
		return _channel.exchangeUnbind(destination, source, routingKey);
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		return _channel.exchangeUnbind(destination, source, routingKey);
	}

	public void exchangeUnbindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		_channel.exchangeUnbindNoWait(destination, source, routingKey, arguments);
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare()
			throws IOException {
		return _channel.queueDeclare();
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare(String queue,
			boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> arguments) throws IOException {
		return _channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
	}

	public void queueDeclareNoWait(String queue, boolean durable,
			boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		_channel.queueDeclareNoWait(queue, durable, exclusive, autoDelete, arguments);
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclarePassive(
			String queue) throws IOException {
		return _channel.queueDeclarePassive(queue);
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue)
			throws IOException {
		return _channel.queueDelete(queue);
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue,
			boolean ifUnused, boolean ifEmpty) throws IOException {
		return _channel.queueDelete(queue, ifUnused, ifEmpty);
	}

	public void queueDeleteNoWait(String queue, boolean ifUnused,
			boolean ifEmpty) throws IOException {
		_channel.queueDeleteNoWait(queue, ifUnused, ifEmpty);
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey) throws IOException {
		return _channel.queueBind(queue, exchange, routingKey);
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		return _channel.queueBind(queue, exchange, routingKey, arguments);
	}

	public void queueBindNoWait(String queue, String exchange,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		_channel.queueBindNoWait(queue, exchange, routingKey, arguments);
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey) throws IOException {
		return _channel.queueUnbind(queue, exchange, routingKey);
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		return _channel.queueUnbind(queue, exchange, routingKey, arguments);
	}

	public PurgeOk queuePurge(String queue) throws IOException {
		return _channel.queuePurge(queue);
	}

	public GetResponse basicGet(String queue, boolean autoAck)
			throws IOException {
		return _channel.basicGet(queue, autoAck);
	}

	public void basicAck(long deliveryTag, boolean multiple) throws IOException {
		_channel.basicAck(deliveryTag, multiple);
	}

	public void basicNack(long deliveryTag, boolean multiple, boolean requeue)
			throws IOException {
		_channel.basicNack(deliveryTag, multiple, requeue);
	}

	public void basicReject(long deliveryTag, boolean requeue)
			throws IOException {
		_channel.basicReject(deliveryTag, requeue);
	}

	public String basicConsume(String queue, Consumer callback)
			throws IOException {
		return _channel.basicConsume(queue, callback);
	}

	public String basicConsume(String queue, boolean autoAck, Consumer callback)
			throws IOException {
		return _channel.basicConsume(queue, autoAck, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		return _channel.basicConsume(queue, autoAck, arguments, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, Consumer callback) throws IOException {
		return _channel.basicConsume(queue, autoAck,consumerTag, callback);
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, boolean noLocal, boolean exclusive,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		return _channel.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments, callback);
	}

	public void basicCancel(String consumerTag) throws IOException {
		_channel.basicCancel(consumerTag);
	}

	public RecoverOk basicRecover() throws IOException {
		return _channel.basicRecover();
	}

	public RecoverOk basicRecover(boolean requeue) throws IOException {
		return _channel.basicRecover(requeue);
	}

	public SelectOk txSelect() throws IOException {
		return _channel.txSelect();
	}

	public CommitOk txCommit() throws IOException {
		return _channel.txCommit();
	}

	public RollbackOk txRollback() throws IOException {
		return _channel.txRollback();
	}

	public com.rabbitmq.client.AMQP.Confirm.SelectOk confirmSelect()
			throws IOException {
		return _channel.confirmSelect();
	}

	public long getNextPublishSeqNo() {
		return _channel.getNextPublishSeqNo();
	}

	public boolean waitForConfirms() throws InterruptedException {
		return _channel.waitForConfirms();
	}

	public boolean waitForConfirms(long timeout) throws InterruptedException,
			TimeoutException {
		return _channel.waitForConfirms(timeout);
	}

	public void waitForConfirmsOrDie() throws IOException, InterruptedException {
		_channel.waitForConfirmsOrDie();
	}

	public void waitForConfirmsOrDie(long timeout) throws IOException,
			InterruptedException, TimeoutException {
		_channel.waitForConfirmsOrDie(timeout);
	}

	public void asyncRpc(Method method) throws IOException {
		_channel.asyncRpc(method);
	}

	public Command rpc(Method method) throws IOException {
		return _channel.rpc(method);
	}

	public long messageCount(String queue) throws IOException {
		return _channel.messageCount(queue);
	}

	public long consumerCount(String queue) throws IOException {
		return _channel.consumerCount(queue);
	}
	

}
