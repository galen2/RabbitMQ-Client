总概：
1、此组件是RabbitMQ的Java版本客户端，因为与MQ交互过程中，connection和channel的创建及关闭,最后往往会成为系统的瓶颈，且
在整个消息的生产和消费之中，均有这两个对象来完成所有消息的交互，因此，此组件主要对这两个对象的创建及整个生命周期进行简单的管理；
2、目前只是简单的对象生命周期管理，随后版本迭代中会加入相应丰富的数据监控及优化策略；
3、ConnectionManager.getInstance().getChannel()来获取channel对象

配置信息：rabbitmq.json
1、支持多个broker链接对象的管理创建
2、里面包含创建MQ所必须的参数信息


