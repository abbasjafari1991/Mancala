package com.bol.mancala.service;

import com.bol.mancala.entity.Board;
import com.bol.mancala.entity.Pit;
import com.bol.mancala.entity.Player;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.CreateBoardDTO;
import com.bol.mancala.service.dto.MoveRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bol.mancala.utils.BoardTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MancalaGameServiceTest {

    private static final String FIRST_PLAYER_ID = "1L";
    private static final String FIRST_PLAYER_NAME = "p1";
    public static final Player FIRST_PLAYER = Player.builder().id(FIRST_PLAYER_ID).name(FIRST_PLAYER_NAME).build();
    private static final String SECOND_PLAYER_ID = "2L";
    private static final String BOARD_ID = "12L";
    private static final String SECOND_PLAYER_NAME = "p2";
    public static final Player SECOND_PLAYER = Player.builder().id(SECOND_PLAYER_ID).name(SECOND_PLAYER_NAME).build();
    @Autowired
    MancalaGameService mancalaGameService;
    @MockBean
    PlayerRepository playerRepository;
    @MockBean
    BoardRepository boardRepository;

    @Test
    void shouldCreateBordWhenPlayerIdsAreExist() {
        Mockito.when(playerRepository.findAllById(any())).thenReturn(List.of(FIRST_PLAYER, SECOND_PLAYER));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        BoardDTO boardDTO = mancalaGameService.createBoard(CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER_ID, PlayerNumber.TWO, SECOND_PLAYER_ID))).build());
        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        verifyBoard(board, GameStatus.IN_PROGRESS, getNewPitMaps(), getNewPitMaps(), 0, 0);
        assertThat(board).extracting(Board::getPlayerRound).isNotNull();

    }

    @Test
    void shouldThrowsExceptionCreateBordWhenFirstPlayerIdIsNotExist() {
        Mockito.when(playerRepository.findAllById(List.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID))).thenReturn(List.of(FIRST_PLAYER));
        CreateBoardDTO board = CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER_ID, PlayerNumber.TWO, SECOND_PLAYER_ID))).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.createBoard(board));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("Players are not valid");
        verify(boardRepository, never()).save(any());
    }

    @Test
    void shouldThrowsExceptionCreateBordWhenFirstPlayerIdsAreEquals() {
        CreateBoardDTO board = CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER_ID, PlayerNumber.TWO, SECOND_PLAYER_ID))).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.createBoard(board));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("Players are not valid");
        verify(boardRepository, never()).save(any());
        verify(playerRepository, never()).findById(any());
    }

    @Test
    void moveShouldClearHomeAddAddToNextHome_PlayerSide() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER)));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(0).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER)));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(8);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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
    void gameShouldBeFinishIfSideIsEmpty() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(0).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(1).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(2).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(3).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(1);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> emptyPits = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(0).build(), 2, Pit.builder().amount(0).build(), 3, Pit.builder().amount(0).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(0).build());
        verifyBoard(board, GameStatus.FINISH, emptyPits, emptyPits, 25, 0);
        assertThat(board).extracting(Board::getPlayerRound).isNull();
    }


    @Test
    void moveWithNextRoundRewardShouldDoNotChangePlayerRound() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER)));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(2).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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
    void moveShouldTakeOppositePitRewardWhenLastStoneIsInEmptyHomeAndOppositeIsNotEmpty() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(1);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(4).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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
    void moveShouldNotTakeOppositePitRewardWhenLastStoneIsInEmptyHomeAndOppositeIsEmpty() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(1);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound.oppositeSide()).getPits().get(0).setAmount(0);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(4).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

        ArgumentCaptor<Board> boardArgumentCaptor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(boardArgumentCaptor.capture());
        assertThat(boardDTO).isNotNull().extracting(BoardDTO::getId).isEqualTo(BOARD_ID);
        assertThat(boardDTO).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(0L);
        Board board = boardArgumentCaptor.getValue();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(1).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(board, GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 0, 0);
        assertThat(board).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void moveShouldNotTakePitRewardWhenLastStoneIsInEmptyOppositeHome() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound.oppositeSide()).getPits().get(1).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(3);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();

        BoardDTO boardDTO = mancalaGameService.move(moveRequest);

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


    @Test
    void shouldThrowExceptionIfDoNotFindBoard() {
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.empty());
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(4).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.move(moveRequest));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("Board not valid");

        verify(boardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfTheGameIsFinish() {
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(Board.builder().status(GameStatus.FINISH).build()));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(4).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.move(moveRequest));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("Game is already finished");

        verify(boardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfTheHomeIsEmpty() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.move(moveRequest));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("selected pit can not be empty");

        verify(boardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfTheHomeIsIsAnotherPlayerRound() {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(BOARD_ID, playerRound.oppositeSide(), FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        when(boardRepository.findById(BOARD_ID)).thenReturn(Optional.of(initBoard));
        Mockito.when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().id(BOARD_ID).version(0L).build());
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(BOARD_ID).playerId(FIRST_PLAYER_ID).version(0L).index(5).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> mancalaGameService.move(moveRequest));
        assertThat(exception).isNotNull().extracting(ResponseStatusException::getReason).isEqualTo("This another player round!");
        verify(boardRepository, never()).save(any());

    }


}