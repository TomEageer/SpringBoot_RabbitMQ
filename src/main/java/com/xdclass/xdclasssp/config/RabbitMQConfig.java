package com.xdclass.xdclasssp.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig  {

    public static final String EXCHANGE_NAME = "order_exchange";
    public static final String QUEUE = "order_queue";

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange orderExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    /**
     * 队列
     * @return
     */
    @Bean
    public Queue orderQueue(){
        return QueueBuilder.durable(QUEUE).build();
    }

    /**
     *交换机和队列绑定关系
     */
    @Bean
    public Binding orderBinding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("order.#").noargs();
    }
}
