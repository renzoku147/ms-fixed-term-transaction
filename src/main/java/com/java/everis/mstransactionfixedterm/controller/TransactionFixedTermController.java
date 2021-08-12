package com.java.everis.mstransactionfixedterm.controller;

import com.java.everis.mstransactionfixedterm.entity.FixedTerm;
import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import com.java.everis.mstransactionfixedterm.service.TransactionFixedTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RefreshScope
@RestController
@RequestMapping("/transactionFixedTerm")
@Slf4j
public class TransactionFixedTermController {

    WebClient webClient = WebClient.create("http://localhost:8006/fixedTerm");

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

    @PostMapping("/create")
    public Mono<ResponseEntity<TransactionFixedTerm>> create(@RequestBody TransactionFixedTerm transactionFixedTerm){

        Mono<FixedTerm> fixedTerm = webClient.get().uri("/find/{id}", transactionFixedTerm.getFixedTerm().getId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FixedTerm.class); // Limite Movimientos


        return fixedTermService.countMovements(transactionFixedTerm.getFixedTerm().getId()) // N° Movimientos actuales
                .flatMap(cnt -> {
                    return fixedTerm
                            .filter(sa -> sa.getLimitMovements() > cnt)
                            .flatMap(sa -> {
                                switch (transactionFixedTerm.getTypeTransaction()){
                                    case DEPOSIT: sa.setBalance(sa.getBalance() + transactionFixedTerm.getTransactionAmount());
                                        return webClient.put().uri("/update", sa.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .syncBody(sa)
                                                .retrieve()
                                                .bodyToMono(FixedTerm.class);
                                    case DRAFT: sa.setBalance(sa.getBalance() - transactionFixedTerm.getTransactionAmount());
                                        return webClient.put().uri("/update", sa.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .syncBody(sa)
                                                .retrieve()
                                                .bodyToMono(FixedTerm.class);
                                    default: return Mono.empty();
                                }
                            })
                            .flatMap(sa -> {
                                transactionFixedTerm.setFixedTerm(sa);
                                transactionFixedTerm.setTransactionDateTime(LocalDateTime.now());
                                return fixedTermService.create(transactionFixedTerm);
                            })
                            .map(sat ->new ResponseEntity<>(sat , HttpStatus.CREATED) );
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<TransactionFixedTerm>> update(@RequestBody TransactionFixedTerm transactionFixedTerm) {
        Mono<FixedTerm> fixedTermMono = webClient.get().uri("/find/{id}", transactionFixedTerm.getFixedTerm().getId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FixedTerm.class); // Limite Movimientos

        return fixedTermMono
                .flatMap(sa ->{
                    return fixedTermService.findById(transactionFixedTerm.getId())
                    .flatMap(sat ->{
                    switch (transactionFixedTerm.getTypeTransaction()) {
                        case DEPOSIT: sa.setBalance(sa.getBalance() - sat.getTransactionAmount() );
                             return webClient.put().uri("/update", sa.getId())
                                     .accept(MediaType.APPLICATION_JSON)
                                     .syncBody(sa)
                                     .retrieve()
                                     .bodyToMono(FixedTerm.class).flatMap(saUpdate -> {
                                                        transactionFixedTerm.setFixedTerm(saUpdate);
                                                        transactionFixedTerm.setTransactionDateTime(LocalDateTime.now());
                                                        return fixedTermService.update(transactionFixedTerm);
                                                       });

                        case DRAFT: sa.setBalance(sa.getBalance() + sat.getTransactionAmount() - transactionFixedTerm.getTransactionAmount());
                             return webClient.put().uri("/update", sa.getId())
                                     .accept(MediaType.APPLICATION_JSON)
                                     .syncBody(sa)
                                     .retrieve()
                                     .bodyToMono(FixedTerm.class).flatMap(saUpdate ->{
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
