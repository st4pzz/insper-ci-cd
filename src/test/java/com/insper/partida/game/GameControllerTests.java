package com.insper.partida.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insper.partida.equipe.dto.TeamReturnDTO;
import com.insper.partida.game.dto.EditGameDTO;
import com.insper.partida.game.dto.GameReturnDTO;

@ExtendWith(MockitoExtension.class)
public class GameControllerTests {
    
    MockMvc mockMvc;

    @InjectMocks
    GameController gameController;

    @Mock
    GameService gameService;
    
    @BeforeEach
    void setup()  {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(gameController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getGame() throws Exception {
        GameReturnDTO game = new GameReturnDTO();
        game.setIdentifier("game1");

        Mockito.when(gameService.getGame("game1")).thenReturn(game);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/game/game1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ObjectMapper om = new ObjectMapper();

        String resp = result.getResponse().getContentAsString();
        Assertions.assertEquals(om.writeValueAsString(game), resp);
    }

    @Test
    void getListGames() throws Exception {
        List<GameReturnDTO> games = new ArrayList<>();

        TeamReturnDTO team1 = new TeamReturnDTO();
        team1.setIdentifier("Kansas");
        TeamReturnDTO team2 = new TeamReturnDTO();
        team2.setIdentifier("Dallas");

        GameReturnDTO game1 = new GameReturnDTO();
        game1.setIdentifier("game1");
        game1.setHome(team1);
        game1.setAway(team2);
        game1.setAttendance(10000);
        games.add(game1);

        PageRequest paginacao = PageRequest.of(0, 10);
        Mockito.when(gameService.listGames(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                .thenReturn((new PageImpl<>(games, paginacao, games.size())));

        MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/game")
                .param("home", "Kansas")
                .param("away", "Dallas")
                .param("attendance", "10000"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        ObjectMapper om = new ObjectMapper();

        Page<GameReturnDTO> gamePage = new PageImpl<>(games, paginacao, 2);
        String resp = result.getResponse().getContentAsString();
        Assertions.assertEquals(om.writeValueAsString(gamePage), resp);
    }

    @Test
    void testChangeGame() throws Exception {
        EditGameDTO gameEdit = new EditGameDTO();
        gameEdit.setScoreHome(1);
        gameEdit.setScoreAway(2);
        gameEdit.setAttendance(50000);
    
        GameReturnDTO game = new GameReturnDTO();
        game.setIdentifier("game1");
  
        Mockito.when(gameService.editGame(eq("game1"), any(EditGameDTO.class))).thenReturn(game);
    
        ObjectMapper om = new ObjectMapper();
        MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.post("/game/game1")
                .contentType(MediaType.APPLICATION_JSON) 
                .content(om.writeValueAsString(gameEdit))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    
    
        String resp = result.getResponse().getContentAsString();
        Assertions.assertEquals(om.writeValueAsString(game), resp);
    }
}
