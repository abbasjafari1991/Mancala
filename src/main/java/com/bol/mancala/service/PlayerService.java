package com.bol.mancala.service;

import com.bol.mancala.entity.Player;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.PlayerDTO;
import com.bol.mancala.service.mapper.PlayerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerMapper playerMapper, PlayerRepository playerRepository) {
        this.playerMapper = playerMapper;
        this.playerRepository = playerRepository;
    }

    public PlayerDTO save(PlayerDTO playerDTO) {
        Player player = playerMapper.toEntity(playerDTO);
        player = playerRepository.save(player);
        return playerMapper.toDto(player);
    }

    public PlayerDTO findById(String id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        return optionalPlayer.map(playerMapper::toDto).orElse(null);
    }

    public void delete(String id) {
        playerRepository.deleteById(id);
    }

    public List<PlayerDTO> findAll() {
        return playerMapper.toDto(playerRepository.findAll());
    }
}
