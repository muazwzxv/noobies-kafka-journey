package io.cilantroz.demo.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Properties;

public class ProducerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I'm a kafka producer");

        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create a producer record - to send to kafka
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("local_kafka_cluster_basic", "hello world");

        // send data
        producer.send(producerRecord);

        // flush and close the producer -
        // sync operation, tell producer to send all data and block until done
        producer.flush();

        // close the producer
        // close() actually calls flush() before closing the producer
        producer.close();
    }
}
