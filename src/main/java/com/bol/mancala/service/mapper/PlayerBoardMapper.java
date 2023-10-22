package com.bol.mancala.service.mapper;

import com.bol.mancala.entity.PlayerBoard;
import com.bol.mancala.service.dto.PlayerBoardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerBoardMapper {
    @Mapping(source = "store.amount", target = "storeAmount")
    @Mapping(source = "player.id", target = "playerId")
    PlayerBoardDTO toDto(PlayerBoard board);
}
