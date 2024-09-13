Player Interface Design

To: CEO of the Q Company

From: surprising-monkeys

Date: Oct. 5, 2023

Subject: Player Interface Design

For now, the player interface's core functionality is to take a turn. The _Player_ interface
has a method _takeTurn_ that returns a _TurnAction_ which represents one of the supported
choices for a player's turn, being one of: a pass, exchange, or a list of tile placements.

_TurnAction_ takeTurn(_PlayerInformant_ informer)\
Gives a player all available information to them, and produces a turnAction, representing the
action they choose to take for the turn.

A TurnAction currently is one of 3 classes, A PassAction, a PlacementAction, and an ExchangeAction.
Using the visitor pattern, a referee can create a TurnVisitorImpl, which will handle performing all
rule checking and action execution. All of this PlayerInformant functionality already exists.

The _PlayerInformant_ interface has several observations that the player can use to determine its
next move. These observations include:

_List_<_Posn_> legalMoves(_Tile_ t)
Returns all positions where the given tile can be placed according to the rules of QGame.

_List_<_Player_.PlayerInfo> turnOrder()
Returns the turn order of the players in the game. The first player is the player whose turn it is.

_List_<_Tile_> tilesForPlayer(_Player_ p)
Returns the list of all tiles a player has in their hand.

_QGameMapState_ viewBoard()
Returns the state of the game board.

_Map_<_Player_.PlayerInfo, _Integer_> scoreForPlayers()
Returns a mapping of players to scores so that the students.

_int_ remainingTiles();
Returns the number of tiles the referee has.

_boolean_ arePlacementsLegal(_List_<_Placement_> placements)
Returns true if the given list of placements meets the rules of the current implementation.