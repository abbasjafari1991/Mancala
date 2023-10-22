package com.bol.mancala.service.mapper;

import com.bol.mancala.entity.Player;
import com.bol.mancala.service.dto.PlayerDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTO toDto(Player player);

    List<PlayerDTO> toDto(List<Player> player);

    Player toEntity(PlayerDTO playerDTO);
}
