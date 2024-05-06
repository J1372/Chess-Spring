package com.github.J1372.WebBackend.Services;

import com.github.J1372.WebBackend.Dto.Color;
import com.github.J1372.WebBackend.Dto.GameCreationDetails;
import com.github.J1372.WebBackend.Dto.GamePostDto;
import com.github.J1372.WebBackend.Entities.OpenGame;
import com.github.J1372.WebBackend.Entities.User;
import com.github.J1372.WebBackend.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final UsersHistoryRepository usersHistoryRepository;
    private final UserRepository userRepository;

    private final OpenGameRepository openGameRepository;


    public GameServiceImpl(GameRepository gameRepository, UsersHistoryRepository usersHistoryRepository,
                           UserRepository userRepository, OpenGameRepository openGameRepository) {
        this.userRepository = userRepository;
        this.usersHistoryRepository = usersHistoryRepository;
        this.gameRepository = gameRepository;
        this.openGameRepository = openGameRepository;
    }

    @Override
    @Transactional
    public GameCreationDetails tryJoin(UUID uuid, String player) {
        return openGameRepository.findById(uuid).map(game -> {
            if (game.isHost(player)) {
                return null;
            }

            openGameRepository.deleteById(uuid);
            return new GameCreationDetails(
                    uuid.toString(),
                    game.getCreator().getUsername(),
                    Color.fromChar(game.getCreatorPlayAs()),
                    game.getGameType()
            );
        }).orElse(null);
    }

    @Override
    public String createGame(String host, Color hostPrefer) {
        User user = userRepository.findByUsername(host);
        if (user == null) return null;

        UUID uuid = UUID.randomUUID();
        openGameRepository.save(new OpenGame(
                uuid,
                user,
                hostPrefer.toString().toLowerCase().charAt(0),
                "Standard"
        ));

        return uuid.toString();
    }

    @Override
    public List<GamePostDto> getOpenGames() {
        return openGameRepository.findAll().stream().map(openGameEntity -> new GamePostDto(
                openGameEntity.getUuid().toString(),
                openGameEntity.getCreator().getUsername(),
                Color.fromChar(openGameEntity.getCreatorPlayAs()),
                openGameEntity.getCreated(),
                openGameEntity.getGameType()
        )).toList();
    }

}
