import * as http from 'http';
import { Server, Socket } from 'socket.io';
import { Color, Game, Move } from '../gameplay/index.js';
import express, { NextFunction } from 'express';
import GameService, { MoveResult } from './game_server_service.js';
import MySqlDB from './mysql_db_layer.js';

if (process.env.DB_HOST == undefined ||
    process.env.DB_USER == undefined ||
    process.env.DB_PASS == undefined ||
    process.env.DB_NAME == undefined) {
    throw new Error('Missing at least one env variable [DB_HOST, DB_USER, DB_PASS, DB_NAME].');
}

const dbLayer = new MySqlDB(
    process.env.DB_HOST,
    process.env.DB_USER,
    process.env.DB_PASS,
    process.env.DB_NAME,
);

const gameService = new GameService(dbLayer);

const app = express();
const server = http.createServer(app);
const io = new Server(server);

const port = process.env.PORT || 4000;

app.post('/start-active-game', express.json(), (req, res) => {
    const data = req.body;
    console.log(data);

    if (data.auth === process.env.GAME_SERVER_TOKEN) {
        const game = data.game;

        let hostPlaysAsColor;
        if (game.hostPlaysAs === 'WHITE') {
            hostPlaysAsColor = Color.WHITE;
        } else if (game.hostPlaysAs === 'BLACK') {
            hostPlaysAsColor = Color.BLACK;
        } else {
            hostPlaysAsColor = undefined;
        }

        gameService.createGame(game.uuid, game.creator, data.userJoining, hostPlaysAsColor, game.gameType);
        console.log(`Game created, game count: ${gameService.getActiveGameCount()}.`)

        res.sendStatus(200);
    } else {
        console.log('Attempted game creation with invalid auth token.')
        res.sendStatus(403);
    }
});

function establishGameViewerConnection(client: Socket, gameUUID: string): Game | null {
    const game = gameService.getActiveGame(gameUUID);

    if (game !== undefined) {
        // send gamestate to client
        client.emit('game-state', JSON.stringify(game));

        // add socket to game list
        client.join(gameUUID);
        return game;
    } else {
        return null;
    }
}


function handleGameClose(game: Game) {
    // maybe send a final reason for game ending before closing.
    io.in(game.uuid).disconnectSockets();
}

function deserializeMove(json: any): Move | null {
    const isValidSquare = (json: any) => json != null 
                                        && json.row != null && json.col != null
                                        && typeof(json.row) === 'number' && typeof(json.col) === 'number';

    const isValidMove = json != undefined && isValidSquare(json.from) && isValidSquare(json.to) && typeof(json.promotedTo) === 'string';
    if (isValidMove) {
        return { from: json.from, to: json.to, promotedTo: json.promotedTo }; // strips other properties from json.
    } else {
        return null;
    }
}

function handlePlayerMove(game: Game, user: string, moveJson: any) {
    const move = deserializeMove(moveJson);
    if (move == null) {
        return;
    }

    const gameUUID = game.uuid;
    const result = gameService.tryMove(gameUUID, user, move);

    if (result === MoveResult.ACCEPTED) {
        io.to(gameUUID).emit('move', JSON.stringify({ move: { from: move.from, to: move.to, promotedTo: move.promotedTo } }));
    }
    else if (result === MoveResult.CHECKMATE) {
        io.to(gameUUID).emit('move', JSON.stringify({ move: { from: move.from, to: move.to, promotedTo: move.promotedTo }, ended: 'mate' }));
        handleGameClose(game);
    }
}

function handlePlayerResign(game: Game, user: string) {
    if (gameService.tryResign(game.uuid, user)) {
        io.to(game.uuid).emit('resign', user);
        handleGameClose(game);
    }
}

function parseSessionIdCookie(cookie?: string) {
    if (cookie == undefined) return null;

    const sidRow = cookie.split('; ')
        .find(cookie => cookie.startsWith('SESSION='));

    if (sidRow == undefined) return null;
    
    const sidRaw = sidRow.split('=')[1];
    const sidURIDecoded = decodeURIComponent(sidRaw);
    const sidDecoded = Buffer.from(sidURIDecoded, 'base64').toString();

    return sidDecoded;
}

// can be async?
io.on('connection', async client => {
    console.log(`Socket ${client.id} connected.`);

    client.on('disconnect', () => {
        console.log(`Socket ${client.id} disconnected.`);
        // socketio - on disconnect, automatically clears socket from room.
    });

    const uuid = client.handshake.query.gameUUID;
    
    if (uuid == undefined || Array.isArray(uuid)) {
        console.log('Invalid query uuid');
        client.disconnect();
        return;
    }

    console.log(`Connecting to: ${uuid}`);

    const game = establishGameViewerConnection(client, uuid);
    if (game === null) {
        console.log('Game not found');
        client.disconnect();
        return;
    }

    const sessionId = parseSessionIdCookie(client.handshake.headers.cookie);
    if (sessionId == null) {
        console.log('User is not logged in');
        return;
    }

    console.log(`Session id: ${sessionId}`);

    const user = await dbLayer.getLoggedInUser(sessionId);

    if (user == null) {
        console.log('User is not logged in');
    } else if (game.isPlayer(user)) {
        // if user is player in game:
        // create player message handlers to respond to move, resign.
        console.log(`User: ${user} - player.`);

        client.on('move', (move: string) => {
            console.log(`Received ${user}'s move: ${move} for ${game.uuid}`);
            handlePlayerMove(game, user, JSON.parse(move));
        });

        client.on('resign', () => {
            handlePlayerResign(game, user);
        });
    } else {
        console.log(`User: ${user} - viewer.`);
    }
});

io.engine.on("connection_error", (err) => {
    console.log(err.req);
    console.log(err.code);
    console.log(err.message);
    console.log(err.context);
});


console.log(`Game socket server listening on port ${port}`);
server.listen(port);
