package com.bol.mancala.rest;

import com.bol.mancala.entity.Player;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class GameControllerTest {

    private static final Long FIRST_PLAYER_ID = 1L;
    private static final String FIRST_PLAYER_NAME = "p1";
    private static final Long SECOND_PLAYER_ID = 2L;
    private static final String SECOND_PLAYER_NAME = "p2";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PlayerRepository playerRepository;


    @BeforeAll
    public void setup() {
        playerRepository.save(Player.builder().name(FIRST_PLAYER_NAME).build());
        playerRepository.save(Player.builder().name(SECOND_PLAYER_NAME).build());

    }


    @Test
    void testCreateBoard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.toString())
                        .param("secondPlayerId", SECOND_PLAYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.playerId").value(FIRST_PLAYER_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.storeAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[0].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[0].index").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[1].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[1].index").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[2].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[2].index").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[3].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[3].index").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[4].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[4].index").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[5].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits[5].index").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.playerId").value(SECOND_PLAYER_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.storeAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[0].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[0].index").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[1].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[1].index").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[2].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[2].index").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[3].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[3].index").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[4].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[4].index").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[5].amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.pits[5].index").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerRound").isNotEmpty());
    }

    @Test
    void testCreateBoardWithSamePlayerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.toString())
                        .param("secondPlayerId", FIRST_PLAYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateBoardWithWrongPlayerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.toString())
                        .param("secondPlayerId", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateBoardWithTextPlayerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.toString())
                        .param("secondPlayerId", "asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
