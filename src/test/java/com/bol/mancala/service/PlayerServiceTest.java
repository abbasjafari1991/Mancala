package com.bol.mancala.service;

import com.bol.mancala.entity.Player;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.PlayerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PlayerServiceTest {

   @MockBean
   private PlayerRepository playerRepository;

   @Autowired
   private PlayerService playerService;

   @Test
   void testSavePlayer() {
      // Create a sample Player
      Player player = Player.builder().id(1L).name("Alice").build();
      PlayerDTO playerDTO = PlayerDTO.builder().name("Alice").build();

      // Mock the repository's save method
      when(playerRepository.save(any(Player.class))).thenReturn(player);

      PlayerDTO savedPlayer = playerService.save(playerDTO);

      // Assertions
      assertEquals(player.getId(), savedPlayer.getId());
      assertEquals(player.getName(), savedPlayer.getName());
   }

   @Test
   void testFindPlayerById() {
      // Create a sample PlayerDTO
      Player player = Player.builder().id(1L).name("Bob").build();

      // Mock the repository's findById method
      when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
      PlayerDTO foundPlayer = playerService.findById(1L);

      // Assertions
      assertNotNull(foundPlayer);
      assertEquals(1L, foundPlayer.getId());
      assertEquals("Bob", foundPlayer.getName());
   }

   @Test
   void testDeletePlayer() {
      // Mock the repository's deleteById method
      Mockito.doNothing().when(playerRepository).deleteById(1L);
      playerService.delete(1L);
      verify(playerRepository).deleteById(1L);
   }
}
