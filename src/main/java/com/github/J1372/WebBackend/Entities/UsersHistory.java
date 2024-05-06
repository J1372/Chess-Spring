package com.github.J1372.WebBackend.Entities;

import com.github.J1372.WebBackend.Dto.UserStats;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@IdClass(UsersHistoryId.class)
public class UsersHistory {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private User a;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private User b;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int user1Wins = 0;
    @Column(nullable = false)
    @ColumnDefault("0")
    private int draws  = 0;
    @Column(nullable = false)
    @ColumnDefault("0")
    private int user2Wins = 0;

    protected UsersHistory() {}
    public UsersHistory(User user1, User user2) {
        if (user1.getId() > user2.getId()) {
            a = user1;
            b = user2;
        } else {
            a = user2;
            b = user1;
        }
    }


    public void addWin(User user) {
        if (user.equals(a)) {
            user1Wins++;
        } else if (user.equals(b)) {
            user2Wins++;
        }
    }

    public void addDraw() {
        draws++;
    }

    public UserStats getFromPerspectiveOf(User perspective) {
        if (perspective.equals(a)) {
            return new UserStats(user1Wins, draws, user2Wins);
        } else if (perspective.equals(b)) {
            return new UserStats(user2Wins, draws, user1Wins);
        } else {
            return null;
        }
    }
}
