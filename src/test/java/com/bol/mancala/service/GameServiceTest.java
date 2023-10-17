package com.bol.mancala.service;

import com.bol.mancala.entity.Board;
import com.bol.mancala.entity.Player;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class GameServiceTest {

    private static final Long FIRST_PLAYER_ID = 1L;
    private static final String FIRST_PLAYER_NAME = "p1";
    private static final Long SECOND_PLAYER_ID = 2L;
    private static final Long BOARD_ID = 12L;
    private static final String SECOND_PLAYER_NAME = "p2";
    @Autowired
    GameService gameService;
    @MockBean
    PlayerRepository playerRepository;
    @MockBean
    BoardRepository boardRepository;

    @Test
    void shouldCreateBordWhenPlayerIdsAreExist() {
        Mockito.when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(Player.builder().id(FIRST_PLAYER_ID).name(FIRST_PLAYER_NAME).build()));
        Mockito.when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(Player.builder().id(SECOND_PLAYER_ID).name(SECOND_PLAYER_NAME).build()));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).build());
        BoardDTO board = gameService.createBoard(FIRST_PLAYER_ID, SECOND_PLAYER_ID);
        assertThat(board).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
    }

    @Test
    void shouldThrowsExceptionCreateBordWhenFirstPlayerIdIsNotExist() {
        Mockito.when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.empty());
        Mockito.when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(Player.builder().id(SECOND_PLAYER_ID).name(SECOND_PLAYER_NAME).build()));
        Throwable exception = assertThrows(RuntimeException.class, () -> gameService.createBoard(FIRST_PLAYER_ID, SECOND_PLAYER_ID));
        assertThat(exception).isNotNull();
        verify(boardRepository, never()).save(any());
    }

}