The commit we tagged for your submission is a823c34feaa1aa76074a688ce14867709008b84b.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/a823c34feaa1aa76074a688ce14867709008b84b

## Self-Evaluation Form for Milestone 5

Indicate below each bullet which piece of your code takes care of each task:

1. a data definition (inc. interpretation) for the result of a strategy
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/TurnStrategy.java#L6-L18 
- Our implementation of strategies performs iteration as well as placement selection.
2. the `dag` strategy 
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/DagStrategy.java#L13-L31

3. the `ldasg` strategy 
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/LdasgStrategy.java#L14-L62

4. a data definition (inc. interpretation) for the result of a strategy iterator
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/TurnStrategy.java#L6-L18
Turn Action: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/action/TurnAction.java#L3-L15


5. unit tests for the `dag` strategy
   - one for a 'pass' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L116-L135
   - one for a 'replace all tiles' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L137-L156
   - one for a 'place this tile there' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L53-L81

6. unit tests for the `ldaag` strategy
   - one for a 'pass' decision
   Don't have this
   - one for a 'replace all tiles' decision
   Don't have this
   - one for a 'place this tile there' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/LDASGStrategyTest.java#L111-L142

7. unit tests for the strategy iteration functionality 
   - one for a 'pass' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L116-L135
   - one for a 'replace all tiles' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L137-L156
   - one for a _sequence of_ 'place this tile there' decision
   https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/test/qgame/player/strategy/DagStrategyTest.java#L53-L81

8. how does your design abstract the common strategy iteration functionality 
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/SmallestRowColumnTileStrategy.java#L93-L102
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/SmallestRowColumnTileStrategy.java#L105-L115
We have an abstract class that both strategies extend, which contains the methods seen above when strategy iteration is necessary for
finding all the placements that need to be made. The first method is called by the second method, which determines
if a placements are possible, and if not, whether to pass or exchange. 




9. does your design abstract the common search through the sorted tiles?
   (for a bonus)
  abstracting finding smallest tile in player hand: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/player/strategy/SmallestRowColumnTileStrategy.java#L76-L86
  comparing by row-column in tie-breaking cases: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/44fba4aab6e752fcc3392fc0990bb6b74c3e869a/Q/Common/src/qgame/util/PosnUtil.java#L16-L30

   
The ideal feedback for each of these points is a GitHub perma-link to
the range of lines in a specific file or a collection of files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


