The commit we tagged for your submission is c8a448fe42a3914faccac57009d659b591e17b0c.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/tree/c8a448fe42a3914faccac57009d659b591e17b0c

## Self-Evaluation Form for Milestone 9

Indicate below each bullet which file/unit takes care of each task.

For `Q/Server/player`,

- explain how it implements the exact same interface as `Q/Player/player`
  A PlayerProxy is an implementation of the Player interface and can be used interchangeably with any other Player. The PlayerProxy implements all of the Player methods specified in logical interactions. 
  https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Server/player.java#L28
- explain how it receives the TCP connection that enables it to communicate with a client
A PlayerProxy, in addition to a player name, is constructed with a JsonStreamParser object that contains the input stream from a TCP Socket. It is also constructed with a PrintWriter that contains an output stream that will print out back to the socket connection. In the receive() method, the parser tries to read the next Json object from the input stream. In sendOverConnection(JsonElement), the PlayerProxy can send the given JsonElement via the PrintWriter over the socket connection.

Constructor: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Server/player.java#L38-L42

receive(): https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Server/player.java#L54-L64

sendOverConnection(): https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Server/player.java#L44-L47

- point to unit tests that check whether it writes (proper) JSON to a mock output device
This unit test was not implemented.


For `Q/Client/referee`,

- explain how it implements the same interface as `Q/Referee/referee`
Did not realize that the RefereeProxy needed to implement the IReferee interface. It didn't seem like the RefereeProxy needed the same functionality as a QReferee such as playing a full game.
- explain how it receives the TCP connection that enables it to communicate with a server
A RefereeProxy is constructed with a PrintWriter and a JsonStream parser that contain the out and in streams of the TCP connection respectively. 

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/RefereeProxy.java#L33-L37

- point to unit tests that check whether it reads (possibly broken) JSON from a mock input device
 
Did not implement this unit test

For `Q/Client/client`, explain what happens when the client is started _before_ the server is up and running:

- does it wait until the server is up (best solution)
When a client is started, it will send the player's name over the TCP connection. If the server hasn't started to run, the data will stay in the input stream until it is read in by the Server.

how a Client is started: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Client.java#L64-L86

How a server, once it is started, reads from the input stream and gets the player name:
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Server.java#L95-L98 <br/> getProxies() enforces the multiple waiting periods and getPlayerProxiesWithinTimeout enforces a single waiting period <br/>
For getting a single player proxy when and if the client sent the name:
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Server.java#L132-L175
- does it shut down gracefully (acceptable now, but switch to the first option for 10)

For `Q/Server/server`, explain how the code implements the two waiting periods. 
In the method getPlayerProxiesWithinTimeout, a 20 second timeout period is enforced that starts just before the method to get a single player proxy is called. Out of the total 20 second WAITING_PERIOD, each Client has the remaining time to connect after the previous clients have connected. At the start of each loop getting up to a maximum number of PlayerProxies, the time left in the waiting period is equal to the total waiting period minus the difference between the start of the looping and when the current loop is starting (represented by start and now variables). 
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Server.java#L179-L212

Then the 3 second waiting period is enforced in getPlayerProxy. The time period starts when the socket is accepted (A client has connected). Then the player name must be received by the server within 3 seconds or the server moves on to the next client.
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Server.java#L152-L164

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

