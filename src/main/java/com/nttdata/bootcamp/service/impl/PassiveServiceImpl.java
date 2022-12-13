package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.PassiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PassiveServiceImpl implements PassiveService {

    @Autowired
    private PassiveRepository passiveRepository;

    public Mono<Passive> searchBySavingCustomer(Passive dataPersonalCustomer){
        Mono<Passive> savingsAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni()) &&
                        x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer()) &&
                        x.getSaving().equals(true)
                )
                .next();
        return savingsAccount;
    }
    public Mono<Passive> searchByCurrentCustomer(Passive dataPersonalCustomer){
        Mono<Passive> currentAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni()) &&
                        x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer()) &&
                        x.getCurrentAccount().equals(true)
                )
                .next();
        return currentAccount;
    }
    public Mono<Passive> searchByFixedTermCustomer(Passive dataPersonalCustomer){
        Mono<Passive> fixedTermAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni()) &&
                        x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer()) &&
                        x.getFixedTerm().equals(true)
                )
                .next();
        return fixedTermAccount;
    }
}
