package com.bol.mancala.service;

import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.CreateBoardDTO;
import com.bol.mancala.service.dto.MoveRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface MancalaGameService {

     BoardDTO createBoard(CreateBoardDTO createBoardDTO);

     BoardDTO move(MoveRequestDTO moveRequestDTO);

     BoardDTO findById(String id);
}
