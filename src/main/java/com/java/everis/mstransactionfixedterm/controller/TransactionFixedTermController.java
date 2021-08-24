package com.java.everis.mstransactionfixedterm.controller;

import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import com.java.everis.mstransactionfixedterm.service.TransactionFixedTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RefreshScope
@RestController
@RequestMapping("/transactionFixedTerm")
@Slf4j
public class TransactionFixedTermController {

    @Autowired
    TransactionFixedTermService fixedTermService;

    @GetMapping("list")
    public Flux<TransactionFixedTerm> findAll(){
        return fixedTermService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<TransactionFixedTerm> findById(@PathVariable String id){
        return fixedTermService.findById(id);
    }

    @GetMapping("/checkAllTransactions/{numberCard}")
    public Flux<TransactionFixedTerm> findAllTransactions(@PathVariable String numberCard){
        return fixedTermService.findByFixedTermCardNumber(numberCard);
    }

    @GetMapping("/checkAllCommissions/{numberCard}/{from}/{to}")
    public Flux<TransactionFixedTerm> findAllCommissions(@PathVariable String numberCard,
                                                         @PathVariable(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                         @PathVariable(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
        return fixedTermService.findByFixedTermCardNumber(numberCard)
                .filter(ft -> ft.getTransactionDateTime().toLocalDate().isAfter(from)
                            && ft.getTransactionDateTime().toLocalDate().isBefore(to)
                            && ft.getCommissionAmount() > 0);
    }


    @PostMapping("/create")
    public Mono<ResponseEntity<TransactionFixedTerm>> create(@RequestBody TransactionFixedTerm transactionFixedTerm){

        return fixedTermService.findFixedTermById(transactionFixedTerm.getFixedTerm().getId())
            .flatMap(fixedTerm -> fixedTermService.countTransactions(transactionFixedTerm.getFixedTerm().getId(), transactionFixedTerm.getTypeTransaction())
                                .filter(count -> {
                                    Integer limit = 0;
                                    switch (transactionFixedTerm.getTypeTransaction()){
                                        case DEPOSIT: limit = fixedTerm.getLimitDeposits(); break;
                                        case DRAFT: limit = fixedTerm.getLimitDraft(); break;
                                    }
                                    return count < limit && fixedTerm.getAllowDateTransaction().equals(LocalDate.now());
                                })
                                .flatMap(count -> {
                                    if(fixedTerm.getFreeTransactions() > count){
                                        transactionFixedTerm.setCommissionAmount(0.0);
                                        fixedTerm.setBalance(fixedTerm.getBalance() + transactionFixedTerm.getTransactionAmount());
                                    }else{
                                        transactionFixedTerm.setCommissionAmount(fixedTerm.getCommissionTransactions());
                                        fixedTerm.setBalance(fixedTerm.getBalance() + transactionFixedTerm.getTransactionAmount() - fixedTerm.getCommissionTransactions());
                                    }
                                    return fixedTermService.updateFixedTerm(fixedTerm)
                                            .flatMap(ftUpdate -> {
                                                transactionFixedTerm.setFixedTerm(ftUpdate);
                                                transactionFixedTerm.setTransactionDateTime(LocalDateTime.now());
                                                return fixedTermService.create(transactionFixedTerm);
                                            });
                                })
            )
            .map(sat ->new ResponseEntity<>(sat , HttpStatus.CREATED) )
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @PutMapping("/update")
    public Mono<ResponseEntity<TransactionFixedTerm>> update(@RequestBody TransactionFixedTerm transactionFixedTerm) {

        return fixedTermService.findFixedTermById(transactionFixedTerm.getFixedTerm().getId())
                .flatMap(sa ->{
                    return fixedTermService.findById(transactionFixedTerm.getId())
                    .flatMap(sat ->{
                    switch (transactionFixedTerm.getTypeTransaction()) {
                        case DEPOSIT: sa.setBalance(sa.getBalance() - sat.getTransactionAmount() );
                             return fixedTermService.updateFixedTerm(sa).flatMap(saUpdate -> {
                                                        transactionFixedTerm.setFixedTerm(saUpdate);
                                                        transactionFixedTerm.setTransactionDateTime(LocalDateTime.now());
                                                        return fixedTermService.update(transactionFixedTerm);
                                                       });

                        case DRAFT: sa.setBalance(sa.getBalance() + sat.getTransactionAmount() - transactionFixedTerm.getTransactionAmount());
                             return fixedTermService.updateFixedTerm(sa).flatMap(saUpdate ->{
                                                        transactionFixedTerm.setFixedTerm(saUpdate);
                                                        transactionFixedTerm.setTransactionDateTime(LocalDateTime.now());
                                                        return fixedTermService.update(transactionFixedTerm);
                                                        });
                        default: return Mono.empty();
                    }
                    });
                })
                .map(sat -> new ResponseEntity<>(sat, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return fixedTermService.delete(id)
                .filter(deleteTransactionFixedTerm -> deleteTransactionFixedTerm)
                .map(deleteCustomer -> new ResponseEntity<>("Customer Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
