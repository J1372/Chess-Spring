package com.github.J1372.WebBackend.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStats {
    private int wins;
    private int draws;
    private int losses;

    public UserStats() {
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
    }

    public UserStats(int wins, int draws, int losses) {
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
    }
}
