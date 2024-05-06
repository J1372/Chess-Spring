package com.github.J1372.WebBackend.Dto;

import lombok.Getter;

@Getter
public class UsersHistoryPerspective {
    String dbPerspective;
    UserStats stats;

    public UsersHistoryPerspective(String perspective, int wins, int draws, int losses) {
        dbPerspective = perspective;
        stats = new UserStats(wins, draws, losses);
    }
}
