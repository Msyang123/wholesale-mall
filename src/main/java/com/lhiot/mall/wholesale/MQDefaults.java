package com.lhiot.mall.wholesale;

public interface MQDefaults {

    String DIRECT_EXCHANGE_NAME = "direct-exchange";//延迟队列信道

    String DLX_QUEUE_NAME = "dlx-queue";//延迟队列队列名

    String REPEAT_QUEUE_NAME ="repeat-queue";//转发队列队列名


    /**
     * 是否持久化
     */
    boolean DURABLE = true;
    /**
     * 仅创建者可以使用的私有队列，断开后自动删除
     */
    boolean EXCLUSIVE = false;
    /**
     * 当所有消费客户端连接断开后，是否自动删除队列
     */
    boolean AUTO_DELETE = false;
}
