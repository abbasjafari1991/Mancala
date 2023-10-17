package com.bol.mancala.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBoardDTO {

    private Long playerId;
    private List<PitDTO> pits;
    private Integer storeAmount;


}
