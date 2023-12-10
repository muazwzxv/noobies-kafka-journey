package io.cilantroz.demo.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallbacks {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallbacks.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I'm a kafka producer");

        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty("batch.size", "400");
        // properties.setProperty("partitioner.class", RoundRobinPartitioner.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create a producer record - to send to kafka
        // ProducerRecord<String, String> producerRecord = new ProducerRecord<>("basics_kafka", "hello world");

        // send data
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 30; i++) {
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("basics_kafka", "Hello bois " + i);
                produceMessage(producer, producerRecord);
            }

            // Sleep after each 30 message sent
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }


        // flush and close the producer -
        // sync operation, tell producer to send all data and block until done
        producer.flush();

        // close the producer
        // close() actually calls flush() before closing the producer
        producer.close();
    }

    public static void produceMessage(KafkaProducer producer, ProducerRecord record) {
        if (producer == null || record == null)
            return;

        producer.send(record, (metadata, e) -> {
            // executes every time a record is successfully sent or an exception is thrown
            if (e == null) {
                // the record was successfully sent
                log.info("Received new metadata \n" +
                        "Topic: " + metadata.topic() + "\n" +
                        "Partition: " + metadata.partition() + "\n" +
                        "Offset: " + metadata.offset() + "\n" +
                        "Timestamp: " + metadata.timestamp() + "\n"
                );
                return;
            }
            log.error("Error while producing", e);
        });
    }
}
