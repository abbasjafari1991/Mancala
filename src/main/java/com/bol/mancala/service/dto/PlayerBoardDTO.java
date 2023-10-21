package com.bol.mancala.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBoardDTO {

    private String playerId;
    private Map<Integer, PitDTO> pits;
    private Integer storeAmount;


}
