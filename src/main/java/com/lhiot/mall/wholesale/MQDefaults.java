package com.lhiot.mall.wholesale;

public interface MQDefaults {
    String CONFIG_PREFIX = "message-queue";

    String DELAY_EXCHANGE_NAME = "delay.direct.exchange";

    String MATCH_EXCHANGE_NAME = "match.direct.exchange";

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
