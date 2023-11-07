Pair: salty-wolves \
Commit: [a823c34feaa1aa76074a688ce14867709008b84b](https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/a823c34feaa1aa76074a688ce14867709008b84b) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/60e1b8ad835ba9ab0cefeeb0476f4ab77ee234f9/5/self-5.md \
Score: 91/125 \
Grader: Vish Jeyaraman


#### [56/85pts] PROGRAMMING
1. [0/20pts] Helpful and accurate self-eval. (Self eval turned in later)
2. [15/15pts] 
   - [5/5pts] Data representation (including interpretation) of the result ('action'). 
   - [5/5pts] Signatures and purpose statments for 'dag' and 'ldasg'.
   - [5/5pts] Functionality of 'iterating a strategy'.
3. [6/15pts] Unit tests for 'dag' and 'ldasg' strategies.
   - Missing unit tests. Partial credit for honesty.
4. [15/15pts] Unit tests for strategy iteration functionality.
5. [20/20pts] Properly organize the strategy component.

#### [35/40pts] DESIGN `referee-state.md`
1. [20/20pts] Game state functionality.
  - [5/5] checking legality of a requested action of tile placements
  - [5/5] scoring a legal requested action of tile placements
  - [5/5] complete the turn using the newly computed map, score-change, and change to player order
  - [5/5] checking whether the game is over
2. [15/20pts] Calling order.
    A referee must call the above method in the following order :
    - [5/5]check legality
    - [0/5]determine score (order incorrect/unclear when scoring takes place and in which order)
    - [5/5]complete turn
    - [5/5]`is the game over` functionality is called once per turn or round.


Problem with self-eval URLs; deduct all self-eval accuracy points:
Linked to post-deadline commit 44fba4aab6e752fcc3392fc0990bb6b74c3e869a from 10-20 1:07 AM
