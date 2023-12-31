package com.bol.mancala.service.dto;

import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private String id;
    private Map<PlayerNumber, PlayerBoardDTO> playerBoards;
    private PlayerNumber playerRound;
    private Long version;
    private GameStatus status;

}
