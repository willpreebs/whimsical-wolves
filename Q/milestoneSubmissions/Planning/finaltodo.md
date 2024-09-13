# Currently known bugs:
1. Not in line cheat does not always produce a cheat when it should.
2. rule breakers may be out of order in the final game result when they
cause a problem (throw bug or timeout) on win.
   1. Could have to do with the order we are notifying them in the first place, or
   2. a subtler bug with the game