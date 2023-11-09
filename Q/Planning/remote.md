**Remote**

To: CEOs of the Q Company

From: whimsical-wolves

Date: November 9, 2023

Subject: Remote

Our design for making the Q-Game a distributed system
will make use of the remote-proxy design pattern. The communication between 
the referee proxy and the
player proxies will be done through JSON files which are serialized from
the state information each proxy would like to send and deserialized back
into that state information for the receiver's representation of the state. 

SETUP:
On instantiation of the game, the referee will actively 
listen for players who would like to join the game
on a port. Once connected, the referee will create a 
"proxy player" for each player that connects to the game.
We also expect each player to set up a proxy of the referee
when they connect on the port. The referee treats the proxy players
as if they were real players connected to its game, and each player
treats the proxy referee as if it were the real referee it was connected
to. The proxy player relays the information it receives from the referee
to the proxy referee, which relays its information to its player. 

The referee will send a JSON String "connected" to each player to 
validate the connection and prepare for sending each player their requisite tile.
The player will send an ACK packet back acknowledging that they made a connection.
The referee will wait some amount of time for each player to respond, and will not
allow them to play if they time out. The ref then sends each player a JSON Array of 
3 objects: a JSON string "setup", a JMap representing the initial board, and JTile arr
of the player's tiles. The ref will again wait for an ACK from each player before continuing.

STEADY-STATE: While playing the game, the ref will call takeTurn on a player proxy,
giving it a JPubGameState, which once receieved from the ref proxy, the player will 
deserialize and use to decide on a TurnAction. The player will communicate to the ref proxy
their TurnAction, which is serialized into a JTurnAction and sent to the player proxy, which
will deserialize and send the TurnAction to the ref for processing. This communication protocol
will repeat for every turn for the rest of the game. If a player cheats at any point in the game,
the ref will send a JSON string notifying the player that they cheated, and the player proxy
will no longer communicate to this player's ref proxy, effectively severing the connection.

ENDGAME: Once the ref determines the game is over, it will send a JSON string to each player 
notifying it that it either won or lost. This is a boolean sent to the player proxy which will
serialize it into a JSON obj of {"win": bool}, which is then deserialized by the ref proxy, which
calls win(bool b) on each player. The referee will then shut down the game by notifying each player
via a JSON string that it is disconnecting. It will sever its port connection and does not care what
players do from there. 