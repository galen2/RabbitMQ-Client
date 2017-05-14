package com.liequ.rabbitmq.factory;

public class BrokerException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7662140994176633994L;

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
