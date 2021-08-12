package com.java.everis.mstransactionfixedterm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Document("TransactionFixedTerm")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFixedTerm {
    @Id
    private String id;

    private FixedTerm fixedTerm;

    private String transactionCode;

    @Valid
    private TypeTransaction typeTransaction;

    @NotNull
    private Double transactionAmount;

    @NotNull
    private LocalDateTime transactionDateTime;



}
