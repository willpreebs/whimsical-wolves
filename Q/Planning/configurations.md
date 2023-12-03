

# Client modifications
- New constructor that takes in fields matching the ClientConfig specification.
- Take socket construction out of constructor and put it in run()
- Take in and out stream construction out of constructor and put it in run()
- Use JsonPrintWriter (our class) to send Json messages
- Use multiple attempts to make a new Socket connection.
- Add log method which prints out logging messages to either the error stream or System.out
- Close socket after the ref proxy stops listening for messages.
## RefereeProxy
- Add log method
- Use JsonPrintWriter
- Stop listening to messages if it gets an unexpected or ill-formed message from the server

# Server modifications:
- New constructor that takes in fields matching the ServerConfig specification
- Use JsonPrintWriter to send Json messages
- Add method to set the stream to send the results of a game for testing purposes
- Add log method which prints out logging messages to either the error stream or System.out
- Within run:
	- Add capability to start a QReferee with a RefereeConfig object
	- Close all sockets after completion of game.
	- Call System.exit to ensure that all child threads end
- Tries a single time instead of loops while connecting to a Client and getting the player name
## PlayerProxy
- Add log method
- Use JsonPrintWriter

Referee modifications:
- New constructor that takes in fields matching the RefereeConfig specification
- Add log method

Scoring modifications:
- New method in RuleUtil that takes in just the qbo and fbo from the referee configuration.

