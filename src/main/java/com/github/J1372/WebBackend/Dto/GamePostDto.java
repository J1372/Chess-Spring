package com.github.J1372.WebBackend.Dto;

import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class GamePostDto {
    public String uuid;
    public String host;
    public Color hostPlayAs;
    public Instant posted;
    public String gameType;
}
