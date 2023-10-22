package com.bol.mancala.utils;

import com.bol.mancala.entity.*;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.PitDTO;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public interface BoardTestUtils {
    static Board initBoard(String id, PlayerNumber playerRound, Player player1, Player player2) {
        Board board = Board.builder().playerRound(playerRound).status(GameStatus.IN_PROGRESS).playerBoards(
                Map.of(
                        PlayerNumber.ONE, PlayerBoard.builder().player(player1).store(new Store(0)).pits(getNewPitMaps()).build(),
                        PlayerNumber.TWO, PlayerBoard.builder().player(player2).store(new Store(0)).pits(getNewPitMaps()).build()
                )).build();
        if (id != null)
            board.setId(id);
        return board;
    }

    static Map<Integer, Pit> getNewPitMaps() {
        return Map.of(0, getNewPit(), 1, getNewPit(), 2, getNewPit(), 3, getNewPit(), 4, getNewPit(), 5, getNewPit());
    }

    static Map<Integer, PitDTO> getNewPitDTOMaps() {
        return Map.of(0, getNewPitDTO(), 1, getNewPitDTO(), 2, getNewPitDTO(), 3, getNewPitDTO(), 4, getNewPitDTO(), 5, getNewPitDTO());
    }

    static Pit getNewPit() {
        return Pit.builder().amount(4).build();
    }

    static PitDTO getNewPitDTO() {
        return PitDTO.builder().amount(4).build();
    }

    static void verifyBoard(Board board, GameStatus gameStatus, Map<Integer, Pit> boardOnePits, Map<Integer, Pit> boardTwoPits, int storeOneStone, int storeTwoStone) {
        assertThat(board).extracting(Board::getStatus).isEqualTo(gameStatus);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().keySet()).isNotNull().extracting(Set::size).isEqualTo(2);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.ONE).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardOnePits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(storeOneStone);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.TWO).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardTwoPits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO).getStore()).isNotNull().extracting(Store::getAmount).isEqualTo(storeTwoStone);
    }

    static void verifyBoardDTO(Board initBoard, BoardDTO board, PlayerNumber playerRound, GameStatus gameStatus, Map<Integer, PitDTO> boardOnePits, Map<Integer, PitDTO> boardTwoPits, int storeOneStone, int storeTwoStone) {

        assertThat(board).isNotNull().extracting(BoardDTO::getId).isEqualTo(initBoard.getId());
        assertThat(board).extracting(BoardDTO::getVersion).isNotNull().isEqualTo(1L);
        assertThat(board).extracting(BoardDTO::getPlayerRound).isEqualTo(playerRound);

        assertThat(board).extracting(BoardDTO::getStatus).isEqualTo(gameStatus);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().keySet()).isNotNull().extracting(Set::size).isEqualTo(2);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.ONE).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardOnePits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.ONE).getStoreAmount()).isNotNull().isEqualTo(storeOneStone);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO)).isNotNull();
        assertThat(board.getPlayerBoards().get(PlayerNumber.TWO).getPits()).isNotEmpty().hasSize(6).containsExactlyInAnyOrderEntriesOf(boardTwoPits);
        assertThat(board).extracting(board1 -> board1.getPlayerBoards().get(PlayerNumber.TWO).getStoreAmount()).isNotNull().isEqualTo(storeTwoStone);
    }
}
