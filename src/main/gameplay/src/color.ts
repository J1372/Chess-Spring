export enum Color {
    WHITE,
    BLACK,
}

export namespace Color {
    export function opposite(color: Color) {
        return color === Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    export function toString(color: Color) {
        if (color === Color.WHITE) {
            return 'White';
        } else if (color === Color.BLACK) {
            return 'Black';
        } else {
            return '';
        }
    }
}