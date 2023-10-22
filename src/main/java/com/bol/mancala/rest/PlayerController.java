package com.bol.mancala.rest;

import com.bol.mancala.service.PlayerService;
import com.bol.mancala.service.dto.PlayerDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/player")
public class PlayerController {
    private final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody @Valid PlayerDTO player) {
        logger.info("Received a request to create a player.");
        PlayerDTO createdPlayer = playerService.save(player);
        logger.info("Player created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
    }

    @GetMapping
    public ResponseEntity<Page<PlayerDTO>> getPlayers(Pageable pageable) {
        logger.info("Received a request to get the list of players.");
        Page<PlayerDTO> players = playerService.findAll(pageable);
        if (players != null) {
            logger.info("Players retrieved successfully.");
            return ResponseEntity.ok(players);
        } else {
            logger.info("No players found.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable String id) {
        logger.info("Received a request to retrieve a player by ID: {}", id);
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            logger.info("Player retrieved successfully.");
            return ResponseEntity.ok(player);
        } else {
            logger.info("Player not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable String id, @RequestBody @Valid PlayerDTO updatedPlayer) {
        logger.info("Received a request to update a player with ID: {}", id);
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            updatedPlayer.setId(id); // Make sure the ID is set correctly
            player = playerService.save(updatedPlayer); // This should update the player
            logger.info("Player updated successfully.");
            return ResponseEntity.ok(player);
        } else {
            logger.info("Player not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        logger.info("Received a request to delete a player with ID: {}", id);
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            playerService.delete(id);
            logger.info("Player deleted successfully.");
            return ResponseEntity.noContent().build();
        } else {
            logger.info("Player not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
