package qgame.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import qgame.player.PlayerInfo;
import qgame.state.map.QMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;

/**
 * Contains all the information about the game that should be known
 * by the current player.
 */
public class QPlayerGameState implements IPlayerGameState {

  private final List<Integer> allScores;
  private final QMap board;
  private final int refTileCount;
  private final PlayerInfo info;

  public QPlayerGameState(List<Integer> scores, QMap board, int refTileCount,
                              Collection<Tile> playerTiles, String playerName) {
    this(scores, board, refTileCount, new Bag<>(playerTiles), playerName);
  }

  public QPlayerGameState(List<Integer> scores, QMap board, int refTileCount,
                              Bag<Tile> playerTiles, String playerName) {
    this(scores, board, refTileCount, new PlayerInfo(refTileCount, playerTiles.getItems(), playerName));
  }

  public QPlayerGameState(List<Integer> scores, QMap board, int refTileCount, PlayerInfo info) {
    this.allScores = new ArrayList<>(scores);
    this.board = new QMap(board.getBoardState());
    this.refTileCount = refTileCount;
    this.info = info;
  }

  @Override
  public List<Integer> getPlayerScores() {
    return new ArrayList<>(allScores);
  }

  @Override
  public QMap getBoard() {
    return new QMap(this.board.getBoardState());
  }

  @Override
  public int getNumberRemainingTiles() {
    return refTileCount;
  }

  @Override
  public Bag<Tile> getCurrentPlayerTiles() {
    return new Bag<>(this.info.getTiles());
  }

  /**
   * Mutates the board by making the given Placement
   * and removes the Placement's tile from this Player's list of tiles.
   */
  @Override
  public void makePlacement(Placement placement) {
    this.info.getTiles().remove(placement.tile());
    this.board.placeTile(placement);
  }

  @Override
  public String getPlayerName() {
    return this.info.getName();
  }
}
