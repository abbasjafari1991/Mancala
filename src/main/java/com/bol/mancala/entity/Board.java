package com.bol.mancala.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
public class Board {

    @Id
    private String id;
    @OneToMany(cascade = CascadeType.ALL)
    private Map<PlayerNumber, PlayerBoard> playerBoards;
    private PlayerNumber playerRound;

}
