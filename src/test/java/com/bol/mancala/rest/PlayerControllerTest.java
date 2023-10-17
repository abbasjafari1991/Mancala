package com.bol.mancala.rest;

import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.service.dto.PlayerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void testCreatePlayer() throws Exception {
        // Create a sample PlayerDTO
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("Carol");


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/player")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(playerDTO))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Carol"));
    }

    @Test
    void testGetPlayerById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/player/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Carol"));
    }

    @Test
    void testUpdatePlayer() throws Exception {
        // Create a sample PlayerDTO
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setName("Bob");


        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/player/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Bob"));
    }

    @Test
    void testDeletePlayer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/player/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertEquals(playerRepository.count(), 0);
    }
}
