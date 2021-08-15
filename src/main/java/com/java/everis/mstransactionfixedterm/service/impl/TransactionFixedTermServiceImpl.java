package com.java.everis.mstransactionfixedterm.service.impl;

import com.java.everis.mstransactionfixedterm.entity.FixedTerm;
import com.java.everis.mstransactionfixedterm.entity.TransactionFixedTerm;
import com.java.everis.mstransactionfixedterm.entity.TypeTransaction;
import com.java.everis.mstransactionfixedterm.repository.TransactionFixedTermRepository;
import com.java.everis.mstransactionfixedterm.service.TransactionFixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionFixedTermServiceImpl implements TransactionFixedTermService {

    WebClient webClient = WebClient.create("http://localhost:8887/ms-fixed-term/fixed/fixedTerm");

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
    public Mono<Long> countTransactions(String id, TypeTransaction typeTransaction) {
        return fixedTermRepository.findByFixedTermId(id)
                .filter(transactionFixedTerm -> transactionFixedTerm.getTypeTransaction().equals(typeTransaction))
                .count();
    }

    @Override
    public Mono<FixedTerm> findFixedTermById(String t) {
        return webClient.get().uri("/find/{id}", t)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FixedTerm .class);
    }

    @Override
    public Mono<FixedTerm> updateFixedTerm(FixedTerm ft) {
        return webClient.put().uri("/update", ft.getId())
                .accept(MediaType.APPLICATION_JSON)
                .syncBody(ft)
                .retrieve()
                .bodyToMono(FixedTerm.class);
    }

}
