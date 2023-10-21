package com.bol.mancala.service;

import com.bol.mancala.entity.*;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.MoveRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameServiceTest {

    private static final Long FIRST_PLAYER_ID = 1L;
    private static final String FIRST_PLAYER_NAME = "p1";
    public static final Player FIRST_PLAYER = Player.builder().id(FIRST_PLAYER_ID).name(FIRST_PLAYER_NAME).build();
    private static final Long SECOND_PLAYER_ID = 2L;
    private static final Long BOARD_ID = 12L;
    private static final String SECOND_PLAYER_NAME = "p2";
    public static final Player SECOND_PLAYER = Player.builder().id(SECOND_PLAYER_ID).name(SECOND_PLAYER_NAME).build();
    @Autowired
    GameService gameService;
    @MockBean
    PlayerRepository playerRepository;
    @MockBean
    BoardRepository boardRepository;

    @Test
    void shouldCreateBordWhenPlayerIdsAreExist() {
        Mockito.when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        Mockito.when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        BoardDTO boardDTO = gameService.createBoard(FIRST_PLAYER_ID, SECOND_PLAYER_ID);
        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        verifyBoard(board, GameStatus.IN_PROGRESS, getNewPitMaps(), getNewPitMaps(), 0, 0);

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

    @Test
    void moveShouldClearHomeAddAddToNextHome_PlayerSide() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound)));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(0).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, getNewPitMaps(), 0, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void moveShouldClearHomeAddAddToNextHomeOppositeSideAndAddToStore() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound)));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());

    }

    @Test
    void moveShouldClearHomeAddAddToNextHomeOppositeSideAndAddToStoreShouldNotAddToOppositeStore() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(8);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(5).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }


    @Test
    void moveWithNextRoundRewardShouldDoNotChangePlayerRound() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound)));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(2).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(0).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(5).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound);
    }

    @Test
    void moveShouldTakeOppositePitRewardWhenLastStoneIsInEmptyHome() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(1);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(4).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 5, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void moveShouldNotTakePitRewardWhenLastStoneIsInEmptyOppositeHome() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound);
        initBoard.getPlayerBoards().get(playerRound.oppositeSide()).getPits().get(1).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(3);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(FIRST_PLAYER));
        when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = gameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(1).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }


    //todo Test Validation

    private Board initBoard(long id, PlayerNumber playerRound) {
        return Board.builder().id(id).playerRound(playerRound).version(0L).status(GameStatus.IN_PROGRESS).playerBoards(
                Map.of(
                        PlayerNumber.ONE, PlayerBoard.builder().player(FIRST_PLAYER).store(new Store(0)).pits(getNewPitMaps()).build(),
                        PlayerNumber.TWO, PlayerBoard.builder().player(FIRST_PLAYER).store(new Store(0)).pits(getNewPitMaps()).build()
                )).build();
    }

    private Map<Integer, Pit> getNewPitMaps() {
        return Map.of(0, getNewPit(), 1, getNewPit(), 2, getNewPit(), 3, getNewPit(), 4, getNewPit(), 5, getNewPit());
    }

    private Pit getNewPit() {
        return Pit.builder().amount(4).build();
    }

    private void verifyBoard(Board board, GameStatus gameStatus, Map<Integer, Pit> boardOnePits, Map<Integer, Pit> boardTwoPits, int storeOneStone, int storeTwoStone) {
        assertThat(board).extracting(Board::getPlayerRound).isNotNull();
        assertThat(board).extracting(Board::getStatus).isEqualTo(gameStatus);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().keySet()).isNotNull().extracting(Set::size).isEqualTo(2);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.ONE).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardOnePits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(storeOneStone);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.TWO).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardTwoPits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(storeTwoStone);
    }

}