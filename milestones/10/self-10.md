The commit we tagged for your submission is c4a03784723fb0c099a7a22c66c421a1f0613e7f.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/tree/c4a03784723fb0c099a7a22c66c421a1f0613e7f

## Self-Evaluation Form for Milestone 10

Indicate below each bullet which file/unit takes care of each task.

The data representation of configurations clearly needs the following
pieces of functionality. Explain how your chosen data representation 

- implements creation within programs _and_ from JSON specs 
The constructor of all of the configuration classes (RefereeConfig, RefereeStateConfig, ClientConfig, ServerConfig) takes in a JsonObject which is either parsed using the Gson library from an input outside of the program, or can be constructed from within the program. 

Example of when ServerConfig object is constructed: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/harnesses/XServerClient.java#L44

- enforces that each configuration specifies a fixed set of properties (no more, no less)
Each configuration takes in a JsonObject, and from this JsonObject we get several fields that are required for the JsonObject to have. If one of these fields do not exist or are not in the correct format, an exception is thrown. We do not enforce that a JsonObject does not contain extra fields since we did not see an issue with accepting a JsonObject that had unnecessary information.

Example: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/server/ServerConfig.java#L18-L32

- supports the retrieval of properties 
Has getters for each of the fields of the spec

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/server/ServerConfig.java#L35-L57

- sets properties (what happens when the property shouldn't exist?) 
Our data representation does not support setting properties, except that you could make another Config object from a new JsonObject.

- unit tests for these pieces of functionality
Did not implement unit tests for Config data representations

Explain how the server, referee, and scoring functionalities are abstracted
over their respective configurations.

Server:
A server may or may not be constructed with a ServerConfig. If it does not include a ServerConfig, then default values are used. If it is, then several fields are initialized using getters of the ServerConfig object.

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/server/Server.java#L87-L96

Referee: 
A QReferee may or may not be constructed with a RefereeConfig. If it is not, then default values are used or rules are passed in. Otherwise several fields are initialized using the RefereeConfig upon construction of the QReferee.

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/referee/QReferee.java#L82-L101

Scoring:

In RuleUtil.createScoreRules there is a constructor for passing in the values of the RefereeStateConfig object, namely qBo as qBonus and fBo as fBonus

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/util/RuleUtil.java#L62-L69

Does the server touch the referee or scoring configuration, other than
passing it on?
The RefereeConfig is not touched by the Server other than setting it as a field in the Server
so it can use it later to start the Referee.
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/server/Server.java#L95

https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/server/Server.java#L147-L148

Does the referee touch the scoring configuration, other than passing
it on?
The QReferee gets the two fields of the scoring configuration to call createScoreRules.
https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c4a03784723fb0c099a7a22c66c421a1f0613e7f/Q/Common/src/qgame/referee/QReferee.java#L99


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

