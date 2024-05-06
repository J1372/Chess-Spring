package com.github.J1372.WebBackend.Entities;

import com.github.J1372.WebBackend.Dto.UserStats;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false, unique = true)
    private String username;

    @Column(length = 80, nullable = false)
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer wins;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer draws;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer losses;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<CompletedGame> pastGames;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<OpenGame> openGames;


    protected User() {}

    public User(String user, String pass) {
        username = user;
        password = pass;
        wins = 0;
        draws = 0;
        losses = 0;
    }

    public UserStats getUserStats() {
        return new UserStats(wins, draws, losses);
    }

    public boolean equals(Object o) {
        if (o instanceof User other) {
            return id.equals(other.id);
        } else {
            return false;
        }
    }

}
