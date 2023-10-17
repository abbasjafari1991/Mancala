package com.bol.mancala.entity;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @ManyToOne
    private Player player;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Pit> pits;
    @ManyToOne(cascade = CascadeType.ALL)
    private Store store;


}
