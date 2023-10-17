package com.bol.mancala.rest;

import com.bol.mancala.entity.Player;
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
@ActiveProfiles("dev")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
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
