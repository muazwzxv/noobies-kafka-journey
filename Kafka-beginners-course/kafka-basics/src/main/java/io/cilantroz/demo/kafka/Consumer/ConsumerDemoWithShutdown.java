package io.cilantroz.demo.kafka.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWithShutdown {
    private static final Logger log = LoggerFactory.getLogger(io.cilantroz.demo.kafka.Producer.ProducerDemo.class.getSimpleName());
    private static final String groupId = "my-java-application";
    private static final String topic = "local_kafka_cluster_basic";

    public static void main(String[] args) {
        log.info("I'm a kafka consumer ......");

        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // create consumer configs
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());
        properties.setProperty("group.id", groupId);

        // non - consumer group must be set before starting the application
        // earliest - read from the beginning of the topic
        // latest - read the new messages only
        // properties.setProperty("auto.offset.reset", "non/earliest/latest");
        properties.setProperty("auto.offset.reset", "earliest");

        // create a consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // get a reference to the main thread
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown, exiting with consumer.wakeup()...");
            consumer.wakeup();

            // join the main thread to allow execution of code in th emain thread
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }));


        try {
            // subscribe to a topic
            consumer.subscribe(Arrays.asList(topic));

            // poll for data
            while (true) {
                log.info("Polling .......");

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, String> record: records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }
            }
        } catch (WakeupException e) {
            log.info("consumer is shutting down .......");
        } catch (Exception e) {
            log.warn("unexpected exception in consumer", e);
        } finally {
            // commit offsets and close consumer
            consumer.close();
            log.info("consumer has been gracefully shut down");
        }

    }
}
