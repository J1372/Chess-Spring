# Chess-Spring
Chess web application built using a variety of different web technologies, featuring user registration, profiles, histories, and real-time gameplay.

# Components
## Frontend - React, JavaScript
- Uses common libraries such as React Router and React Query.
- Utilizes client Socket.io library to receive and send game updates through web sockets.
## Web backend - Spring Boot, Java
- Provides REST API endpoints for registering, logging in, creating new games, joining games.
- Updates the database using Hibernate and JPQL.
- Communicates with the web socket service to start new games when an open game is joined.
- Serves files.
## Web socket backend - Node.js, TypeScript
- Maintains Socket.io connections.
- Receives player moves through web sockets and validates against the server-side board model.
- Broadcasts valid moves back to players and viewers.
- Updates user stats and histories in the database using SQL on game end.
## Database - MySQL
- Data stored includes user information, past games, and users' histories against each other. 

All backend services are containerized using Docker and scaled using Kubernetes.

# Features
- User registration, profiles, and sessions.
  - Users' game histories and stats can be viewed on their profile.
  - Users can see their history with another player that they have played against by viewing the other player's profile page.
- Posting games and joining open games from a React Query automatically updating table.
- Real-time gamplay for players and viewers using web sockets.
  
# Addendum
Much of the work on the Node.js backend and frontend I wrote while developing my previous project found here:
https://github.com/J1372/ChessSitePrelim

This project is also a chess web application; however, the backend of this previous project is written entirely using Node.js and Express. Additionally, this previous project used MongoDB instead of MySQL.
