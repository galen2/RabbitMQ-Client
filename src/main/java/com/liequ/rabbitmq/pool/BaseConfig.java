package com.liequ.rabbitmq.pool;

public class BaseConfig {
	
    public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
    
    /**
     * 获取channel阻塞等待时间
     */
    public static final int DEFAULT_MAX_WAIT_MILLIS_CHANNEL = 3000;
    
    
    /**
     * 获取connection阻塞等待时间
     */
    public static final int DEFAULT_MAX_WAIT_MILLIS_CONNECTION = 1000;

    
    
    /**
     * 初始化默认connection连接数
     */
    public static final int DEFAULT_INITIAL_CONN_SIZE = 4;

    
    /**
     * 创建connection最大数
     */
    public static final int DEFAULT_MAX_CONN_TOTAL = 4;
    
    /**
     * 单个连接创建的最大channel数
     */
    public static final int DEFAULT_MAX_CHANNEL_TOTAL_TO_CONN = 8;

}
