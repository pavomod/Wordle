# Wordle

## Introduction 🚀
This project aims to emulate the famous New York Times game, Wordle, by implementing a client-server architecture in Java. The system is designed to be multithreaded, allowing simultaneous management of multiple client connections, and supports multicast communication to efficiently send updates to all connected clients.

## Architecture 🏗
- **Client**: The user interface that allows players to interact with the game, sending their guesses to the server and receiving responses.
- **Server**: The core of the system, managing the game logic, client connections, and game sessions. It is designed to be multithreaded to support multiple players simultaneously.

## Technologies Used 💻
- **Java**: The programming language chosen for developing both the client and server components due to its portability and multithreading capabilities.
- **Multithreading**: Used to handle multiple connections in parallel, allowing the server to process requests from multiple clients at the same time.
- **Multicast**: Implemented for communication between the server and clients, enabling the server to send messages to all connected clients simultaneously.
