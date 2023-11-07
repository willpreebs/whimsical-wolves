Pair: salty-wolves \
Commit: [8d833c6c77ce2813d3cb47f33b31d841217c4822](https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/tree/8d833c6c77ce2813d3cb47f33b31d841217c4822) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F23/salty-wolves/blob/1bfb9d8b246691eb575d6b6017e59ab1fdfc9af9/4/self-4.md \
Score: 60/70 \
Grader: Vish Jeyaraman


#### [55/60pts] PROGRAMMING
1. [20/20pts] Helpful and accurate self-eval.
2. [5/5pts] Singatures and purpose statements of the method: 'rendering the referee state'.
3. [5/5pts] Singatures and purpose statements of the method: 'scoring a placement'.
4. [5/10pts] Unit tests for 'scoring a placement'.
   - [5/5pts] Unit tests cover the case when a player completes a Q and receives bonus. 
   - [0/5pts] Unit tests cover the case when there is no bonus.
      - This test case seems to be missing 
5. [20/20pts] Factoring functionality for 'scoring a placement'.

#### [5/10pts] DESIGN `player-protocol.md`
1. [5/10pts] A referee must call the following methods in order: 'take turn' then 'accept tiles (optional)'.
   - This milestone asked for communications between referee and player, not server and client.
   - The referee should call player to take turn rather than wait for client to submit actions.  
   
Comment:  

