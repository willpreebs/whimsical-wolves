The commit we tagged for your submission is 070a8e7571892bf732a50a3dc2946294b4ceec9d.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/070a8e7571892bf732a50a3dc2946294b4ceec9d

## Self-Evaluation Form for Milestone 6

Indicate below each bullet which piece of your code takes care of each task:

1. the five pieces of player functionality
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/player/Player.java#L10-L54
2. `setting up players` functionality in the referee component 
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/referee/BasicQGameReferee.java#L50-L66
3. `running a game` functionality in the referee component
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/referee/BasicQGameReferee.java#L117-L136
4. `managing a round` functionality in the referee component
    (This must be factored out to discover the end-of-game condition.)
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/referee/BasicQGameReferee.java#L138-L175
5. `managing an individual turn` functionality in the referee component
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/referee/BasicQGameReferee.java#L196-L203
6. `informing survivors of the outcome` functionality in the referee component
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/src/qgame/referee/BasicQGameReferee.java#L87-L101


7. unit tests for the `referee`:

   - five distinct unit tests for the overall `referee` functionality
     https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L223-L229
     https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L197-L207
     https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L112-L124
     https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L126-L138
     https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L140-L152
     
     
   - unit tests for the abvove pieces of functionality 
     Player Tests: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/player/PlayerTest.java#L32-L115
     Individual turn: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L223-L229
     Whole Round: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L154-L162
     Running a game: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/070a8e7571892bf732a50a3dc2946294b4ceec9d/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L198-L207
     
8. the explanation of what is considered ill-behaved player and how the referee deals with it.

There are 3 types of ill-behaved behavior. One is an invalid move. The referee deals with that with a helper to determine the validity
of the players action, so checking if it is possible to exchange when they opt to, and that it is possible to place the tiles according to its rules. 
The other two types are time outs when the referee asks the player for its turn and disconnects during communication with the player.
The referee handles determining time outs by creating a thread and calling a method on the player, check if the method has given a return value within a 
timeout value, which is configured by the referee constructor. This return will return a _TimeOutException_ when the player takes too long. The methods in 
the player interface throw IllegalStateExceptions which represent some failure of the message from the referee to the player. The referee then catches the 
different types of exceptions when it communicates with the player and removes the player from its list of players still in the game once that occurs.

The ideal feedback for each of these points is a GitHub perma-link to
the range of lines in a specific file or a collection of files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


