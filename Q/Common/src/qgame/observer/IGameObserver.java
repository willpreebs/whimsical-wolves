package qgame.observer;

import qgame.state.IGameState;

public interface IGameObserver {

  /**
   * Render the next state if available.
   * If there are no later states available, then the GUI is not changed
   */
  public void next();

  /**
   * Render the previous state if available.
   * If there are no earlier states available, then the GUI is not changed
   */
  public void previous();

  /**
   * Saves the current state formatted as a JState
   * at the given filepath (root is at the lowest level in the repository's directory)
   */
  public void save(String filepath);

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
