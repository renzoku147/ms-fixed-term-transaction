package com.java.everis.mstransactionfixedterm.repository;

import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionFixedTermRepository extends ReactiveMongoRepository<TransactionFixedTerm, String> {

    Flux<TransactionFixedTerm> findByFixedTermId(String id);
}

