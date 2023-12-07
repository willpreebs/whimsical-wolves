# Q-Game

This repository is used for the development and implementation
of Q-Game, which is a distributed 2-4 player turn-based strategy game
loosely based on the board game Qwirkle. 

<h3>Game Overview</h3>

A standard Q-Game is a 2-4 player board game in which each player must attempt to gain as
many points as possible by placing down successive tiles in appropriate ordering
on the board.

The game's board size is bounded only the by existing tiles that have been placed on 
the board, which can be extended upon by a player on their turn. There are 30 tiles of
each of the 36 tile variants, for a total of 1080 in a game.

<h4>A tile is defined by 2 properties:</h4>
- Shape: "star", "8star", "square", "circle", "clover", "diamond".
- Color: "red", "green", "blue", "yellow", "orange", "purple".  
Both these lists are ordered from least to greatest value. 

<h4>Setup</h4>
On setup, the Referee, which is an omniscient controller method
for the game, will authenticate each player and give them
their starting hand of tiles. Turn-order is established by connection
order, and the game starts with the Referee placing a tile on the
board. 

<h4>Gameplay</h4>
On a turn a player can do one of three things:
- Pass: the player forfeits their turn. 
- Exchange: the player returns all of their tiles to 
referee's collection and is dealt an equivalent amount
of new tiles from the ref's collection. 
  - constraint: The referee must have at least the player's
  hand size number of tiles for this action to be possible.
- Place: place as many tiles from the player's hand so long as
the placements follow the rules. 
  - Rules:
    - all tiles placed in a turn must be along one column or
   one row. 
    - Each tile placed must extend the existing board.
    - Each tile placed and its immediate horizontal neighbors must match in shape or color.
    - Each tile placed and its immediate vertical neighbors must match in shape or color. 

<h4>Scoring</h4>
A player gets points as seen below for the following actions:
- On a turn
  - 1 pt per tile placed.
  - 1 pt per tile in column containing a tile placed.
  - 1 pt per tile in row containing a tile placed.
  - 8 pts per Q made in a turn
    - Q is defined as any row or column that contains a tile placed
    and has 1 tile for every shape, or 1 tile for every color. 
  - 4 pts for being the last player, or the one who ends the game.

<h4>Breaking the Rules</h4>
If at any point the referee determines a player cheats or they fail
to respond appropriately in a timely fashion, said player is ejected from
the game. Their turn will always be skipped. 

<h4>Ending the Game</h4>
The game ends when one of the following conditions are met. 
- In one round, no player places a tile. 
- In one turn, a player uses all of their tiles.
- After a turn, there are no players left in the game.

<h3>Q-Game's Distributed System</h3>
Q-Game implements a form of TCP using the Java NET
framework for players and the game to communicate even when on
different systems. The following UML diagram documents the expected
interactions between the Server the game is hosted on and the player
Clients that attempt to connect to the game.
![remote-interactions.png](Q%2FPlanning%2Fremote-interactions.png)

Notably, this design makes use of the remote-proxy pattern to 
ensure that the Referee and each Player are themselves agnostic
to the specific implementation of communication. The proxies currently
communicate through the JSON wire protocol. On setup, the server has 
a configurable waiting time for an appropriate number of players to 
connect before starting the game, represented as a finite number of
waiting periods of a defined time. Once connections are properly established,
the game can commence as normal. During the game, the referee has a 
configurable waiting time for a player's response before it determines that
player to be unresponsive. Unresponsive players are disconnected in the same
way that cheating players are. Server-client connections are cleanly disconnected
because each server and client is a Runnable object, meaning that the thread 
they run on's existence is directly tied to if said program is still running. 

<h3>Players</h3>
Any player who wishes to participate in Q-Game will have to implement
our player API, which can be seen [here](https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/e4b98843700ce51811766285273b2a4f64782740/Q/Common/src/qgame/player/Player.java)
.

It is recommended that player's develop some sort of strategy algorithm
to assist them in determining the appropriate action in their turn. Two examples
of naive player strategies can be found [here](https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/32195c3d3af0cb3ca01bf5be1ee42fde96168037/Q/Common/src/qgame/player/strategy)
. 
