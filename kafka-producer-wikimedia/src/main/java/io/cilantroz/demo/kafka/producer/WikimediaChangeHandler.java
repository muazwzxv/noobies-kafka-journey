package io.cilantroz.demo.kafka.producer;

import com.launchdarkly.eventsource.MessageEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import com.launchdarkly.eventsource.EventHandler;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class WikimediaChangeHandler implements EventHandler {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final Logger log = LoggerFactory.getLogger(WikimediaChangeHandler.class.getSimpleName());

    public WikimediaChangeHandler(KafkaProducer<String, String> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void onOpen() throws Exception {

    }

    @Override
    public void onClosed() throws Exception {

    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent){
        // action on receiving message

        // log
        log.info(messageEvent.getData());

        // async
        this.producer.send(new ProducerRecord<>(this.topic, messageEvent.getData()));
    }

    @Override
    public void onComment(String s) throws Exception {

    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error in reading stream", throwable);
    }
}
