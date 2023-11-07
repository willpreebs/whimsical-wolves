package qgame.state;

import java.util.Collection;
import java.util.List;

import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.player.PlayerInfo;

/**
 * Contains all the information about the state of the Q Game that the referee knows.
 */
public interface IGameState {
  /**
   * Gets a list of players based on whose turn it currently is. The first player in this list is
   * the player whose turn it is, the second player in the list is the next up player, etc.
   *
   * @return list of players in chronological order of active turns
   */
  List<PlayerInfo> playerInformation();

  /**
   * Gets an immutable copy of the game map.
   * 
   * @return QGameMapState
   */
  IMap viewBoard();

  /**
   * Gets how many tiles the ref has left.
   * @return number of tiles the ref has left
   */
  Bag<Tile> refereeTiles();

  /**
   * Constructs a PlayerGameState from the current
   * QGameState.
   * @return all the information the current player should
   * know about the current game.
   */
  IPlayerGameState getCurrentPlayerState();

  /**
   * Moves the first player in the game to the end of the rotation.
   * @throws IllegalStateException if there are no players in the game.
   */
  void shiftCurrentToBack() throws IllegalStateException;

  /**
   * Adds a given number of points to the current player.
   * @param score The amount of points to give to the first player
   * @throws IllegalStateException if there are no players in the game.
   */
  void addScoreToCurrentPlayer(int score)  throws IllegalStateException;

  /**
   * Removes the first player in the game from the rotation.
   * @throws IllegalStateException if there are no players in the game.
   */
  void removeCurrentPlayer() throws IllegalStateException;

  /**
   * Assigns the list of tiles to the current player in the game.
   * @param tiles The tiles to be given to the current player.
   * @throws IllegalStateException When there are no players in the game.
   */
  void setCurrentPlayerHand(Bag<Tile> tiles) throws IllegalStateException;


  /**
   * Places a tile at the given position on the game state's map.
   * @param placement The tile.
   */
  void placeTile(Placement placement);

  /**
   * Gets a given number of tests
   * @param count
   * @return
   * @throws IllegalArgumentException
   */
  Collection<Tile> takeOutRefTiles(int count) throws IllegalArgumentException;

  void giveRefereeTiles(Bag<Tile> tiles) throws IllegalArgumentException;
}
