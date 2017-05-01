package com.liequ.rabbitmq.workqueue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.liequ.rabbitmq.QueueFactory;
import com.liequ.rabbitmq.QueueMessageHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class ConsumerQueue {
	
	private static final int workThreadNum = 5;
	
	public ConsumerQueue(Channel channel, String queue, boolean autoAck, QueueMessageHandler handler, int workThreadNum ){
		
	}
	public static  void consumerPersistent(String queue,boolean autoAck,QueueMessageHandler handler,Channel channel  ) throws IOException {
		QueueFactory.declareDurableQueue(channel, queue);
		constumer(channel, queue, autoAck,handler);
    }
	
	public static  void consumerVolatitle(String queue,boolean autoAck, QueueMessageHandler handler, Channel channel ) throws IOException {
		QueueFactory.declareTransientQueue(channel, queue);
		constumer(channel, queue, autoAck,handler);
    }

	private static void constumer(final Channel channel,String queue,final boolean autoAck, final QueueMessageHandler handler) throws IOException{
		channel.basicQos(1);
	    final QueueingConsumer consumer = new QueueingConsumer(channel);

	    channel.basicConsume(queue, autoAck,consumer);

	    final AtomicInteger count = new AtomicInteger(workThreadNum);
	    final CountDownLatch latch = new CountDownLatch(workThreadNum);

	    for(int i = 0; i < workThreadNum; i++){
	      new Thread(){
	        @Override public void run(){
	          try {
	            while(true){
	            	Delivery delivery = consumer.nextDelivery();
	            	handler.consumer(delivery);
	            	 if(!autoAck){
		        		  channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		        	  }
	            }
	          } catch (ShutdownSignalException sig) {
	              count.decrementAndGet();
	          } catch (Exception e) {
	            throw new RuntimeException(e);
	          } finally {
	            latch.countDown();
	          }
	        }
	      }.start();
	    }
	}
}
