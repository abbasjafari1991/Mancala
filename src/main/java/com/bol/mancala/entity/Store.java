package com.bol.mancala.entity;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Store extends Pit {
    public Store(Integer amount) {
        super(null, amount);
    }

    public void addToAmount(Integer add) {
        super.setAmount(super.getAmount() + add);

    }
}
