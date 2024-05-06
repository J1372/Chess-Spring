FROM node:18

WORKDIR /usr/src/chess_server/game_server

COPY ./game_server/package.json ../
COPY ./game_server/package-lock.json ../

RUN npm install

COPY ./gameplay ../gameplay
COPY ./game_server/game_server_service.js ./
COPY ./game_server/game_server.js ./
COPY ./game_server/db_layer.js ./
COPY ./game_server/mysqldb_layer.js ./

CMD [ "node", "game_server.js" ];
