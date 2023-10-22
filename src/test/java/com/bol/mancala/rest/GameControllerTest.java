package com.bol.mancala.rest;

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
import com.bol.mancala.service.dto.PitDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static com.bol.mancala.utils.BoardTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameControllerTest {

    public static final String GAME_BOARD_URL = "/v1/game/board/";
    private static Player FIRST_PLAYER;
    private static final String FIRST_PLAYER_NAME = "p1";
    private static Player SECOND_PLAYER;
    private static final String SECOND_PLAYER_NAME = "p2";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public void setup() {
        FIRST_PLAYER = playerRepository.save(Player.builder().name(FIRST_PLAYER_NAME).build());
        SECOND_PLAYER = playerRepository.save(Player.builder().name(SECOND_PLAYER_NAME).build());

    }


    @Test
    void testCreateBoard() throws Exception {
        CreateBoardDTO createBoardDTO = CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER.getId(), PlayerNumber.TWO, SECOND_PLAYER.getId()))).build();
        String requestBody = objectMapper.writeValueAsString(createBoardDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(GAME_BOARD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.playerId").value(FIRST_PLAYER.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.storeAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.0.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.1.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.2.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.3.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.4.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.5.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.playerId").value(SECOND_PLAYER.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.storeAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.0.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.1.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.2.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.3.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.4.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits.5.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerRound").isNotEmpty());
    }

    @Test
    void testCreateBoardWithSamePlayerId() throws Exception {
        CreateBoardDTO createBoardDTO = CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER.getId(), PlayerNumber.TWO, FIRST_PLAYER.getId()))).build();
        String requestBody = objectMapper.writeValueAsString(createBoardDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(GAME_BOARD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateBoardWithWrongPlayerId() throws Exception {
        CreateBoardDTO createBoardDTO = CreateBoardDTO.builder().players(new EnumMap<>(Map.of(PlayerNumber.ONE, FIRST_PLAYER.getId(), PlayerNumber.TWO, "SECOND_PLAYER.getId()"))).build();
        String requestBody = objectMapper.writeValueAsString(createBoardDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(GAME_BOARD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateBoardWithNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(GAME_BOARD_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void testFindGame() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard = boardRepository.save(initBoard);

        BoardDTO boardDTO = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .get(GAME_BOARD_URL + initBoard.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString(), BoardDTO.class);

        verifyBoardDTO(initBoard, boardDTO, playerRound, GameStatus.IN_PROGRESS, getNewPitDTOMaps(), getNewPitDTOMaps(), 0, 0, 0L);
    }

    @Test
    void testFindGameNotFind() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(GAME_BOARD_URL + "Invalid_Id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void moveShouldClearHomeAddAddToNextHome_PlayerSide() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard = boardRepository.save(initBoard);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(0).build();

        BoardDTO boardDTO = move(moveRequest);
        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(0).build(), 1, PitDTO.builder().amount(5).build(), 2, PitDTO.builder().amount(5).build(), 3, PitDTO.builder().amount(5).build(), 4, PitDTO.builder().amount(5).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, getNewPitDTOMaps(), 0, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, getNewPitMaps(), 0, 0);


    }


    @Test
    void moveShouldClearHomeAddAddToNextHomeOppositeSideAndAddToStore() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard = boardRepository.save(initBoard);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = move(moveRequest);

        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(0).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(5).build(), 1, PitDTO.builder().amount(5).build(), 2, PitDTO.builder().amount(5).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 1, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());

    }

    @Test
    void moveShouldClearHomeAddAddToNextHomeOppositeSideAndAddToStoreShouldNotAddToOppositeStore() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(8);
        initBoard = boardRepository.save(initBoard);

        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(8);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = move(moveRequest);


        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(5).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(0).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(5).build(), 1, PitDTO.builder().amount(5).build(), 2, PitDTO.builder().amount(5).build(), 3, PitDTO.builder().amount(5).build(), 4, PitDTO.builder().amount(5).build(), 5, PitDTO.builder().amount(5).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 1, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(5).build(), 2, Pit.builder().amount(5).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(5).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());

    }

    @Test
    void gameShouldBeFinishIfSideIsEmpty() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);

        initBoard.getPlayerBoards().get(playerRound).getPits().get(0).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(1).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(2).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(3).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(1);
        initBoard = boardRepository.save(initBoard);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = move(moveRequest);

        Map<Integer, PitDTO> emptyPitsPitDTOMap = Map.of(0, PitDTO.builder().amount(0).build(), 1, PitDTO.builder().amount(0).build(), 2, PitDTO.builder().amount(0).build(), 3, PitDTO.builder().amount(0).build(), 4, PitDTO.builder().amount(0).build(), 5, PitDTO.builder().amount(0).build());
        verifyBoardDTO(initBoard, boardDTO, null, GameStatus.FINISH, emptyPitsPitDTOMap, emptyPitsPitDTOMap, 25, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> emptyPits = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(0).build(), 2, Pit.builder().amount(0).build(), 3, Pit.builder().amount(0).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(0).build());
        verifyBoard(boardOptional.get(), GameStatus.FINISH, emptyPits, emptyPits, 25, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isNull();
    }


    @Test
    void moveWithNextRoundRewardShouldDoNotChangePlayerRound() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard = boardRepository.save(initBoard);
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(2).build();

        BoardDTO boardDTO = move(moveRequest);


        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(0).build(), 3, PitDTO.builder().amount(5).build(), 4, PitDTO.builder().amount(5).build(), 5, PitDTO.builder().amount(5).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound, GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 1, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(0).build(), 3, Pit.builder().amount(5).build(), 4, Pit.builder().amount(5).build(), 5, Pit.builder().amount(5).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound);
    }

    @Test
    void moveShouldTakeOppositePitRewardWhenLastStoneIsInEmptyHomeAndOppositeIsNotEmpty() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(1);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        initBoard = boardRepository.save(initBoard);
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(4).build();

        BoardDTO boardDTO = move(moveRequest);


        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(0).build(), 5, PitDTO.builder().amount(0).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(0).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 5, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 5, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void moveShouldNotTakeOppositePitRewardWhenLastStoneIsInEmptyHomeAndOppositeIsEmpty() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(4).setAmount(1);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound.oppositeSide()).getPits().get(0).setAmount(0);
        initBoard = boardRepository.save(initBoard);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(4).build();

        BoardDTO boardDTO = move(moveRequest);


        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(0).build(), 5, PitDTO.builder().amount(1).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(0).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 0, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(0).build(), 5, Pit.builder().amount(1).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(0).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 0, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void moveShouldNotTakePitRewardWhenLastStoneIsInEmptyOppositeHome() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.getPlayerBoards().get(playerRound.oppositeSide()).getPits().get(1).setAmount(0);
        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(3);
        initBoard = boardRepository.save(initBoard);

        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(5).build();

        BoardDTO boardDTO = move(moveRequest);

        Map<Integer, PitDTO> boardOnePitDTOMap = Map.of(0, PitDTO.builder().amount(4).build(), 1, PitDTO.builder().amount(4).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(0).build());
        Map<Integer, PitDTO> boardTwoPitDTOMap = Map.of(0, PitDTO.builder().amount(5).build(), 1, PitDTO.builder().amount(1).build(), 2, PitDTO.builder().amount(4).build(), 3, PitDTO.builder().amount(4).build(), 4, PitDTO.builder().amount(4).build(), 5, PitDTO.builder().amount(4).build());
        verifyBoardDTO(initBoard, boardDTO, playerRound.next(), GameStatus.IN_PROGRESS, boardOnePitDTOMap, boardTwoPitDTOMap, 1, 0, 1L);

        Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());
        assertThat(boardOptional).isPresent();
        Map<Integer, Pit> boardOnePit = Map.of(0, Pit.builder().amount(4).build(), 1, Pit.builder().amount(4).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(0).build());
        Map<Integer, Pit> boardTwoPit = Map.of(0, Pit.builder().amount(5).build(), 1, Pit.builder().amount(1).build(), 2, Pit.builder().amount(4).build(), 3, Pit.builder().amount(4).build(), 4, Pit.builder().amount(4).build(), 5, Pit.builder().amount(4).build());
        verifyBoard(boardOptional.get(), GameStatus.IN_PROGRESS, boardOnePit, boardTwoPit, 1, 0);
        assertThat(boardOptional.get()).extracting(Board::getPlayerRound).isEqualTo(playerRound.next());
    }

    @Test
    void shouldThrowExceptionIfDoNotFindBoard() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId("Invalid_Id").playerNumber(playerRound).version(0L).index(4).build();
        badRequestMove(moveRequest, "Board not valid");
    }


    @Test
    void shouldThrowExceptionIfTheGameIsFinish() throws Exception {
        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);
        initBoard.setStatus(GameStatus.FINISH);
        initBoard = boardRepository.save(initBoard);
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(4).build();
        badRequestMove(moveRequest, "Game is already finished");
    }

    @Test
    void shouldThrowExceptionIfTheHomeIsEmpty() throws Exception {


        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);

        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);

        initBoard = boardRepository.save(initBoard);
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound).version(0L).index(5).build();
        badRequestMove(moveRequest, "selected pit can not be empty");

    }

    @Test
    void shouldThrowExceptionIfTheHomeIsIsAnotherPlayerRound() throws Exception {

        PlayerNumber playerRound = PlayerNumber.ONE;
        Board initBoard = initBoard(null, playerRound, FIRST_PLAYER, SECOND_PLAYER);

        initBoard.getPlayerBoards().get(playerRound).getPits().get(5).setAmount(0);

        initBoard = boardRepository.save(initBoard);
        MoveRequestDTO moveRequest = MoveRequestDTO.builder().boardId(initBoard.getId()).playerNumber(playerRound.oppositeSide()).version(0L).index(5).build();
        badRequestMove(moveRequest, "This another player round!");
    }

    private BoardDTO move(MoveRequestDTO moveRequest) throws Exception {
        String requestBody = objectMapper.writeValueAsString(moveRequest);
        BoardDTO boardDTO = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/game/board/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString(), BoardDTO.class);
        return boardDTO;
    }

    private void badRequestMove(MoveRequestDTO moveRequest, String errorMessage) throws Exception {
        String requestBody = objectMapper.writeValueAsString(moveRequest);
        String message = mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/game/board/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();
        assertThat(message).isNotNull().isEqualTo(errorMessage);
    }
}


