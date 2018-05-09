package com.lhiot.mall.wholesale;

import com.leon.microx.common.probe.rabbitmq.RabbitInitializer;
import com.leon.microx.util.auditing.Random;
import org.springframework.amqp.core.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author victor
 */
@Configuration
@EnableConfigurationProperties(QueueProperties.class)
public class QueueConfiguration {

    @Bean
    public InitQueue initQueues(QueueProperties properties, RabbitInitializer initializer){
        return new InitQueue(properties, initializer).doInit();
    }

    static class InitQueue{
        private QueueProperties properties;
        private RabbitInitializer initializer;

        public InitQueue(QueueProperties properties, RabbitInitializer initializer) {
            this.properties = properties;
            this.initializer = initializer;
        }

        public InitQueue doInit(){
            properties.getDirectQueue().forEach((businessName,queueNames) ->
                    initializer.delay(queueNames.get("direct_exchange_name"), queueNames.get("dlx_queue_name"), queueNames.get("repeat_queue_name"))
            );
            //initializer.delay(MQDefaults.DIRECT_EXCHANGE_NAME, MQDefaults.DLX_QUEUE_NAME, MQDefaults.REPEAT_QUEUE_NAME);
            properties.getFanoutQueue().forEach((exchangeName, queueNames) ->
                    initializer.publish(exchangeName, queueNames.toArray(new String[queueNames.size()]))
            );
            return this;
        }
    }

    //信道配置
//    @Bean
//    public DirectExchange defaultExchange() {
//        return new DirectExchange(MQDefaults.DIRECT_EXCHANGE_NAME, true, false);
//    }
//
//
//    @Bean
//    public Queue deadLetterQueue() {
//        Map<String, Object> arguments = new HashMap<>();
//        arguments.put("x-dead-letter-exchange", MQDefaults.DIRECT_EXCHANGE_NAME);
//        arguments.put("x-dead-letter-routing-key", MQDefaults.REPEAT_QUEUE_NAME);
//        Queue queue = new Queue(MQDefaults.DLX_QUEUE_NAME,true,false,false,arguments);
//        System.out.println("arguments :" + queue.getArguments());
//        return queue;
//    }
//
//    @Bean
//    public Binding  deadLetterBinding( Queue deadLetterQueue,DirectExchange defaultExchange) {
//        return BindingBuilder.bind(deadLetterQueue).to(defaultExchange).with(MQDefaults.DLX_QUEUE_NAME);
//    }
//
//    @Bean
//    public Queue repeatTradeQueue() {
//        return new Queue(MQDefaults.REPEAT_QUEUE_NAME,true,false,false);
//    }
//
//    @Bean
//    public Binding repeatTradeBinding(Queue repeatTradeQueue, DirectExchange defaultExchange) {
//        return BindingBuilder.bind(repeatTradeQueue).to(defaultExchange).with(MQDefaults.REPEAT_QUEUE_NAME);
//    }

//    @Bean
//    public FanoutExchangeRegister fanoutExchangeRegister(FanoutExchangeProperties properties){
//        return new FanoutExchangeRegister(properties);
//    }
//
//    static class FanoutExchangeRegister implements ApplicationContextAware, InitializingBean {
//
//        private DefaultListableBeanFactory beanFactory;
//        private FanoutExchangeProperties properties;
//
//        public FanoutExchangeRegister(FanoutExchangeProperties properties) {
//            this.properties = properties;
//        }
//
//        @Override
//        public void afterPropertiesSet() {
//            Assert.notNull(this.beanFactory, "[DefaultListableBeanFactory] field is required; it must not be null");
//            Assert.notNull(this.properties, "[FanoutExchangeProperties] field is required; it must not be null");
//            this.properties.getPublisher().forEach((exchangeName, queueNames) -> {
//                if (!CollectionUtils.isEmpty(queueNames)) {
//                    FanoutExchange fanoutExchange = this.registerBean(exchangeName, () -> new FanoutExchange(exchangeName, true, false));
//                    queueNames.forEach(queueName -> {
//                        Queue queue = this.registerBean(queueName, () -> new Queue(queueName, true, false, false));
//                        beanFactory.registerSingleton(queueName + ".binding-" + Random.random(6), BindingBuilder.bind(queue).to(fanoutExchange));
//                    });
//                }
//            });
//        }
//
//        @SuppressWarnings("unchecked")
//        private <T>T registerBean(String beanName, Supplier<T> supplier){
//            if (beanFactory.containsBean(beanName + ".bean")){
//                return  (T)beanFactory.getBean(beanName + ".bean");
//            }else {
//                T bean = supplier.get();
//                beanFactory.registerSingleton(beanName + ".bean", bean);
//                return bean;
//            }
//        }
//
//        @Override
//        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//            if (applicationContext instanceof ConfigurableApplicationContext) {
//                this.beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
//            }
//        }
//    }
} 