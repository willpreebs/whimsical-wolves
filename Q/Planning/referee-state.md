**Referee State**

To: CEOs of the Q Company

From: salty-wolves

Date: Oct. 19, 2023

Subject: Referee State Design

Referee and Game State Protocol: We have designed the referee and the
game state such that the referee validates actions it receives from players per request. It also 
gives the scoring rule functionality to the game state when it is time to process an action.
The "intelligent" work of performing a player's desired turn action is handled by a visitor and creates
new game states. The referee has a game state field which represents the current state of the game.
Each game state is immutable and the referee generates a new game state every time
the state of the game updates and updates its gameState field accordingly. The referee's 
interaction with the game state is compartmentalized into a TurnVisitor field it has which will
determine whether a proposed move by a player is legal, updating the game state, and progressing
the game to the next player per the rules of the game. Using the information
provided by the game state, the referee can also determine when the game is over, when a round 
is over, and when a turn is over/the previous turns in a round. 

We distinguish
between a PlayerGameState, which represents information to supply the player with about the
current gameState, and QGameState, which represents all the information that the Referee knows
about the game. PlayerGameState is an informational subset of QGameState, and as such the referee
generates PlayerGameState from its QGameState and gives it to the player at the start of their turn.


The QGameState interface has the following methods: 

`List<PlayerInfo> playerInformation()`: Gets a list of players based on whose turn it currently is. The first player in this list is
the player whose turn it is, the second player in the list is the next up player, etc.

`QGameMapState viewBoard()`: Gets a QGameMapState (immutable) that represent the current QGameMap

`List<Tile> refereeTiles()`: returns the list of tiles left in referee's possession

`PlayerGameState getCurrentPlayerState()`: returns a PlayerGameState with all the information
the current player needs to know about themselves and the game.

`QGameState nextTurn(TurnAction action)`: iterates the game based on the player's turn action using
a turn visitor. 
**NOTE: This may only temporarily in the gameState interface because the referee has not been 
developed yet. In the future, this method could be in the referee and use its
turn visitor as opposed to creating a new one.**

`QGameState removeCurrentPlayer()`: removes the current player from the game, used by the referee
when it wants to boot someone out and have their turn always be skipped.

`List<TurnAction> turnHistory()`: returns a list of all the turnActions taken in the round so far.

`int roundNumber()`: returns the current round in the game. Not particularly useful at the moment
but we anticipate that this information may be necessary for future implementation.

*Since our gameStates are immutable, to "load" a gameState, the referee will simply have to
take in a QGameState and begin playing the game on that.

The referee game-state protocol begins with the referee creating a new state
or loading in a previous state. From there, the referee can begin communicating with a
player to get their _TurnAction_ for the round. After receiving  the action, the referee will use
the game state and its _PlacementRuleBook_ to determine if the action is legal.
From there, the referee can do a couple of things. If the action is legal,
the referee will perform a pass, exchange, or set of placements on the board
by passing the action to _GameState.nextTurn_ .
Alternatively, if the action is illegal, the referee can reach the next game state through
_GameState.removeCurrentPlayer_. When the referee needs to send a _PlayerGameState_ at the start
of a players turn, it can call _GameState.getCurrentPlayerState_.
When a turn has been executed, the referee should check to see  if a new round of gameplay has begun,
so it can terminate a game when all players choose to pass for a round.
The _GameState.turnHistory_ method provides a list of all turns made within a round. Therefore, the referee
can determine when the round is completed, and figure out the information of all the remaining players
with _gameState.playerInformation_. Because this game state design is immutable and returns completely
new game states, the referee is allowed to compare a previous state to a currentState and use both to determine
when exactly a round of all passes occurs. A _StateIterator_ is a _TurnVisitor_ that will
end up being configurable by the referee to perform its desired actions on a game state, as well as
update the order of players, and scoring. The current implementation is hardwired with the scoring logic.
