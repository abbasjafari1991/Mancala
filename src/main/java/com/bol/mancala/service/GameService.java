package com.bol.mancala.service;

import com.bol.mancala.entity.*;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.mapper.BoardMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class GameService {

    private static final int PIT_INIT_AMOUNT = 4;
    private static final int SIZE_OF_PIT = 6;
    private static final int STORE_INIT_AMOUNT = 0;
    private final PlayerRepository playerRepository;
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;

    public GameService(PlayerRepository playerRepository, BoardRepository boardRepository, BoardMapper boardMapper) {
        this.playerRepository = playerRepository;
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
    }

    @Transactional
    public BoardDTO createBoard(@NotNull Long firstPlayerId, @NotNull Long secondPlayerId) {
        if (firstPlayerId.equals(secondPlayerId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First player and second player should be different.");
        Optional<Player> firstPlayer = playerRepository.findById(firstPlayerId);
        Optional<Player> secondPlayer = playerRepository.findById(secondPlayerId);
        if (firstPlayer.isEmpty() || secondPlayer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FirstPlayer or secondPlayer is not valid");
        }
        Board board = createBoard(firstPlayer, secondPlayer);

        board = boardRepository.save(board);
        return boardMapper.toDto(board);

    }

    private Board createBoard(Optional<Player> firstPlayer, Optional<Player> secondPlayer) {
        EnumMap<PlayerNumber, PlayerBoard> playerBoards = createPlayerBoards(firstPlayer, secondPlayer);
        Board board = Board.builder().status(GameStatus.IN_PROGRESS).playerBoards(playerBoards)
                .playerRound(ThreadLocalRandom.current().nextBoolean() ? PlayerNumber.ONE : PlayerNumber.TWO)
                .build();
        return board;
    }

    private EnumMap<PlayerNumber, PlayerBoard> createPlayerBoards(Optional<Player> firstPlayer, Optional<Player> secondPlayer) {
        EnumMap<PlayerNumber, PlayerBoard> playerBoards = new EnumMap<>(PlayerNumber.class);
        Stream.of(PlayerNumber.ONE, PlayerNumber.TWO).forEach(playerNumber -> {
            PlayerBoard playerBoard = createPlayerBoard(playerNumber == PlayerNumber.ONE ? firstPlayer.get() : secondPlayer.get());
            playerBoards.put(playerNumber, playerBoard);
        });
        return playerBoards;
    }

    private PlayerBoard createPlayerBoard(Player player) {
        var pits = IntStream.range(0, SIZE_OF_PIT).mapToObj(i -> Pit.builder().index(i).amount(PIT_INIT_AMOUNT).build()).collect(Collectors.toSet());
        return PlayerBoard.builder().player(player).pits(pits).store(Store.builder().amount(STORE_INIT_AMOUNT).build()).build();
    }

    /*public BoardDTO move(@NotNull Long boardId, @NotNull Integer index) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        if (optionalBoard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board not valid");
        }
        optionalBoard.ifPresent(board -> {
            PlayerNumber playerRound = board.getPlayerRound();
            PlayerBoard playerBoard = board.getPlayerBoards().get(playerRound);
            Pit selectedPit = playerBoard.getPits().get(index - 1);
            for (int i = selectedPit.getIndex(); i < playerBoard.getPits().size(); i++) {

            }
        });
        return null;

    }*/
}
