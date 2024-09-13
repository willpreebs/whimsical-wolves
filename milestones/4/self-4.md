The commit we tagged for your submission is 8d833c6c77ce2813d3cb47f33b31d841217c4822.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/8d833c6c77ce2813d3cb47f33b31d841217c4822

## Self-Evaluation Form for Milestone 4

Indicate below each bullet which method takes care of each task:

1 'rendering the referee state' 
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/gui/GameStateView.java#L17-L23

2. 'scoring a placement'
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/ScoringRuleBook.java#L8-L22
https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/ScoringRule.java#L8-L12

3. The 'scoring a placement' functionality clearly performs four different checks: 
  - 'length of placement'
    Yes.
    https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/PointPerTileRule.java#L8-L16
  - 'bonus for finishing'
    Yes.
  https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/PlaceAllOwnedTiles.java#L8-L21
  - 'segments extended along the line (row, column) of placements'
    Yes.
    https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/PointPerContiguousSequenceRule.java#L25-L26
  - 'segments extended orthogonal to the line (row, column) of placements'
    Yes.
    https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/PointPerContiguousSequenceRule.java#L23-L24
  - indicate which of these are factored out into separate
    methods/functions and where.
    The above links should show that. One thing is the the checks that segments extent along the line and that segments extend orthogonally
    use the same helper method to iterate along the board.
    https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/8d833c6c77ce2813d3cb47f33b31d841217c4822/Q/Common/src/qgame/rule/scoring/CrawlingRule.java#L8-L32
   
The ideal feedback for each of these points is a GitHub perma-link to
the range of lines in a specific file or a collection of files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


