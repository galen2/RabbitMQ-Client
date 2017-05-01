package com.liequ.rabbitmq;
import java.io.IOException;
import java.util.Map;
import com.rabbitmq.client.Channel;
public class QueueFactory {

	 public static  void declareDurableQueue(Channel channel,String q) throws IOException {
	        channel.queueDeclare(q, true, false, false, null);
	 }
	 
	 public static void declareTransientQueue(Channel channel,String q) throws IOException {
	        channel.queueDeclare(q, false, false, false, null);
	 }

	 public static void declareTransientQueue(Channel channel,String q, Map<String, Object> args) throws IOException {
	        channel.queueDeclare(q, false, false, false, args);
	 }
	 
	 public static void deleteQueue(Channel channel,String q) throws IOException {
	        channel.queueDelete(q);
	 }
	 
}
