package com.java.everis.mstransactionfixedterm.service.impl;

import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import com.java.everis.mstransactionfixedterm.repository.TransactionFixedTermRepository;
import com.java.everis.mstransactionfixedterm.service.TransactionFixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionFixedTermServiceImpl implements TransactionFixedTermService {

    @Autowired
    private TransactionFixedTermRepository fixedTermRepository;

    @Override
    public Mono<TransactionFixedTerm> create(TransactionFixedTerm t) {
        return fixedTermRepository.save(t);
    }

    @Override
    public Flux<TransactionFixedTerm> findAll() {
        return fixedTermRepository.findAll();
    }

    @Override
    public Mono<TransactionFixedTerm> findById(String id) {
        return fixedTermRepository.findById(id);
    }

    @Override
    public Mono<TransactionFixedTerm> update(TransactionFixedTerm t) {
        return fixedTermRepository.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return fixedTermRepository.findById(t)
                .flatMap(tar -> fixedTermRepository.delete(tar).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> countMovements(String t) {
        return fixedTermRepository.findByFixedTermId(t).count();
    }

}
