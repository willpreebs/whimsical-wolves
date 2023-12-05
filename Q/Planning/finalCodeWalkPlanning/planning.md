
1. Components diagram - brief overview of relationships between components
2. Start with Server
   1. Go through getting clients breifly
   2. Segue into talking about Referee
3. Referee - general overview (playing a game from a list of players)
4. Referee and GameState interactions with explanation of data representations including Players / PlayerInfo
5. Exemplify interactions using setupPlayers
   1. Error handling (setup, newTiles, win follow similar behavior)
   2. Removing player on error
   3. Add player to the next round if setup succeeds
6. Go to playGameRounds
   1. Introduce looping logic and end conditions
7. Go over how each round is done with the context of how setupPlayers works
8. Go over what happens after the game is over
   1. Notify observers
   2. Compute GameResults + notifying players
9.  Server closes all connections

Maybe: demo of a full game at the end w/ client/server and observer