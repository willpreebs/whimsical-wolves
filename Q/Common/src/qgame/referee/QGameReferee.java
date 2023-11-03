package qgame.referee;

import java.util.List;

import qgame.player.Player;
import qgame.state.QGameState;

/**
 * Represents a QGameReferee, which acts like a function
 * that executes a game given a state and a list of players. It will
 * return the results of the game: including winners and rule-breakers.
 */
public interface QGameReferee {

  /**
   * Takes a given game state and plays the game to completion, returning the winners and players
   * who break rules.
   * @param state The game state to play on.
   * @param players The list of players in the game, which must be sorted in order of ascending
   *                age and must have between 2-4 players.
   * @return a GameResults object, which contains a list of players who've won the game and a list
   * of players who broke the rules. 
   * @throws IllegalStateException if the state is invalid such that
   * running a game is not possible given the list of players.
   */
  GameResults playGame(QGameState state, List<Player> players) throws IllegalStateException;
}
