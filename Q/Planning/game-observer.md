Game Observer Design

To: CEO of the Q Company

From: salty-wolves

Date: Oct. 25, 2023

Subject: Game Observer Design

When deciding on the functionality for a game observer, we thought about what information that anyone
that is not a player or referee could have access to. We didn't want to reveal too much, due to the
risk of a player receiving information that they don't have from an observer. Our observer interface supports:
- List\<Integer> _viewScores_(): Viewing the scores of players in the current turn.
- QGameMap _viewBoard_(): Viewing the current game's board.
- List\<String> _turnOrder_(): Viewing the current game's turn order.
- List\<QGameMap> _gameHistory_(): A history of the game board as the game progresses.
- boolean isGameOver(): Returns true when the game is over and false otherwise
- GameResults _results_(): Returning the results of the game after it finishes.

These should provide enough information for an observer to know lots of public things about the game.
While these are the methods that one would want to use to get information from the observer,
the referee still needs to update the observer when things change in the game. This motivates
two more methods:
- void _receiveState_(QGameState state): Take in a game state and update internal information using it.
- void _endGame_(GameResults results): Tells the observer that the game is over, and gives the results of the game.

As for how someone who should only know the state of the board, to protect
the privacy of the player and ref hands, A component for them to interact with should communicate
with an observer, only ever requesting information, while the referee should be sending the
observer game state after performing each turn. For a quick overview on how everything should start,
the referee should inform the observer of the initial map state during its setup of the game.
And someone watching could see the starting state, who is playing, and what the starting scores are,
through _viewBoard_, _turnOrder_, and _viewScores_. After a turn is made,
the referee would call _receiveState_, giving the observer the updated state.
The view of the person interacting with the observer would probably have to query the observer fairly frequently to
make sure they are up-to-date with the latest updates. This cycle would continue
until the game ends, and the referee calls _endGame_ on the observer, and the person interacting with
the observer could query that information through _isGameOver_ and _results_. One last possible interaction is
reviewing previous game states through the _gameHistory_ method, giving a user
who begins to observe the game after it starts the ability to find out what
set of turns led the game to the state it currently is in.