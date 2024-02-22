# Wordle 

## Introduzione 🚀
Questo progetto si propone di emula il famoso gioco Wordle del New York Times, implementando un'architettura client-server in Java. Il sistema è progettato per essere multithread, consentendo la gestione simultanea di più connessioni client, e supporta la comunicazione multicast per inviare aggiornamenti a tutti i client connessi in modo efficiente.

## Architettura 🏗
- **Client**: L'interfaccia utente che permette ai giocatori di interagire con il gioco, inviando le loro supposizioni al server e ricevendo risposte.
- **Server**: Il cuore del sistema, gestisce la logica del gioco, le connessioni client, e le sessioni di gioco. È progettato per essere multithread, per supportare più giocatori contemporaneamente.

## Tecnologie Utilizzate 💻
- **Java**: Linguaggio di programmazione scelto per lo sviluppo di entrambi i componenti, client e server, grazie alla sua portabilità e alle sue capacità multithread.
- **Multithreading**: Utilizzato per gestire connessioni multiple in parallelo, permettendo al server di processare le richieste di più client contemporaneamente.
- **Multicast**: Implementato per la comunicazione tra server e client, consentendo al server di inviare messaggi a tutti i client connessi simultaneamente.
