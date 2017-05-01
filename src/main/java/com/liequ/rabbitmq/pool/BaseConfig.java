package com.liequ.rabbitmq.pool;

public class BaseConfig {
    public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
    
    public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;
    
    public static final int DEFAULT_MAX_CONN_TOTAL = 4;
    
    /**
     * 单个连接创建的最大channel数
     */
    public static final int DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN = 8;

}
