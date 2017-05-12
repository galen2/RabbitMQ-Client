package com.liequ.rabbitmq.publish;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liequ.rabbitmq.ConnectionManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;

public class PublishWorkQueue {
	
	private final static Logger LOG = LoggerFactory.getLogger(PublishWorkQueue.class);

	public static void basicPublish(String brokerName,String queueName,String message) throws Exception, IOException{
		basicPublish(brokerName, queueName, message, false, false, false, null);
	}
	
	public static void basicPublish(String brokerName, String queueName, String message,boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments) throws Exception {
		Channel channel = ConnectionManager.getInstance().getChannel(brokerName);
		try{
			channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
			String exchange_name = queueName+"_exchange";
		    channel.exchangeDeclare(exchange_name, "direct",durable);
		    channel.queueBind(queueName, exchange_name, queueName);
		    channel.basicPublish(exchange_name, queueName,
		    		durable?MessageProperties.PERSISTENT_TEXT_PLAIN:MessageProperties.PERSISTENT_TEXT_PLAIN,
		        message.getBytes("UTF-8"));
		} finally {
			channel.close();
		}
	}
    
	public static void basicPublishACK(String brokerName, String queueName, final String message,boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments,final PublishACKCallBack callBack) throws Exception {
		Channel channel = ConnectionManager.getInstance().getChannel(brokerName);
		channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
		String exchange_name = queueName+"_exchange";
	    channel.exchangeDeclare(exchange_name, "direct",durable);
	    channel.queueBind(queueName, exchange_name, queueName);
		try{
			final AtomicBoolean ack  = new AtomicBoolean(false);
			channel.addConfirmListener(new ConfirmListener() {
	    	    public void handleAck(long seqNo, boolean multiple) {
	    	    	ack.set(true);
	    	    }
	    	    public void handleNack(long seqNo, boolean multiple) {
	    	    	ack.set(true);
	    	    	callBack.handlerMissMsg(message);
	    	    }
	    	});
			channel.confirmSelect();
			channel.basicPublish(exchange_name, queueName,
			    		durable?MessageProperties.PERSISTENT_TEXT_PLAIN:MessageProperties.PERSISTENT_TEXT_PLAIN,
			        message.getBytes("UTF-8"));
			while(!ack.get())
	            Thread.sleep(10);
			channel.waitForConfirms();
		}catch (Exception e){
			throw e;
		}
	}
}
