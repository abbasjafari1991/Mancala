package com.bol.mancala.rest;

import com.bol.mancala.service.GameService;
import com.bol.mancala.service.dto.BoardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create-board")
    public ResponseEntity<BoardDTO> createBoard(
            @RequestParam(name = "firstPlayerId") Long firstPlayerId,
            @RequestParam(name = "secondPlayerId") Long secondPlayerId) {
        BoardDTO board = gameService.createBoard(firstPlayerId, secondPlayerId);
        return ResponseEntity.ok(board);
    }

}
