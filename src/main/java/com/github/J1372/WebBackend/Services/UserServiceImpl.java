package com.github.J1372.WebBackend.Services;

import com.github.J1372.WebBackend.Dto.Color;
import com.github.J1372.WebBackend.Dto.GameResultCause;
import com.github.J1372.WebBackend.Entities.CompletedGame;
import com.github.J1372.WebBackend.Entities.User;
import com.github.J1372.WebBackend.Dto.UserStats;
import com.github.J1372.WebBackend.Dto.CompletedGameDto;
import com.github.J1372.WebBackend.Repositories.UserRepository;
import com.github.J1372.WebBackend.Repositories.UsersHistoryRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UsersHistoryRepository usersHistoryRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UsersHistoryRepository usersHistoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.usersHistoryRepository = usersHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserRegStatus create(String user, String pass) {
        try {
            userRepository.save(new User(user, passwordEncoder.encode(pass)));
            return UserRegStatus.CREATED;
        } catch (DataAccessException e) {
            return UserRegStatus.EXISTS;
        } catch (Exception e) {
            return UserRegStatus.OTHER_ERROR;
        }
    }

    @Override
    public boolean exists(String user) {
        return userRepository.existsByUsername(user);
    }

    @Override
    public void delete(String user) {
        userRepository.deleteByUsername(user);
    }

    @Override
    public String getRegistrationErrors(String user, String pass) {
        if (user.length() < 3 || user.length() > 16) {
            return "Username length must be in the range [3, 16]";
        }

        char firstChar = Character.toLowerCase(user.charAt(0));

        if (firstChar < 'a' || firstChar > 'z') {
            return "Username must start with a letter.";
        }

        if (pass.length() == 0) {
            return "No password entered.";
        }

        if (pass.length() < 7) {
            return "Password too short (min = 7 characters)";
        }

        if (pass.length() > 72) {
            return "Password too long (max = 72 characters)";
        }

        return "";
    }

    @Override
    public boolean isValidLogin(String username, String pass) {
        User user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(pass, user.getPassword());
    }

    @Override
    public UserStats getStats(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getUserStats();
        } else {
            return null;
        }
    }

    @Override
    public List<CompletedGameDto> getRecentGames(String username, int amount) {
        List<CompletedGame> gameEntities = userRepository.findRecentGames(username, amount);
        return gameEntities.stream().map(gameEntity ->
                new CompletedGameDto(gameEntity.getUuid().toString(),
                    gameEntity.getWhite().getUsername(),
                    gameEntity.getBlack().getUsername(),
                    Color.fromChar(gameEntity.getResult()),
                    GameResultCause.fromChar(gameEntity.getDueTo()),
                    gameEntity.getStarted(),
                    gameEntity.getEnded(),
                    gameEntity.getGameType()
                    )
        ).toList();
    }

    @Override
    public UserStats getHistoryBetween(String username1, String username2) {
        return usersHistoryRepository.findHistoryWith(username1, username2);
    }
}
