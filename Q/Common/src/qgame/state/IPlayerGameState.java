package qgame.state;


import java.util.List;

import qgame.state.map.QMap;
import qgame.state.map.Tile;

/**
 * Represents the information in a QGameState that a player has access to.
 */
public interface IPlayerGameState {

  /**
   * Gets a list of the scores of all the players in this game. The first player in this list is
   * the player whose turn it is, the second player in the list is the next up player, etc.
   *
   * @return list of players in chronological order of active turns
   */
  List<Integer> getPlayerScores();


  /**
   * Gets a QGameMapState (immutable) that represent the current QGameMap
   *
   * @return QGameMapState
   */
  QMap getBoard();

  /**
   * Gets how many tiles the ref has left.
   * @return number of tiles the ref has left
   */
  int getNumberRemainingTiles();

  /**
   * Gets a new bag containing all of this player's tiles
   * @return
   */
  Bag<Tile> getCurrentPlayerTiles();

  /**
   * 
   * @param placement
   */
  void makePlacement(Placement placement);

  String getPlayerName();
}
