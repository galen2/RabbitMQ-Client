package workqueue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.QueueFactory;
import com.ToolUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class ConsumerQueue {
	private static final int THREADS = 5;
	public static  void consumerPersistent(String queue,boolean autoAck ) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareDurableQueue(channel, queue);
		constumer(channel, queue, autoAck);
    }
	
	public static  void consumerVolatitle(String queue,boolean autoAck ) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareTransientQueue(channel, queue);
		constumer(channel, queue, autoAck);
    }
	
	private static void constumer(final Channel channel,String queue,final boolean autoAck) throws IOException{
		channel.basicQos(1);
	    final QueueingConsumer consumer = new QueueingConsumer(channel);

	    channel.basicConsume(queue, autoAck,consumer);

	    final AtomicInteger count = new AtomicInteger(THREADS);
	    final CountDownLatch latch = new CountDownLatch(THREADS);

	    for(int i = 0; i < THREADS; i++){
	      new Thread(){
	        @Override public void run(){
	          try {
	            while(true){
	            	Delivery delivery = consumer.nextDelivery();
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
