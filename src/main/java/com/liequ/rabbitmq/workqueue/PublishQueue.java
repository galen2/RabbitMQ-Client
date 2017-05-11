package com.liequ.rabbitmq.workqueue;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.liequ.rabbitmq.PublishCallBack;
import com.liequ.rabbitmq.QueueFactory;
import com.liequ.rabbitmq.ToolUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;

public class PublishQueue {
	
	public static  void basicPublishPersistent(byte[] msg, String routingKey) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareDurableQueue(channel, routingKey);
		channel.basicPublish("", routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
    }
	
	public static  void basicPublishPersistentACK(final String msg, String routingKey,final PublishCallBack callBack) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareDurableQueue(channel, routingKey);
		basicPublishACK(channel, routingKey, msg, MessageProperties.PERSISTENT_TEXT_PLAIN, callBack);
	}
	

    public static void basicPublishVolatile(String msg,String routingKey,Map<String, Object> args) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareTransientQueue(channel, routingKey, args);
    	channel.basicPublish("", routingKey, MessageProperties.TEXT_PLAIN, msg.getBytes("UTF-8"));
    }
	    
    
    public static void basicPublishVolatileACK(String msg, String routingKey,Map<String, Object> args,final PublishCallBack callBack) throws IOException {
		Channel channel = ToolUtils.getChannelInstance();
		QueueFactory.declareTransientQueue(channel, routingKey, args);
		basicPublishACK(channel, routingKey, msg,MessageProperties.TEXT_PLAIN, callBack);
    }
    
    
	private static void basicPublishACK(Channel channel, String routingKey,final String msg, AMQP.BasicProperties props,final PublishCallBack callBack) throws IOException {
		try{
			final AtomicBoolean ack  = new AtomicBoolean(false);
			channel.addConfirmListener(new ConfirmListener() {
	    	    public void handleAck(long seqNo, boolean multiple) {
	    	    	ack.set(true);
	    	    }
	    	    public void handleNack(long seqNo, boolean multiple) {
	    	    	ack.set(true);
	    	    	callBack.handlerMissMsg(msg);
	    	    }
	    	});
			channel.confirmSelect();
			channel.basicPublish("", routingKey,props, msg.getBytes("UTF-8"));
			while(!ack.get())
	            Thread.sleep(10);
			channel.waitForConfirms();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
