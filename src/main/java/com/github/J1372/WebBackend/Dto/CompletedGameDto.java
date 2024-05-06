package com.github.J1372.WebBackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedGameDto {
    private String uuid;

    private String white;
    private String black;
    private Color result;
    private GameResultCause dueTo;

    private Instant started;
    private Instant ended;
    private String gameType;
}
