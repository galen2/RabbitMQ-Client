package com.liequ.rabbitmq;

import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public interface QueueMessageHandler {
	
	void initArgument(Channel channel, Map<String, Object> queueArguments);
	
	void consumer(Delivery delivery);
	
	void basicAck(Delivery delivery);
}
