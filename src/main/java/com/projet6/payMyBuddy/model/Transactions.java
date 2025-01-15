package com.projet6.payMyBuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Transactions {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", unique = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", unique = false)
    private User receiver;
    
    @Column
    private String description;
    
    @Column
    private double amount;
}


