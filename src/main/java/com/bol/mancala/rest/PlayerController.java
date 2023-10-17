package com.bol.mancala.rest;

import com.bol.mancala.service.PlayerService;
import com.bol.mancala.service.dto.PlayerDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody @Valid PlayerDTO player) {
        PlayerDTO createdPlayer = playerService.save(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody @Valid PlayerDTO updatedPlayer) {
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            updatedPlayer.setId(id); // Make sure the ID is set correctly
            player = playerService.save(updatedPlayer); // This should update the player
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        PlayerDTO player = playerService.findById(id);
        if (player != null) {
            playerService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
