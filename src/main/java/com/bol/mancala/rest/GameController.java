package com.bol.mancala.rest;

import com.bol.mancala.service.GameService;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.CreateBoardDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create-board")
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody CreateBoardDTO createBoardDTO) {
        BoardDTO board = gameService.createBoard(createBoardDTO);
        return ResponseEntity.ok(board);
    }

}
