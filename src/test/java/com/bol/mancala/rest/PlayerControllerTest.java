package com.bol.mancala.rest;

import com.bol.mancala.service.PlayerService;
import com.bol.mancala.service.dto.PlayerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Test
    public void testCreatePlayer() throws Exception {
        // Create a sample PlayerDTO
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setName("Carol");

        // Mock the service's save method
        when(playerService.save(any(PlayerDTO.class))).thenReturn(playerDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/player")
                        .contentType("application/json")
                        .content("{\"name\":\"Carol\"}")
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Carol"));
    }

    // Similar tests for other controller endpoints (GET, PUT, DELETE) can be added.
}
