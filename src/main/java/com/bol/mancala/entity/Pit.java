package com.bol.mancala.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Integer amount;

    public void addAmountPlusOne() {
        this.amount++;
    }

    @Transient
    public boolean isEmpty() {
        return amount == 0;
    }

    public void clear() {
        this.amount = 0;
    }
}
