package com.bol.mancala.rest;

import com.bol.mancala.entity.Player;
import com.bol.mancala.entity.enumeration.GameStatus;
import com.bol.mancala.repository.PlayerRepository;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameControllerTest {

    private static Player FIRST_PLAYER_ID;
    private static final String FIRST_PLAYER_NAME = "p1";
    private static Player SECOND_PLAYER_ID;
    private static final String SECOND_PLAYER_NAME = "p2";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PlayerRepository playerRepository;


    @BeforeAll
    public void setup() {
        FIRST_PLAYER_ID = playerRepository.save(Player.builder().name(FIRST_PLAYER_NAME).build());
        SECOND_PLAYER_ID = playerRepository.save(Player.builder().name(SECOND_PLAYER_NAME).build());

    }


    @Test
    void testCreateBoard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.getId())
                        .param("secondPlayerId", SECOND_PLAYER_ID.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.playerId").value(FIRST_PLAYER_ID.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.storeAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.0.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.1.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.2.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.3.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.4.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.ONE.pits.5.amount").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerBoards.TWO.playerId").value(SECOND_PLAYER_ID.getId()))
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
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.getId())
                        .param("secondPlayerId", FIRST_PLAYER_ID.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateBoardWithWrongPlayerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/game/create-board")
                        .param("firstPlayerId", FIRST_PLAYER_ID.getId())
                        .param("secondPlayerId", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
