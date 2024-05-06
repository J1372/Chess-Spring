package com.github.J1372.WebBackend.Dto;

public enum Color {
    WHITE,
    BLACK,
    EITHER;

    public static Color fromChar(char character) {
        return switch (character) {
            case 'w' -> Color.WHITE;
            case 'b' -> Color.BLACK;
            default -> Color.EITHER;
        };
    }
}
