/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 *
 * @author Asus
 */
@Configuration
public class KafkaOrderFullfilmentTopicConfig {
    @Value(value =  "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
    
    public static final String ORDER_FULFILLMENT_KAFKA_TOPIC = "order-fulfillment";
    
    @Bean
    public KafkaAdmin kafkaAdmin(){
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapAddress);
    return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic orderFulfillmentTopic(){
        return new NewTopic(ORDER_FULFILLMENT_KAFKA_TOPIC, 1, (short)1);
    }
}
