package com.bol.mancala.entity;


import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private Map<PlayerNumber, PlayerBoard> playerBoards;
    @Enumerated(EnumType.STRING)
    private PlayerNumber playerRound;
    @Version
    private Long version;
    @Enumerated(EnumType.STRING)
    private GameStatus status;

}
