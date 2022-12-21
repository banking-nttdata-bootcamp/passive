package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.PassiveDto;
import com.nttdata.bootcamp.entity.dto.CurrentAccountDto;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.service.FixedTermService;
import com.nttdata.bootcamp.service.SavingAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private CurrentAccountService currentAccountService;
    @Autowired
    private FixedTermService fixedTermService;
    @Autowired
    private SavingAccountService savingAccountService;


    @GetMapping("/reportAccountsByCustomer/{dni}")
    public Flux<PassiveDto> findCurrentAccountByAccountNumber(@PathVariable("dni") String dni) {

        Flux<Passive> listCurrent= currentAccountService.findCurrentAccountByCustomer(dni);
        Flux<Passive> listSaving = savingAccountService.findSavingAccountByCustomer(dni);
        Flux<Passive> listFixedTerm= fixedTermService.findFixedTermByCustomer(dni);

        ArrayList<PassiveDto> listPassive = new ArrayList<>();

        listCurrent.
                toStream().
                forEach(x-> listPassive.add(new PassiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"CurrentAccount")));
        listSaving.
                toStream().
                forEach(x-> listPassive.add(new PassiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"SavingAccount")));
        listFixedTerm.
                toStream().
                forEach(x-> listPassive.add(new PassiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"FixedTermAccount")));

        return Flux.fromStream(listPassive.stream());


    }

}
