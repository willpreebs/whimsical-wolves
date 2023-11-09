**Remote**

To: CEOs of the Q Company

From: whimsical-wolves

Date: November 9, 2023

Subject: Remote

Our design for making the Q-Game a distributed system
will make use of the remote-proxy design pattern. The communication between 
the referee proxy and the player proxies will be done through JSON files which 
are serialized from the state information each proxy would like to send and 
deserialized back into that state information for the receiver's representation 
of the state. 

SETUP:
On instantiation of the game, the referee will actively listen for players who would like to join 
the game on a port. Once connected, the referee will create a "proxy player" for each player that 
connects to the game. This proxy player will exist on the server side and will be created with a 
connection to a "proxy referee" which exists on the client side and is constructed when a player 
communicates that they would like to join a game. The referee treats the proxy player as if they 
were real players connected to its game, and each player treats the proxy referee as if it were the 
real referee it was connected to. The proxy player relays the information from method calls from the 
referee over a communication layer to the proxy referee, which calls the player's respective methods.

Once the communication layer is opened, The referee will call connect() method of the proxy player,
which sends a JSON String "connect" to the proxy referee, who calls connect() on the player. This
validates the connection prior to sending the game setup information. When the player returns
from this method (void) an acknowlegement is sent from the proxy referee to the proxy player
so it can return (void) back to the Referee. The referee will wait some amount of time for each player 
to respond, and will not allow them to play if they time out. The ref then sends each player a JSON Array 
of 3 objects: a JSON string "setup", a JMap representing the initial board, and JTile arr of the player's 
tiles. The pattern of acknowledging the message is repeated (see sequence diagram)

STEADY-STATE: While playing the game, the ref will call takeTurn on a player proxy,
giving it a JPub, which once receieved from the ref proxy, the player method is called with the 
information so it can decide on a TurnAction. The player will communicate to the ref proxy
their TurnAction, which is serialized into a JTurnAction and sent to the player proxy, which
will deserialize and send the TurnAction to the ref for processing. This communication protocol
will repeat for every turn for the rest of the game. If the TurnAction was a PlaceAction or 
Exchangeaction, then the Referee calls newTiles() with the Player's new tiles and the pattern of 
communication repeats. If a player cheats at any point in the game or takes too long to respond
the ref will call kickPlayer() with a message of what rule they broke and the player proxy
will no longer communicate to this player's ref proxy until the end of the game.

ENDGAME: Once the ref determines the game is over, it calls win() on each player proxy with a boolean
that indicates whether the player has won or lost. This is communicated between proxies as a JSON object 
of {"win": bool}. The referee will then shut down the game by notifying each player
via a JSON string that it is disconnecting. At this point, all player proxies close the connection to its
referee proxy. 

Sequence Diagram:

This displays interactions between a Referee and a single player. With multiple players,
each step is repeated such that the Referee interacts with each PlayerProxy which then communicates 
with its respective RefereeProxy, and then the message is relayed to each Player.

    | Method calls:                  | JSON based Communication layer:        |      Method Calls:           |
    |                                |                                        |                              |
    |                                |                                        |                              |

    Referee                         PlayerProxy                            RefereeProxy                      Player
    |                                |                                        |                              |
    Setup Phase ----------------------------------------------------------------------------------------------------
    |                                |                                        |                              |
    |       ---connect()--->         |       ...."connect"...>                |         --connect()-->       |  
    |                                |                                        |                              |  return void
    |      <------------             |      <..{"acknowledge": "connect"}..   |         <--------------      |  
    |                                |                                        |                              |
    |                                |                                        |                              |
    |     ---setup(map, tiles)-->    |  ..["setup",{"map":...},[tile,...]].>  |  ---setup(map, tiles)-->     |  
    |                                |                                        |                              |  return void
    |      <-----------              |    <..{"acknowledge": "setup"}..       |           <------------      |  
    |                                |                                        |                              |
    |                                |                                        |                              |
    Steady State Phase -------------------------------------------------------------------------------------------------
    |                                |                                        |                              |
    |                                |                                        |                              |
    | ---takeTurn(playerState)--->   |     ....["takeTurn", {JPub}]....>      | ---takeTurn(playerState)-->  |
    |                                |                                        |                              | return TurnAction
    |  <---TurnAction----            |     <...["takeTurn", {JAction}]..      |    <---TurnAction----        | 
    |                                |                                        |                              |
    |   If TurnAction is a ExchangeAction or PlaceAction:
    |                                |                                        |                              |
    |   ----newTiles(tiles)-->       |    ...["newTiles", [tile,...]]..>      |      ----newTiles(tiles)-->  |
    |                                |                                        |                              | return void
    |      <------------             |     <..{"acknowledge": "newTiles"}..   |     <---------------         |
    |                                |                                        |                              |
    |  If the TurnAction is illegal, or the Referee otherwise determines that the connection should be terrminated:
    |                                |                                        |                              |
    |   --kickPlayer(string)-->      |    ...["kickPlayer", "message"]..>     |   --kickPlayer(string)--     |
    |                                |                                        |                              | return void
    |    <-----------                |   <..{"acknowledge": "kickPlayer"}..   |    <---------------          |
    |                                |                                        |                              |
    |   Repeat until Referee determines the game is over:
    .                                .                                        .                              .
    .                                .                                        .                              .
    .                                .                                        .                              .
    .                                .                                        .                              .
    Shutdown Phase -----------------------------------------------------------------------------------------------
    |                                |                                        |                              |
    |                                |                                        |                              |
    |   ----win(Boolean)--->         |       ...{"win": boolean}...>          |     ----win(Boolean)--->     |
    |                                |                                        |                              | return void
    |   <---------------             |      <..{"acknowledge": "win"}..       |     <------------            |
    |                                |                                        |                              |
    |                                |                                        |                              |
    |    ---disconnect()-->          |      ..."disconnect"..>                |     ---disconnect()-->       |
    |                                |                                        |                              | return void
    |    <------------               |  <..{"acknowledge": "disconnect"}..    |    <-------------            |
    |                                |                                        |                              |
    |                                |           CONNECTION CLOSED            |                              |
    |                                |                                        |                              |
    |                                |                                        |                              |



