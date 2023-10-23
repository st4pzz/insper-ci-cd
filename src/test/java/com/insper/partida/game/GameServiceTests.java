package com.insper.partida.game;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.insper.partida.game.Game;
import com.insper.partida.game.GameRepository;
import com.insper.partida.game.GameService;
import com.insper.partida.game.dto.GameReturnDTO;
import com.insper.partida.game.dto.SaveGameDTO;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

    @InjectMocks
    GameService gameService;

    @Mock
    GameRepository gameRepository;

    @Test
    void getGameByIdentifier(){
        Game game = getGame();
        Mockito.when(gameRepository.findByIdentifier("teste")).thenReturn(game);
        GameReturnDTO response = gameService.getGame("teste");
        Assertions.assertEquals(game.getIdentifier(),response.getIdentifier());
    }

    @Test
    void getGameByTeam(){ 
        List<Game> games = new ArrayList<>();
        games.add(getGame());
        Mockito.when(gameRepository.findByHomeOrAway("Kansas City Chiefs", "Kansas City Chiefs")).thenReturn(games);
        List<Game> response = gameService.getGameByTeam("Kansas City Chiefs");
        Assertions.assertEquals(games.get(0).getIdentifier(), response.get(0).getIdentifier());
    }

    private static Game getGame() {
        Game game = new Game();
        game.setIdentifier("teste");
        game.setHome("Kansas City Chiefs");
        game.setAway("Dallas Cowboys");
        return game;
    }
}
