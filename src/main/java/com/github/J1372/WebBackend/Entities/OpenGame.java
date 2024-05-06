package com.github.J1372.WebBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class OpenGame {

    @Id
    private UUID uuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private User creator;

    @Column(nullable = false)
    private char creatorPlayAs;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant created;

    @Column(nullable = false)
    private String gameType;

    public OpenGame() {}

    public OpenGame(UUID uuid, User creator, char creatorPlayAs, String gameType) {
        this.uuid = uuid;
        this.creator = creator;
        this.creatorPlayAs = creatorPlayAs;
        this.gameType = gameType;
    }


    public boolean isHost(String player) {
        return creator.getUsername().equals(player);
    }
}
