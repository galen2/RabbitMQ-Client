package com;
import java.io.IOException;
import java.util.UUID;

import com.rabbitmq.client.Channel;


public class ExchangeFactory {
	
	 private static Channel channel = ToolUtils.getChannelInstance();
	 
	 public static void declareTransientTopicExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "topic", false);
	 }
	 
	 public static void declareDurableTopicExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "topic", true);
	 }
	 
	 
	 public static void declareDurableDirectExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "direct", true);
	 }
	 
	 public static void declareTransientDirectExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "direct", false);
	 }
	 
	 public static void declareDurableFanoutExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "fanout", true);
	 }
	 
	 public static void declareTransientFanoutExchange(String x) throws IOException {
	        channel.exchangeDeclare(x, "fanout", false);
	 }
	 
	 public static void deleteExchange(String x) throws IOException {
	        channel.exchangeDelete(x);
	 }
	 
	 public static String generateExchangeName() {
	        return "exchange" + UUID.randomUUID().toString();
	    }

}
