package com.java.everis.mstransactionfixedterm.service;

import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionFixedTermService {
    Mono<TransactionFixedTerm> create(TransactionFixedTerm t);

    Flux<TransactionFixedTerm> findAll();

    Mono<TransactionFixedTerm> findById(String id);

    Mono<TransactionFixedTerm> update(TransactionFixedTerm t);

    Mono<Boolean> delete(String t);

    Mono<Long> countMovements(String t);
}
