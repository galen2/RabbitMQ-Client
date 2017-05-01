package com.liequ.rabbitmq.pool;

public class BrokerException extends RuntimeException{

	public BrokerException() {
        super();
    }

    public BrokerException(String message) {
        super(message);
    }
    
    public BrokerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
