package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.CustomerKafkaDto;
import com.nttdata.bootcamp.events.CreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Date;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private PassiveRepository passiveRepository;

    @KafkaListener(
            topics = "${topic.customer.name:topic_customer}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(CreatedEventKafka.class)) {
            CreatedEventKafka createdEvent = (CreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    createdEvent.getId(),
                    createdEvent.getData().toString());
            CustomerKafkaDto customerKafkaDto = ((CreatedEventKafka) eventKafka).getData();
            Passive passive = new Passive();
            passive.setDni(customerKafkaDto.getDni());
            passive.setTypeCustomer(customerKafkaDto.getTypeCustomer());
            passive.setFlagVip(customerKafkaDto.getFlagVip());
            passive.setFlagPyme(customerKafkaDto.getFlagPyme());
            passive.setCreationDate(new Date());
            this.passiveRepository.save(passive).subscribe();
            //customers = Flux.just(((CustomerCreatedEventKafka) eventKafka).getData());
        }
    }

}
