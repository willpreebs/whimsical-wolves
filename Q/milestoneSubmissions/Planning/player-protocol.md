**Player Protocol**

To: CEO of the Q Company

From: salty-wolves

Date: Oct. 12, 2023

Subject: Player Interface Design

We envisioned a conversational interaction protocol
between our referee and a player. Going with last milestone's design,
The _Player_ interface represents a player, so their main action is taking a turn.
So the only method in player is _takeTurn_, which will take in a _Referee_. We currently
imagine there being an implementation of this interface that handles communicating with an
external player, and turning that input into method calls and _Action_ instances,
resulting in a _TurnAction_. The desired communication with such an external player begins
with a prompt from the referee to make an action, and ending with the
player to send an action, as specified in the design spec.

If the player needs more information, it would continually ask the referee, i.e.
if it needed to know if a given list of placements was legal to place tiles for its turn.
The referee has methods such as _currentState_, _canExchange_, _arePlacementsLegal_, and
_legalMoves_, which grant a player all knowledge they may need to perform an action,
in addition to the initial game state they are sent.
We distinguish between a player asking if an action is legal and 
making their final decision. The former will have the referee answer the query,
and the latter is a final action. After the action is received, the referee
will check the action with their rule set, and determine if the player should
remain in the game or not. They will get a different final response depending on
if their move was successful. The player interface
acts as an intermediary transmitting information to and from the referee and player.

The referee and the players will also agree to a preferred method of communication.
It currently seems like we want to send JSON through TCP socket connections, but this
protocol can be used for a player locally connected, or through some other networking
interface. The decision of what format the messages should be is flexible as well.
The referee will return the game state to the player after a successful action,
through the Player interface's _receiveState_ method.

Afterwards, they will not receive a message from the referee again, until it is their turn,
where they receive their next prompt.  In effect, each player will 
wait for referee prompts, and the referee will wait on each
player whenever it's their turn (the referee will ignore players when not their turn, or 
after they've decided on an action). If a player violates the rules in any way, they will
be ejected from the game. The game state will be updated to reflect this, and the referee will
no longer listen to this player. Every time a turn ends, the referee will look for the next
available player in the turn order. If it can't find anyone, then the game will enter
its endgame procedure as specified in the design doc. 

In short, the player-referee interaction will be initiated by the referee,
and continued by a player, either with queries, or an action,
which would initiate the final response from the referee, informing the player
of the new game state or if they are to be removed.