package qgame.observer;

import java.util.List;

import qgame.state.map.IMap;
import qgame.referee.GameResults;
import qgame.state.IGameState;

public interface IGameObserver {
  // void receiveState(IGameState state);

  // void endGame(GameResults results);

  // List<Integer> viewScores();

  // IMap viewBoard();

  // List<String> turnOrder();

  // List<IGameState> gameStates();

  // boolean isGameOver();

  // GameResults results();

  /**
   * Gives the observer the game state
   * @param state
   */
  void receiveState(IGameState state);

  /**
   * Alerts the observer that the game is over
   * and that there will be no more states.
   */
  void gameOver();


}
