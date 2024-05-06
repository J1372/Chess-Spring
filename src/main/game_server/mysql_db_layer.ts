import DbLayer from "./db_layer.js";
import mysql, { ResultSetHeader, RowDataPacket } from 'mysql2/promise';
import { Game, Color } from '../gameplay/index.js'

export default class MySqlDBLayer implements DbLayer {

    private pool: mysql.Pool;

    constructor(url: string, user: string, pass: string, db_name: string) {
        this.pool = mysql.createPool({
            host: url,
            user: user,
            password: pass,
            database: db_name,
            timezone: 'UTC',
        });
    }

    public async getLoggedInUser(token: string): Promise<string | null> {
        const [res, _] = await this.pool.query<RowDataPacket[]>('select PRINCIPAL_NAME from SPRING_SESSION where SESSION_ID = ?', [token]);
        if (res.length === 0) {
            return null;
        } else {
            return res[0].PRINCIPAL_NAME;
        }
    }

    private async updateHistories(connection: mysql.Connection, winnerId: number, loserId: number) {
        // use prepared statement in case user-defined string ever accidentally passed as an id.
        const dupKeyUpdateStmt = winnerId < loserId ? 'user1wins = user1wins + 1' : 'user2wins = user2wins + 1';
        const initialInsertVals = winnerId < loserId ? [winnerId, loserId, 1, 0, 0] : [loserId, winnerId, 0, 0, 1];
        const sql = 
        `
            insert into users_history (a_id, b_id, user1wins, draws, user2wins)
            values (?, ?, ?, ?, ?)
            on duplicate key update ${dupKeyUpdateStmt}
        `;

        return connection.execute(sql, initialInsertVals);
    }

    private async addUserGame(connection: mysql.Connection, game: Game, result: string, dueTo: string, ended: Date, whiteId: number, blackId: number) {
        const sql =
        `
            insert into completed_game (uuid, white_id, black_id, result, due_to, started, ended, game_type)
            values (UUID_TO_BIN(?), ?, ?, ?, ?, ?, ?, ?)
        `;

        const [res, _] = await connection.execute<ResultSetHeader>(sql, [game.uuid, whiteId, blackId, result, dueTo, game.started, ended, game.gameType]);
        
        /*
        Supposedly can insert multiple rows with one execute statement but gives error.
        
        const insertRows = [
            [whiteId, res.insertId],
            [blackId, res.insertId]
            
        ];
        return connection.execute<ResultSetHeader>('insert into user_past_games (user_id, past_games_id) values ?', [insertRows]);
        
        */

        await connection.execute<ResultSetHeader>('insert into user_past_games (user_id, past_games_id) values (?, ?)', [whiteId, res.insertId]);
        return connection.execute<ResultSetHeader>('insert into user_past_games (user_id, past_games_id) values (?, ?)', [blackId, res.insertId]);
    }

    public async storeGame(game: Game, winnerColor: Color, dueTo: string, ended: Date): Promise<void> {
        let result: string;
        let winner: string;
        let loser: string;
        if (winnerColor === Color.WHITE) {
            result = 'w';
            winner = game.white;
            loser = game.black;
        } else {
            result = 'b';
            winner = game.black;
            loser = game.white;
        }

        const connection = await this.pool.getConnection();
        try {
            await connection.beginTransaction();

            const [results, _] = await connection.query<RowDataPacket[]>('select id, username from user where username in (?, ?)', [winner, loser]);
            let winnerId: number;
            let loserId: number;
            if (results[0].username == winner) {
                winnerId = Number.parseInt(results[0].id);
                loserId = Number.parseInt(results[1].id);
            } else {
                winnerId = Number.parseInt(results[1].id);
                loserId = Number.parseInt(results[0].id);
            }

            const [whiteId, blackId] = winnerColor === Color.WHITE ? [winnerId, loserId] : [loserId, winnerId];

            await connection.execute('update user set wins = wins + 1 where id = ?', [winnerId]),
            await connection.execute('update user set losses = losses + 1 where id = ?', [loserId]),
            await this.updateHistories(connection, winnerId, loserId),
            await this.addUserGame(connection, game, result, dueTo, ended, whiteId, blackId)

            await connection.commit();
        } catch (message) {
            await connection.rollback();
            throw message;
        } finally {
            connection.release();
        }
    }

}
