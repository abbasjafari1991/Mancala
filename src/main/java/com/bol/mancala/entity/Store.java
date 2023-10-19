package com.bol.mancala.entity;

import jakarta.persistence.Entity;

@Entity
public class Store extends Pit {
    public Store(Integer amount) {
        super(null, amount);
    }
}
