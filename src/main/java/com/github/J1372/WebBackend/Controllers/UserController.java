package com.github.J1372.WebBackend.Controllers;

import com.github.J1372.WebBackend.Dto.UserStats;
import com.github.J1372.WebBackend.Dto.CompletedGameDto;
import com.github.J1372.WebBackend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{user}/stats")
    public ResponseEntity<UserStats> getUserStats(@PathVariable String user) {
        UserStats stats = userService.getStats(user);
        if (stats != null) {
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{user}/recent-games")
    public List<CompletedGameDto> getRecentGames(@PathVariable String user, @RequestParam(value = "max", defaultValue = "10") int amount) {
        int amountActualCapped = Math.min(amount, 10);
        return userService.getRecentGames(user, amountActualCapped);
    }

    @GetMapping("/{user1}/history/{user2}")
    public ResponseEntity<UserStats> getUsersHistory(@PathVariable String user1, @PathVariable String user2) {
        UserStats history = userService.getHistoryBetween(user1, user2);
        if (history != null) {
            return new ResponseEntity<>(history, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
