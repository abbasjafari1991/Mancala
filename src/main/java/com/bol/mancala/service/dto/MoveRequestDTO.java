package com.bol.mancala.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveRequestDTO {
    @NotNull String boardId;
    @NotNull String playerId;
    @NotNull Integer index;
    @NotNull Long version;
}
