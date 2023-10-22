package com.bol.mancala.rest;

import com.bol.mancala.service.GameService;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.CreateBoardDTO;
import com.bol.mancala.service.dto.MoveRequestDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/game/board")
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable String id) {
        logger.info("Received a request to retrieve a board by ID: {}", id);
        BoardDTO boardDTO = gameService.findById(id);
        if (boardDTO != null) {
            logger.info("Board retrieved successfully.");
            return ResponseEntity.ok(boardDTO);
        } else {
            logger.info("Board not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody CreateBoardDTO createBoardDTO) {
        logger.info("Received a request to create a board.");
        BoardDTO board = gameService.createBoard(createBoardDTO);
        logger.info("Board created successfully.");
        return ResponseEntity.ok(board);
    }

    @PutMapping("/move")
    public ResponseEntity<BoardDTO> move(@Valid @RequestBody MoveRequestDTO moveRequestDTO) {
        logger.info("Received a move request.");
        BoardDTO board = gameService.move(moveRequestDTO);
        logger.info("Move request processed.");
        return ResponseEntity.ok(board);
    }


}
