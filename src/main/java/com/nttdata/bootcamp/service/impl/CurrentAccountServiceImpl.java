package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {

    @Autowired
    private PassiveRepository passiveRepository;
    @Autowired
    private PassiveService passiveService;

    @Override
    public Flux<Passive> findAllCurrentAccount() {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount());
        return passives;
    }

    @Override
    public Flux<Passive> findCurrentAccountByCustomer(String dni) {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount() && x.getDni().equals(dni));
        return passives;
    }

    @Override
    public Mono<Passive> findCurrentAccountByAccountNumber(String accountNumber) {
        Mono<Passive> passiveMono = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount() && x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }

    @Override
    public Mono<Passive> saveCurrentAccount(Passive dataCurrentAccount,Boolean creditCard) {
        Mono<Passive> passive = Mono.empty();
        dataCurrentAccount.setFlagVip(false);
        dataCurrentAccount.setMovementsMonthly(false);
        dataCurrentAccount.setLimitMovementsMonthly(0);
        dataCurrentAccount.setSaving(false);
        dataCurrentAccount.setCurrentAccount(true);
        dataCurrentAccount.setFixedTerm(false);

        if(creditCard){
            dataCurrentAccount.setFreeCommission(true);
            dataCurrentAccount.setCommissionMaintenance(0);
            dataCurrentAccount.setFlagPyme(true);
        }
        else{
            dataCurrentAccount.setFreeCommission(false);
            dataCurrentAccount.setCommissionMaintenance(1);
            dataCurrentAccount.setFlagPyme(false);
        }

        if(dataCurrentAccount.getTypeCustomer().equals(Constant.PERSONAL_CUSTOMER)){
            passive = passiveService.searchByCurrentCustomer(dataCurrentAccount);
        }
        return passive
                .flatMap(__ -> Mono.<Passive>error(new Error("The customer with DNI" + dataCurrentAccount.getDni() + " have an account")))
                .switchIfEmpty(passiveRepository.save(dataCurrentAccount));
    }

    @Override
    public Mono<Passive> updateCurrentAccount(Passive dataCurrentAccount) {
        Mono<Passive> passiveMono = findCurrentAccountByAccountNumber(dataCurrentAccount.getAccountNumber());
        try{
            Passive passive = passiveMono.block();
            passive.setCommissionMaintenance(dataCurrentAccount.getCommissionMaintenance());
            passive.setModificationDate(dataCurrentAccount.getModificationDate());
            return passiveRepository.save(passive);
        }catch (Exception e){
            return Mono.<Passive>error(new Error("The account number " + dataCurrentAccount.getAccountNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteCurrentAccount(String accountNumber) {
        Mono<Passive> passiveMono = findCurrentAccountByAccountNumber(accountNumber);
        try {
            return passiveRepository.delete(passiveMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("The customer with DNI " + accountNumber + " do not exists"));
        }
    }

}
