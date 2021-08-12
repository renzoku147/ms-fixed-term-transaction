package com.java.everis.mstransactionfixedterm.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FixedTerm {

    String id;

    private Customer customer;

    private String cardNumber;

    private List<Person> holders;

    private List<Person> signers;

    private Double balance;

    private Integer limitMovements;

    private LocalDateTime date;

}