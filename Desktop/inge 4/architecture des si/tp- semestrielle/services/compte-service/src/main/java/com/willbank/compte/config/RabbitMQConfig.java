package com.willbank.compte.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
// @ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {
    
    public static final String COMPTE_EXCHANGE = "compte.events";
    public static final String COMPTE_CREATED_QUEUE = "notification.compte.created";
    public static final String SOLDE_UPDATED_QUEUE = "notification.solde.updated";
    public static final String COMPTE_STATUS_CHANGED_QUEUE = "notification.compte.status.changed";
    
    public static final String COMPTE_CREATED_ROUTING_KEY = "compte.created";
    public static final String SOLDE_UPDATED_ROUTING_KEY = "compte.solde.updated";
    public static final String COMPTE_STATUS_CHANGED_ROUTING_KEY = "compte.status.changed";
    
    @Bean
    public TopicExchange compteExchange() {
        return new TopicExchange(COMPTE_EXCHANGE);
    }
    
    @Bean
    public Queue compteCreatedQueue() {
        return new Queue(COMPTE_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue soldeUpdatedQueue() {
        return new Queue(SOLDE_UPDATED_QUEUE, true);
    }
    
    @Bean
    public Queue compteStatusChangedQueue() {
        return new Queue(COMPTE_STATUS_CHANGED_QUEUE, true);
    }
    
    @Bean
    public Binding compteCreatedBinding() {
        return BindingBuilder
                .bind(compteCreatedQueue())
                .to(compteExchange())
                .with(COMPTE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding soldeUpdatedBinding() {
        return BindingBuilder
                .bind(soldeUpdatedQueue())
                .to(compteExchange())
                .with(SOLDE_UPDATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding compteStatusChangedBinding() {
        return BindingBuilder
                .bind(compteStatusChangedQueue())
                .to(compteExchange())
                .with(COMPTE_STATUS_CHANGED_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
