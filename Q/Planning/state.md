Q Game State Design

To: CEO of the Q Company

From: surprising-monkeys

Date: Sep. 27, 2023

Subject: Q Game State Design

For the game state component of our product, we decided to split it into two main interfaces, with
classes and interfaces that build them up.
They are the _QGameMapState_, and the _QGameMap_ interfaces. The _QGameMapState_ represents the
state of a Q Game board, but none of the mutable parts of the board. This is so that
we can give immutable board state to other components that may need information from it.
The _QGameMap_ interface extends the _QGameMapState_ and has the full functionality of a board.

A referee will need to make several observations about the board, so we decided that a board should
have methods that give the referee all important knowledge of the game map.

(_Tile_ is the interface that represents the game tiles,
and _Posn_ is the class that represents an x, y coordinate)\
The wish list for the map we came up with includes these methods:

_Tile_ getTileAtPosn(_Posn_ posn)\
Returns the tile at the given position in the board map. Throws error if there is no tile there.

_boolean_ posnHasTile(_Posn_ posn)\
Returns true if there is a tile in the map at the given position

_Map_<_Integer_, _Map_<_Integer_, _Tile_>> getBoardState()\
Returns the game map as a nested map of key value pairs, with the keys being the y coordinate of a row,
and the value being a map from x coordinate to tile.

_List_<_Posn_> validPositions(_Tile_ tile)\
Returns a list of all positions where a tile can extend the game map.

_void_ placeTile(_Tile_ tile, _int_ row, _int_ col)
Extends the board by placing the given tile at the given position. Throws an error if the given position has a tile
or if the given position does not have any immediate orthogonal neighbors that are tiles.
