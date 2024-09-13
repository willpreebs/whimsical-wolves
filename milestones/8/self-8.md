The commit we tagged for your submission is 8c94cde4161613194f56d6d80fbaeac35e22b57e.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/tree/8c94cde4161613194f56d6d80fbaeac35e22b57e

## Self-Evaluation Form for Milestone 8

Indicate below each bullet which file/unit takes care of each task:

- concerning the modifications to the referee: 

  - is the referee programmed to the observer's interface
    or is it hardwired?
    A QReferee can be constructed with or without Observers attached. If a list of Observers are included, then the behavior
    of the QReferee's methods are unchanged, except that there are calls to each Observer before a turn is played as well as
    after the game is over.
    Constructor with a List of Observers as a parameter: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/8c94cde4161613194f56d6d80fbaeac35e22b57e/Q/Common/src/qgame/referee/QReferee.java#L87-L95

  - if an observer is desired, is every state per player turn sent to
    the observer? Where? 
    Before each player's turn, a method (giveObserversStateUpdate()) is called which goes through the list of Observers and calls receiveState on each one with the current game state.
    Method call within playRound: 
    https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/8c94cde4161613194f56d6d80fbaeac35e22b57e/Q/Common/src/qgame/referee/QReferee.java#L273-L277
    Method that calls receiveState on each observer:
    https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/8c94cde4161613194f56d6d80fbaeac35e22b57e/Q/Common/src/qgame/referee/QReferee.java#L298-L302
    
  - if an observer is not desired, how does the referee avoid calls to
    the observer?
    A Referee can be constructed without any Observers attached. When the methods are called to give state updates and notify observers that the game is over, the loop ends immediately since the list of observers will be empty.

- concerning the implementation of the observer:

  - does the purpose statement explain how to program to the
    observer's interface?
    In IGameObserver, there are purpose statements explaining how calling the methods next or previous renders different game states. Also there is a purpose statement describing how to save an image of the observer's view at a user specified file.
 https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/8c94cde4161613194f56d6d80fbaeac35e22b57e/Q/Common/src/qgame/observer/IGameObserver.java#L7-L23

  - does the purpose statement explain how a user would use the
    observer's view? Or is it explained elsewhere? 
    We are lacking a purpose statement for describing how a user interacts with the GUI.
    

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

