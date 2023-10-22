package com.bol.mancala.entity;


import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Board {

    @Id
    private String id;
    @Size(min = 2, max = 2)
    private Map<PlayerNumber, PlayerBoard> playerBoards;
    private PlayerNumber playerRound;
    @Version
    private Long version;
    @NotNull
    private GameStatus status;

}
