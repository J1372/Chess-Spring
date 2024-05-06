package com.github.J1372.WebBackend.Services;

import com.github.J1372.WebBackend.Dto.UserStats;
import com.github.J1372.WebBackend.Dto.CompletedGameDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    enum UserRegStatus {
        CREATED,
        EXISTS,
        OTHER_ERROR
    }

    UserRegStatus create(String user, String pass);
    boolean exists(String user);
    void delete(String user);

    /**
     * @return A registration error message pertaining to the contents of the given username and password,
     * otherwise an empty string.
     */
    String getRegistrationErrors(String user, String pass);

    boolean isValidLogin(String user, String pass);

    UserStats getStats(String user);
    List<CompletedGameDto> getRecentGames(String user, int amount);

    /**
     *
     * @return If previously played against each other,
     * the win/draw/loss history between user1 and user2, from the perspective of user1.
     * Otherwise, null.
     */
    UserStats getHistoryBetween(String user1, String user2);

}
