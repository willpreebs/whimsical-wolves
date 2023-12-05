# Bug fixes
- Fix bug with player disconnecting at the end of the game prematurely - Will
- Investigate and fix bugs causing test failures in 9. - If time

# Design TOP PRIORITY
- Split JsonConverter into two classes: one for converting into json and one for converting into objects - Will
- Clean up QReferee constructors (Use RefereeConfig, abstract out initializations) - Will
- RuleUtil.createScoringRules take in config object - Will
- playGame should check that the number of players matches the number in the state and returns an empty game result otherwise. - Ethan
- playRound should return false on all turns being pass/exchange end condition. - Ethan
- clean up findWinnersAndNotifyPlayers call stack - Ethan
- Implement gameOver in QGameObserver (when does the observer shut down?) - Will

# Documentation
- Generally go over all comments to make sure they are still accurate / lack gaps - both
- QReferee (class and playGame) - Will
- Readme.md with entire overview, guides for new developers, guide for running the server/client, component diagram- Ethan
- IGameState & QGameState - Will
- GUI and observer Docs + How can an observer interact with the game / make a GUI - Ethan

# Tests
- Server/Client interactions - Will
  - Proxy Player/ Proxy Ref interactions

# Diagrams
- Component diagram (for Readme.md) - Ethan

# Demo
- Prepare test to showcase server/client interaction and observer/gui - Will



