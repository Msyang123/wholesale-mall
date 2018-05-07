package com.lhiot.mall.wholesale;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author victor
 */
@Configuration
public class QueueConfiguration {

    //信道配置  
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(MQDefaults.DIRECT_EXCHANGE_NAME, true, false);
    }


    @Bean
    public Queue deadLetterQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQDefaults.DIRECT_EXCHANGE_NAME);
        arguments.put("x-dead-letter-routing-key", MQDefaults.REPEAT_QUEUE_NAME);
        Queue queue = new Queue(MQDefaults.DLX_QUEUE_NAME,true,false,false,arguments);
        System.out.println("arguments :" + queue.getArguments());
        return queue;
    }

    @Bean
    public Binding  deadLetterBinding( Queue deadLetterQueue,DirectExchange defaultExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(defaultExchange).with(MQDefaults.DLX_QUEUE_NAME);
    }

    @Bean
    public Queue repeatTradeQueue() {
        return new Queue(MQDefaults.REPEAT_QUEUE_NAME,true,false,false);
    }

    @Bean
    public Binding repeatTradeBinding(Queue repeatTradeQueue, DirectExchange defaultExchange) {
        return BindingBuilder.bind(repeatTradeQueue).to(defaultExchange).with(MQDefaults.REPEAT_QUEUE_NAME);
    }

} 