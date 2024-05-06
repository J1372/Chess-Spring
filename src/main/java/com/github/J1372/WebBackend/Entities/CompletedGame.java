package com.github.J1372.WebBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class CompletedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private User white;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private User black;

    @Column(nullable = false, updatable = false)
    private Character result;
    @Column(nullable = false, updatable = false)
    private Character dueTo;

    @Column(nullable = false, updatable = false)
    private Instant started;
    @Column(nullable = false, updatable = false)
    private Instant ended;

    @Column(nullable = false)
    private String gameType;

    public CompletedGame() {}

}
