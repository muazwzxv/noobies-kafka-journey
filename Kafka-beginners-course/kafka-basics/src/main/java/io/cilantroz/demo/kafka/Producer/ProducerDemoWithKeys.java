package io.cilantroz.demo.kafka.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithKeys.class.getSimpleName());

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

        // send data
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 10; i++) {
                String topic = "local_kafka_cluster_basic";
                String key = "id_" + i;
                String value = "Hello bois " + i;

                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
                produceMessage(producer, producerRecord);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.warn(e.getMessage());
                }
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
                log.info("Messages sent \n" +
                        "Key: " + record.key() + "\n" +
                        "Partition: " + metadata.partition() + "\n"
                );
                return;
            }
            log.error("Error while producing", e);
        });
    }
}
