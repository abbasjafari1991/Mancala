package com.bol.mancala.service;

import com.bol.mancala.entity.*;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.entity.enumeration.PlayerNumber;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.BoardDTO;
import com.bol.mancala.service.dto.MoveRequestDTO;
import com.bol.mancala.service.mapper.BoardMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class GameService {

    private static final int PIT_INIT_AMOUNT = 4;
    private static final int PIT_EMPTY_AMOUNT = 0;
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
        checkThePlayersAreNotSame(firstPlayerId, secondPlayerId);
        Board board = createBoardWithDefaultParameters(firstPlayerId, secondPlayerId);
        board = boardRepository.save(board);
        return boardMapper.toDto(board);
    }

    private Board createBoardWithDefaultParameters(Long firstPlayerId, Long secondPlayerId) {
        Optional<Player> firstPlayer = playerRepository.findById(firstPlayerId);
        Optional<Player> secondPlayer = playerRepository.findById(secondPlayerId);
        if (firstPlayer.isEmpty() || secondPlayer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FirstPlayer or secondPlayer is not valid");
        }
        return createBoard(firstPlayer.get(), secondPlayer.get());
    }

    private void checkThePlayersAreNotSame(Long firstPlayerId, Long secondPlayerId) {
        if (firstPlayerId.equals(secondPlayerId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First player and second player should be different.");
    }

    @Transactional
    public BoardDTO move(MoveRequestDTO moveRequestDTO) {
        Optional<Board> optionalBoard = boardRepository.findById(moveRequestDTO.getBoardId());
        boardInProgressingValidation(optionalBoard);
        return optionalBoard.map(board -> {
            play(moveRequestDTO, board);
            board = boardRepository.save(board);
            return boardMapper.toDto(board);
        }).orElse(null);

    }

    private void play(MoveRequestDTO moveRequestDTO, Board board) {
        PlayerNumber playerRound = board.getPlayerRound();
        Integer index = moveRequestDTO.getIndex();
        checkRoundOfPlayer(moveRequestDTO.getPlayerNumber(), playerRound);
        Integer amount = takePitAmount(index, board, playerRound);
        index++;
        PlayerNumber nextPlayerNumber = moveAmountToNextPitsAndStore(index, board.getPlayerBoards(), playerRound, playerRound, amount);
        board.setPlayerRound(nextPlayerNumber);
        if (itCanBeFinish(board, playerRound)) {
            finishGame(board, playerRound);
        }
        board.setVersion(moveRequestDTO.getVersion());
    }

    private void finishGame(Board board, PlayerNumber playerRound) {
        collectThePitForWinner(board, playerRound);
        board.setStatus(GameStatus.FINISH);
    }

    private void collectThePitForWinner(Board board, PlayerNumber playerRound) {
        Store store = board.getPlayerBoards().get(playerRound).getStore();
        playerRound.getOtherPlayers().forEach(playerNumber ->
                board.getPlayerBoards().get(playerNumber).getPits().values().forEach(pit -> {
                    store.addToAmount(pit.getAmount());
                    pit.clear();
                })
        );
    }

    private boolean itCanBeFinish(Board board, PlayerNumber playerRound) {
        return board.getPlayerBoards().get(playerRound).getPits().values().stream().allMatch(Pit::isEmpty);
    }

    private Board createBoard(@NotNull Player firstPlayer, @NotNull Player secondPlayer) {
        EnumMap<PlayerNumber, PlayerBoard> playerBoards = createPlayerBoards(firstPlayer, secondPlayer);
        return Board.builder().status(GameStatus.IN_PROGRESS).playerBoards(playerBoards)
                .playerRound(ThreadLocalRandom.current().nextBoolean() ? PlayerNumber.ONE : PlayerNumber.TWO)
                .build();
    }

    private EnumMap<PlayerNumber, PlayerBoard> createPlayerBoards(@NotNull Player firstPlayer, @NotNull Player secondPlayer) {
        EnumMap<PlayerNumber, PlayerBoard> playerBoards = new EnumMap<>(PlayerNumber.class);
        Stream.of(PlayerNumber.ONE, PlayerNumber.TWO).forEach(playerNumber -> {
            PlayerBoard playerBoard = createPlayerBoard(playerNumber == PlayerNumber.ONE ? firstPlayer : secondPlayer);
            playerBoards.put(playerNumber, playerBoard);
        });
        return playerBoards;
    }

    private PlayerBoard createPlayerBoard(Player player) {
        Map<Integer, Pit> pits = new HashMap<>();
        IntStream.range(0, SIZE_OF_PIT).forEach(i -> pits.put(i, Pit.builder().amount(PIT_INIT_AMOUNT).build()));
        return PlayerBoard.builder().player(player).pits(pits).store(new Store(STORE_INIT_AMOUNT)).build();
    }


    private void boardInProgressingValidation(Optional<Board> optionalBoard) {
        if (optionalBoard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board not valid");
        } else if (optionalBoard.get().getStatus() == GameStatus.FINISH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is already finished");

        }
    }

    private Integer takePitAmount(Integer index, Board board, PlayerNumber playerRound) {
        PlayerBoard playerBoard = board.getPlayerBoards().get(playerRound);
        Pit selectedPit = playerBoard.getPits().get(index);
        Integer amount = selectedPit.getAmount();
        if (amount.equals(PIT_EMPTY_AMOUNT))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "selected pit can not be null");
        selectedPit.setAmount(PIT_EMPTY_AMOUNT);
        return amount;
    }

    private void checkRoundOfPlayer(PlayerNumber playerNumber, PlayerNumber playerRound) {
        if (playerRound != playerNumber) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This another player round!");
        }
    }

    private PlayerNumber moveAmountToNextPitsAndStore(Integer index, Map<PlayerNumber, PlayerBoard> playerBoards, PlayerNumber currentBoardPlayerNumber, PlayerNumber starterPlayerNumber, Integer amount) {
        PlayerBoard playerBoard = playerBoards.get(currentBoardPlayerNumber);
        while (amount > PIT_EMPTY_AMOUNT) {
            if (index > SIZE_OF_PIT)
                throw new RuntimeException("index it is not valid");
            if (index == SIZE_OF_PIT) {
                if (starterPlayerNumber == currentBoardPlayerNumber) {
                    playerBoard.getStore().addAmountPlusOne();
                    amount--;
                    if (amount == PIT_EMPTY_AMOUNT) {
                        return starterPlayerNumber;
                    }
                }
                index = 0;
                currentBoardPlayerNumber = currentBoardPlayerNumber.next();
                playerBoard = playerBoards.get(currentBoardPlayerNumber);
            } else {
                Pit pit = playerBoard.getPits().get(index);
                if (starterPlayerNumber == currentBoardPlayerNumber && amount == 1 && pit.isEmpty()) {
                    int oppositeIndex = SIZE_OF_PIT - 1 - index;
                    Pit oppositePit = playerBoards.get(currentBoardPlayerNumber.oppositeSide()).getPits().get(oppositeIndex);
                    if (oppositePit.isEmpty()) {
                        pit.addAmountPlusOne();
                    } else {
                        playerBoard.getStore().addToAmount(oppositePit.getAmount() + amount);
                        oppositePit.clear();
                    }
                } else {
                    pit.addAmountPlusOne();
                }
                index++;
                amount--;
            }
        }
        return starterPlayerNumber.next();
    }

}
