package com.bol.mancala.service.dto;

import com.bol.mancala.entity.enumeration.PlayerNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBoardDTO {
    @NotNull
    private Map<PlayerNumber, String> players;
}
