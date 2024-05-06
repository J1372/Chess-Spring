package com.github.J1372.WebBackend.Controllers;

import com.github.J1372.WebBackend.Dto.Color;
import com.github.J1372.WebBackend.Dto.GameCreationDetails;
import com.github.J1372.WebBackend.Dto.GamePostDto;
import com.github.J1372.WebBackend.Config.UserSession;
import com.github.J1372.WebBackend.Services.GameService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private GameService gameService;

    private final String fetchURL;
    private final String gameToken;

    public GameController(GameService gameService) {
        this.gameService = gameService;

        gameToken = System.getenv("GAME_SERVER_TOKEN");
        if (gameToken == null) throw new RuntimeException("Game server token not specified in env.");

        String proxy = System.getenv("GAME_SERVER_PROXY");
        if (proxy == null) throw new RuntimeException("Game server proxy not specified in env.");

        fetchURL = "http://" + proxy + "/start-active-game";
    }


    @Getter
    @Setter
    static class CreateGameBody {
        Color color;
    }

    @PostMapping("")
    private ResponseEntity<String> createGame(@AuthenticationPrincipal UserSession user, @RequestBody CreateGameBody body) {
        String uuid = gameService.createGame(user.getUsername(), body.getColor());
        if (uuid != null) {
            return new ResponseEntity<>(uuid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/open-games")
    private List<GamePostDto> getOpenGames() {
        return gameService.getOpenGames();
    }

    @PutMapping("/{uuid}")
    private ResponseEntity<?> tryJoinGame(@AuthenticationPrincipal UserSession user, @PathVariable UUID uuid) {
        String userJoining = user.getUsername();
        GameCreationDetails gameDetails = gameService.tryJoin(uuid, userJoining);

        if (gameDetails == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Game successfully joined, need to send request to socket service.

        RestTemplate restTemplate = new RestTemplate();

        @Getter
        @Setter
        class JoinGameProxyMsg {
            String auth;
            GameCreationDetails game;
            String userJoining;
        }
        var msg = new JoinGameProxyMsg();
        msg.setAuth(gameToken);
        msg.setGame(gameDetails);
        msg.setUserJoining(userJoining);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JoinGameProxyMsg> entity = new HttpEntity<>(msg, headers);

        String finalURL = fetchURL + "?gameUUID=" + uuid;
        ResponseEntity<String> result = restTemplate.postForEntity(finalURL, entity, String.class);

        if (result.getStatusCode() == HttpStatus.OK) {
            System.out.println("Server accepted game creation request.");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Server rejected game creation request.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
