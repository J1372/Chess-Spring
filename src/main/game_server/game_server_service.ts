import { Game, GamePost, Move, Color } from '../gameplay/index.js';
import DbLayer from './db_layer.js';

export enum MoveResult {
    REJECTED = 0,
    ACCEPTED,
    CHECKMATE
}

export default class GameService {

    constructor(private db: DbLayer){}

    private activeGames = new Map<string, Game>;

    public getActiveGameCount = () => this.activeGames.size;

    public tryMove(uuid: string, player: string, move: Move): MoveResult {
        const game = this.activeGames.get(uuid);

        // No game with uuid that is currently being played.
        if (!game) return MoveResult.REJECTED;

        // Check if user can actually move where they want to go.
        if (!game.canMove(player, move)) return MoveResult.REJECTED;

        game.move(move);

        // Move was accepted, check if move caused checkmate.

        if (game.userWon(player)) {
            this.endGame(game);
            this.db.storeGame(game, game.getColor(player), 'm', new Date());

            return MoveResult.CHECKMATE;
        } else {
            return MoveResult.ACCEPTED;
        }
    }

    public tryResign(uuid: string, player: string): boolean {
        const game = this.activeGames.get(uuid);
        
        if (game && game.isPlayer(player)) {
            const winner = Color.opposite(game.getColor(player));
            this.endGame(game);
            this.db.storeGame(game, winner, 'r', new Date());

            return true;
        } else {
            return false;
        }
    }

    public endGame(game: Game) {
        this.activeGames.delete(game.uuid);
    }

    public createGame(uuid: string, host: string, otherPlayer: string, hostPrefer: Color | undefined, gameType: string) {
        const game = Game.fromPost(new GamePost(uuid, host, hostPrefer), otherPlayer);
        this.activeGames.set(uuid, game);
    }

    public getActiveGame = (uuid: string) => this.activeGames.get(uuid);
};
