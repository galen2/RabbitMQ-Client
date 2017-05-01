package com.liequ.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public interface QueueMessageHandler {
	void extendChannel (Channel channel);
	
	void consumer(Delivery delivery);
	
	void basicAck(Delivery delivery);
}
