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
        EnumMap<PlayerNumber, PlayerBoard> playerBoards = new EnumMap<>(PlayerNumber.class);
        Stream.of(PlayerNumber.ONE, PlayerNumber.TWO).forEach(playerNumber -> {
            PlayerBoard playerBoard = createPlayerBoard(playerNumber == PlayerNumber.ONE ? firstPlayer.get() : secondPlayer.get());
            playerBoards.put(playerNumber, playerBoard);
        });
        Board board = Board.builder().status(GameStatus.IN_PROGRESS).playerBoards(playerBoards)
                .playerRound(ThreadLocalRandom.current().nextBoolean() ? PlayerNumber.ONE : PlayerNumber.TWO)
                .build();

        board = boardRepository.save(board);
        return boardMapper.toDto(board);

    }

    private PlayerBoard createPlayerBoard(Player player) {
        var pitList = IntStream.range(0, SIZE_OF_PIT).mapToObj(i -> Pit.builder().index(i).amount(PIT_INIT_AMOUNT).build()).toList();
        return PlayerBoard.builder().player(player).pits(pitList).store(Store.builder().amount(STORE_INIT_AMOUNT).build()).build();
    }

    public BoardDTO move(Long gameId, Integer index) {

        return null;

    }
}
