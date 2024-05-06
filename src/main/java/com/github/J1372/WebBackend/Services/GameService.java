package com.github.J1372.WebBackend.Services;

import com.github.J1372.WebBackend.Dto.Color;
import com.github.J1372.WebBackend.Dto.GameCreationDetails;
import com.github.J1372.WebBackend.Dto.GamePostDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface GameService {

    GameCreationDetails tryJoin(UUID uuid, String player);
    String createGame(String host, Color hostPrefer);

    List<GamePostDto> getOpenGames();
    
}
