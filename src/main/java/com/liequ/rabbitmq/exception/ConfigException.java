package com.liequ.rabbitmq.exception;
 public  class ConfigException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2054067783013352706L;
	public ConfigException(String msg) {
        super(msg);
    }
    public ConfigException(String msg, Exception e) {
        super(msg, e);
    }
}
