package com.github.J1372.WebBackend.Dto;

public enum GameResultCause {
    MATE,
    RESIGN;

    public static GameResultCause fromChar(char character) {
        return switch (character) {
            case 'm' -> GameResultCause.MATE;
            case 'r' -> GameResultCause.RESIGN;
            default -> null;
        };
    }
}