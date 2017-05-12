package com.liequ.rabbitmq.publish;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.liequ.rabbitmq.ConnectionManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

public class PublishWorkDelayQueue {
	private final static Logger LOG = LoggerFactory.getLogger(PublishWorkDelayQueue.class);
	
	public static void basicPublish(String brokerName,String queueName,String message,long time) throws Exception, IOException{
		basicPublish(brokerName, queueName, message,time, false, false, false, null);
	}
	
	public static void basicPublish(String brokerName, String queueName, String message,long time,boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments) throws Exception {
		Channel channel = ConnectionManager.getInstance().getChannel(brokerName);
		try{
			String exchangeName = queueName+"_exchange";
			
			String tempExchangeName = exchangeName+"_temp";
			String tmpQueueName = queueName+"_temp";
			
		    Map<String, Object> args = new HashMap<String, Object>();
		    args.put("x-dead-letter-exchange", exchangeName);
		    args.put("x-dead-letter-routing-key", queueName);
		    args.put("x-message-ttl", time);
			channel.exchangeDeclare(exchangeName, "direct");
			channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
	        channel.queueBind(exchangeName, queueName, queueName);
	        
	        
		    channel.exchangeDeclare(tempExchangeName, "direct");
		    channel.queueDeclare(tmpQueueName, durable, exclusive, autoDelete, arguments);
	        channel.queueBind(tmpQueueName, tempExchangeName, queueName);

	        channel.basicPublish(tmpQueueName, queueName,
		    		durable?MessageProperties.PERSISTENT_TEXT_PLAIN:MessageProperties.PERSISTENT_TEXT_PLAIN,
		        message.getBytes("UTF-8"));
		} finally {
			channel.close();
		}
	}
    
	
	/*public static void basicPublishACK(String brokerName, String queueName, final String message,boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments,final PublishACKCallBack callBack) throws Exception {
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
			e.printStackTrace();
		}
	}*/
}
