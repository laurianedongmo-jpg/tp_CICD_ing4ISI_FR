package com.willbank.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Les queues sont déjà créées par les autres services
    // On les déclare ici pour s'assurer qu'elles existent
    
    @Bean
    public Queue clientCreatedQueue() {
        return new Queue("notification.client.created", true);
    }
    
    @Bean
    public Queue clientKycValidatedQueue() {
        return new Queue("notification.client.kyc.validated", true);
    }
    
    @Bean
    public Queue transactionExecutedQueue() {
        return new Queue("notification.transaction.executed", true);
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
