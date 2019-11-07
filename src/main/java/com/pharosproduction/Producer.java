package com.pharosproduction;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;


public class Producer 
{
    // Variables
    private final KafkaProducer<String, String> producer;
    private final Logger logger = LoggerFactory.getLogger(Producer.class);


    // Constructors
    Producer(String bootstrapServer) {
        Properties props = producerProps(bootstrapServer);
        producer = new KafkaProducer<>(props);

        logger.info("Producer initialized");
    }


    // Public
    void put(String topic, String key, String value) throws ExecutionException, InterruptedException {
        logger.info("Put value: " + value + ", for key: " + key);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record, (recordMetadata, e) -> {
        if (e != null) {
            logger.error("Error while producing", e);
            return;
        }

        logger.info("Received new meta. Topic: " + recordMetadata.topic()
            + "; Partition: " + recordMetadata.partition()
            + "; Offset: " + recordMetadata.offset()
            + "; Timestamp: " + recordMetadata.timestamp());
        }).get(); // Don't use get in Production operation it makes a sync operation
    }

    void close() {
        logger.info("Closing producer's connection");
        producer.close();
    }


    // Private
    private Properties producerProps(String bootstrapServer) {
        String serializer = StringSerializer.class.getName();
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer);
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer);

        return props;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String server = "127.0.0.1:9092";
        String topic = "demo_topic";
    
        Producer producer = new Producer(server);
        producer.put(topic, "user1", "John");
        producer.put(topic, "user2", "Peter");
        producer.close();
    }

}
