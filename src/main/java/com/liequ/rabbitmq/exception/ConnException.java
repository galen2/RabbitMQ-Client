package com.liequ.rabbitmq.exception;
 public  class ConnException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2054067783013352706L;
	public ConnException(String msg) {
        super(msg);
    }
    public ConnException(String msg, Exception e) {
        super(msg, e);
    }
}
