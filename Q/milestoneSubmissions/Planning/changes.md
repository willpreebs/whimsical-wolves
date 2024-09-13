Possible Rule Changes

To: CEO of the Q Company

From: salty-wolves

Date: Nov. 1, 2023

Subject: Possible Rule Changes

- "Changing the bonus points again and allow more players to participate in a game" \
    We would consider this change to be a 1, since our Q scoring rule and game end bonus are
    both already parameterized, so the only change would be what value is passed to the constructor
    of one of our rules, and our refree.
- "Adding wildcard tiles" \
    We would consider this change a 3. Currently our tile interface just has methods 
    for determining the shapes and colors, but really we should have comparison methods between tiles,
    allowing for a wildcard to act as the same as any other tile.

- imposing restrictions that enforce the rules of Qwirkle instead of Q.
    We would consider this change a 3. Our referee component is not flexible enough to easily
    support several different rules in Qwirkle, so there would need to be a number of smaller changes,
    which would total to a medium sized load of work.

