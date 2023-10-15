package com.bol.mancala.entity;


import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PlayerBoard {

    @Id
    private Long id;

    @Nonnull
    @ManyToOne
    private Player player;
    @OneToMany
    private List<Pit> pits;
    @ManyToOne
    private Store store;


}
