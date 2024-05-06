import { Game, Color } from '../gameplay/index.js';

export default interface DbLayer {

    storeGame(game: Game, winnerColor: Color, dueTo: string, ended: Date): Promise<void>;
    getLoggedInUser(token: string): Promise<string | null>;
    
};
