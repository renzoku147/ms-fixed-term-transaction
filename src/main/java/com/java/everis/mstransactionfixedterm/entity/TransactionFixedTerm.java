package com.java.everis.mstransactionfixedterm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @NotNull
    private FixedTerm fixedTerm;

    @NotNull
    private String transactionCode;

    @NotNull
    private TypeTransaction typeTransaction;

    @NotNull
    private Double transactionAmount;

    private Double commissionAmount;

    private LocalDateTime transactionDateTime;

}
