The commit we tagged for your submission is b5e348ea98a72f4d38e1411c465931a9270add3a.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/b5e348ea98a72f4d38e1411c465931a9270add3a

## Self-Evaluation Form for Milestone 7

Indicate below each bullet which file/unit takes care of each task:

The require revision calls for turning Q bonus points and
finished-the-game bonus points into named constants

1. Which unit tests check the Q-bonus functionality? Is it abstracted
   over the named constant?
   This is one of the unit tests. Instead of having a named constant, we parameterized the bonus in the Q scoring rule
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/b5e348ea98a72f4d38e1411c465931a9270add3a/Q/Common/test/qgame/rule/scoring/RuleTests.java#L139-L152
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/b5e348ea98a72f4d38e1411c465931a9270add3a/Q/Common/test/qgame/rule/scoring/RuleTests.java#L40

2. Which unit tests check the finished-the-game functionality? Is it
   abstracted over the named constant?
   This test is set up so that the person who places all of their tiles will end up tying with another player with a set value,
   indicating that the finish the game bonus is exact. We also parametrized the bonus in the referee here
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/b5e348ea98a72f4d38e1411c465931a9270add3a/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L372-L394
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/b5e348ea98a72f4d38e1411c465931a9270add3a/Q/Common/test/qgame/referee/BasicQGameRefereeTest.java#L90
3. Do you also have integration tests that show how setting the bonus
   constants to different constants yields different results for the
   same starting point? (This is optional but helps with milestone 8
   and fits to the request.)
   No.


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

