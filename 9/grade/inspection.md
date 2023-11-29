Pair: whimsical-wolves \
Commit: [c8a448fe42a3914faccac57009d659b591e17b0c](https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/tree/c8a448fe42a3914faccac57009d659b591e17b0c) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/bef6da10f32c0f9af4389b4ccf1fe285cc51a8b5/9/self-9.md \
Score: 150/210 \
Grader: Can Ivit

## Self Eval [20/20]
Thank you for honest and helpful self eval

## Programming [130/190]
- [-15] There are no unit tests that checks if `PlayerProxy` reads/writes correct JSON from/to mock input/output stream.
- You are right, `RefereeProxy` does not need to implement `IReferee` interface. The question in the self-eval was trying to ask how the proxy referee acts similar to the actual referee. (invoking methods on the player).
- [-5] `RefereeProxy` should not ignore exceptions in `listenForMessages`.
- [-5] The loop in RefereeProxy's `listenForMessages` method should check whether there are more JSON coming from the input stream.
- [-15] There are no unit tests that checks if `RefereeProxy` reads/writes correct JSON from/to mock input/output stream.
- [-20] The client crashes if the server at the specified host and port is unavailable. This [line](https://github.khoury.northeastern.edu/CS4500-F23/whimsical-wolves/blob/c8a448fe42a3914faccac57009d659b591e17b0c/Q/Common/src/qgame/server/Client.java#L38) should be protected.
