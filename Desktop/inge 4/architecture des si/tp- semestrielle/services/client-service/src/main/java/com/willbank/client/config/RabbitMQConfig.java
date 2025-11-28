package com.willbank.client.config;

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

@Configuration
public class RabbitMQConfig {
    
    public static final String CLIENT_EXCHANGE = "client.events";
    public static final String CLIENT_CREATED_QUEUE = "notification.client.created";
    public static final String CLIENT_UPDATED_QUEUE = "notification.client.updated";
    public static final String CLIENT_KYC_VALIDATED_QUEUE = "notification.client.kyc.validated";
    public static final String CLIENT_STATUS_CHANGED_QUEUE = "notification.client.status.changed";
    
    public static final String CLIENT_CREATED_ROUTING_KEY = "client.created";
    public static final String CLIENT_UPDATED_ROUTING_KEY = "client.updated";
    public static final String CLIENT_KYC_VALIDATED_ROUTING_KEY = "client.kyc.validated";
    public static final String CLIENT_STATUS_CHANGED_ROUTING_KEY = "client.status.changed";
    
    @Bean
    public TopicExchange clientExchange() {
        return new TopicExchange(CLIENT_EXCHANGE);
    }
    
    @Bean
    public Queue clientCreatedQueue() {
        return new Queue(CLIENT_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue clientUpdatedQueue() {
        return new Queue(CLIENT_UPDATED_QUEUE, true);
    }
    
    @Bean
    public Queue clientKycValidatedQueue() {
        return new Queue(CLIENT_KYC_VALIDATED_QUEUE, true);
    }
    
    @Bean
    public Queue clientStatusChangedQueue() {
        return new Queue(CLIENT_STATUS_CHANGED_QUEUE, true);
    }
    
    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
                .bind(clientCreatedQueue())
                .to(clientExchange())
                .with(CLIENT_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
                .bind(clientUpdatedQueue())
                .to(clientExchange())
                .with(CLIENT_UPDATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding clientKycValidatedBinding() {
        return BindingBuilder
                .bind(clientKycValidatedQueue())
                .to(clientExchange())
                .with(CLIENT_KYC_VALIDATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding clientStatusChangedBinding() {
        return BindingBuilder
                .bind(clientStatusChangedQueue())
                .to(clientExchange())
                .with(CLIENT_STATUS_CHANGED_ROUTING_KEY);
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
