package qgame.observer;

import qgame.state.IGameState;

/**
 * A representation of an "observer" - which is a debug tool that given a series
 * of Q Game States, can allow for dynamic viewing through changes in state of
 * the QGame using a GUI. States can also be saved as images locally where the game
 * is ran at a specified file path.
 */
public interface IGameObserver {

  /**
   * Render the next state if available.
   * If there are no later states available, then the GUI is not changed
   */
   void next();

  /**
   * Render the previous state if available.
   * If there are no earlier states available, then the GUI is not changed
   */
   void previous();

  /**
   * Saves the current state formatted as a JState
   * at the given filepath (root is at the lowest level in the repository's directory)
   */
   void save(String filepath);

  /**
   * Gives the observer the game state and saves a GUI representation of the state
   * in an image
   * @param state
   */
  void receiveState(IGameState state);

  /**
   * Alerts the observer that the game is over
   * and that there will be no more states.
   */
  void gameOver();


}
