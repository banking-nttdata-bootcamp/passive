package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.events.EventKafka;

public interface KafkaService {
    void consumerSave(EventKafka<?> eventKafka);
}
