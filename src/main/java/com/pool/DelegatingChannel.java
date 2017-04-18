package com.pool;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.pool.DelegatingConnection;
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

public class DelegatingChannel<C extends Channel> implements Channel{
	//真实的channel
	private volatile C _channel;
	private DelegatingConnection _conn ;
	public DelegatingChannel(C channel,DelegatingConnection conn){
		this._channel = channel;
		this._conn = conn;
	}
	
	protected Channel getInternalChannel(){
		return _channel;
	}
	
	public DelegatingConnection getDelegatingMQConnection(){
		return _conn;
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

	public int getChannelNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() throws IOException, TimeoutException {
		// TODO Auto-generated method stub
		
	}

	public void close(int closeCode, String closeMessage) throws IOException,
			TimeoutException {
		// TODO Auto-generated method stub
		
	}

	public boolean flowBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void abort() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void abort(int closeCode, String closeMessage) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void addReturnListener(ReturnListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeReturnListener(ReturnListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearReturnListeners() {
		// TODO Auto-generated method stub
		
	}

	public void addFlowListener(FlowListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeFlowListener(FlowListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearFlowListeners() {
		// TODO Auto-generated method stub
		
	}

	public void addConfirmListener(ConfirmListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeConfirmListener(ConfirmListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearConfirmListeners() {
		// TODO Auto-generated method stub
		
	}

	public Consumer getDefaultConsumer() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultConsumer(Consumer consumer) {
		// TODO Auto-generated method stub
		
	}

	public void basicQos(int prefetchSize, int prefetchCount, boolean global)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicQos(int prefetchCount, boolean global) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicQos(int prefetchCount) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicPublish(String exchange, String routingKey,
			BasicProperties props, byte[] body) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, BasicProperties props, byte[] body)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicPublish(String exchange, String routingKey,
			boolean mandatory, boolean immediate, BasicProperties props,
			byte[] body) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public DeclareOk exchangeDeclare(String exchange, String type)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DeclareOk exchangeDeclare(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void exchangeDeclareNoWait(String exchange, String type,
			boolean durable, boolean autoDelete, boolean internal,
			Map<String, Object> arguments) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public DeclareOk exchangeDeclarePassive(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DeleteOk exchangeDelete(String exchange, boolean ifUnused)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void exchangeDeleteNoWait(String exchange, boolean ifUnused)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public DeleteOk exchangeDelete(String exchange) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public BindOk exchangeBind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void exchangeBindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public UnbindOk exchangeUnbind(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void exchangeUnbindNoWait(String destination, String source,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare()
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare(String queue,
			boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> arguments) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void queueDeclareNoWait(String queue, boolean durable,
			boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclarePassive(
			String queue) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String queue,
			boolean ifUnused, boolean ifEmpty) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void queueDeleteNoWait(String queue, boolean ifUnused,
			boolean ifEmpty) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void queueBindNoWait(String queue, String exchange,
			String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String queue,
			String exchange, String routingKey, Map<String, Object> arguments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public PurgeOk queuePurge(String queue) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public GetResponse basicGet(String queue, boolean autoAck)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void basicAck(long deliveryTag, boolean multiple) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicNack(long deliveryTag, boolean multiple, boolean requeue)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void basicReject(long deliveryTag, boolean requeue)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public String basicConsume(String queue, Consumer callback)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String basicConsume(String queue, boolean autoAck, Consumer callback)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String basicConsume(String queue, boolean autoAck,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, Consumer callback) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String basicConsume(String queue, boolean autoAck,
			String consumerTag, boolean noLocal, boolean exclusive,
			Map<String, Object> arguments, Consumer callback)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void basicCancel(String consumerTag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public RecoverOk basicRecover() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public RecoverOk basicRecover(boolean requeue) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public SelectOk txSelect() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public CommitOk txCommit() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public RollbackOk txRollback() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public com.rabbitmq.client.AMQP.Confirm.SelectOk confirmSelect()
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getNextPublishSeqNo() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean waitForConfirms() throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean waitForConfirms(long timeout) throws InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		return false;
	}

	public void waitForConfirmsOrDie() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	public void waitForConfirmsOrDie(long timeout) throws IOException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		
	}

	public void asyncRpc(Method method) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public Command rpc(Method method) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public long messageCount(String queue) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long consumerCount(String queue) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
