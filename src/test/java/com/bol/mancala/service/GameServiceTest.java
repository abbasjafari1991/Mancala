package com.bol.mancala.service;

import com.bol.mancala.entity.Board;
import com.bol.mancala.entity.Pit;
import com.bol.mancala.entity.Player;
import com.bol.mancala.entity.Store;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;

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
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        BoardDTO boardDTO = gameService.createBoard(FIRST_PLAYER_ID, SECOND_PLAYER_ID);
        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        assertThat(board).extracting(Board::getPlayerRound).isNotNull();
        assertThat(board).extracting(Board::getStatus).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().keySet()).isNotNull().extracting(Set::size).isEqualTo(2);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.ONE).getPits())
                .isNotEmpty().hasSize(6)
                .extracting(Pit::getAmount, Pit::getIndex)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(4, 0),
                        Tuple.tuple(4, 1),
                        Tuple.tuple(4, 2),
                        Tuple.tuple(4, 3),
                        Tuple.tuple(4, 4),
                        Tuple.tuple(4, 5));
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(0);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.TWO).getPits())
                .isNotEmpty().hasSize(6)
                .extracting(Pit::getAmount, Pit::getIndex)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(4, 0),
                        Tuple.tuple(4, 1),
                        Tuple.tuple(4, 2),
                        Tuple.tuple(4, 3),
                        Tuple.tuple(4, 4),
                        Tuple.tuple(4, 5));
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(0);

    }

    @Test
    void shouldThrowsExceptionCreateBordWhenFirstPlayerIdIsNotExist() {
        Mockito.when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.empty());
        Mockito.when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(Player.builder().id(SECOND_PLAYER_ID).name(SECOND_PLAYER_NAME).build()));
        Throwable exception = assertThrows(RuntimeException.class, () -> gameService.createBoard(FIRST_PLAYER_ID, SECOND_PLAYER_ID));
        assertThat(exception).isNotNull();
        verify(boardRepository, never()).save(any());
    }

    @Test
    void shouldThrowsExceptionCreateBordWhenFirstPlayerIdsAreEquals() {
        Throwable exception = assertThrows(RuntimeException.class, () -> gameService.createBoard(FIRST_PLAYER_ID, FIRST_PLAYER_ID));
        assertThat(exception).isNotNull();
        verify(boardRepository, never()).save(any());
        verify(playerRepository, never()).findById(any());
    }

}