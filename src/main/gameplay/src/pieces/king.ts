import { Board } from "../board.js";
import { Color } from "../color.js";
import { Square } from "../square.js";
import { Piece } from "./piece.js";

export class King implements Piece {
    private static offsets: Array<[number, number]> = [
        [-1, 1],
        [0, 1],
        [1, 1],
        [-1, 0],
        [1, 0],
        [-1, -1],
        [0, -1],
        [1, -1],
    ];
    
    readonly pieceName = 'k';
    constructor(readonly color: Color) {}

    getControlArea(pos: Square, board: Board): Square[] {
        let moves: Square[] = [];

        King.offsets.forEach(offset => {
            const potentialMove: Square = {row: pos.row + offset[1], col: pos.col + offset[0]};

            if (board.inBoundsSquare(potentialMove)) {
                moves.push(potentialMove);
            }
        });
        
        return moves;
    }
    
    notifyMove(): void {}


    getMoves(pos: Square, board: Board): Square[] {
        const controls = this.getControlArea(pos, board);
        
        // remove squares where same color.
        const diffColor = controls.filter(square => !board.occupiedBy(square.row, square.col, this.color));
        diffColor.push(...board.getCastleMoves(this.color));


        // remove squares that put self in check.
        const validMoves = diffColor.filter(square => !board.putsInCheck(pos, square, this.color));

        return validMoves;
    }
}
