package com.bol.mancala.service.mapper;

import com.bol.mancala.entity.Board;
import com.bol.mancala.service.dto.BoardDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PlayerBoardMapper.class)
public interface BoardMapper {
    BoardDTO toDto(Board board);
}
