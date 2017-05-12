package com.liequ.rabbitmq.publish;

public interface PublishACKCallBack {

	void handlerMissMsg(String msg);
}
