package qgame.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;

/**
 * Contains all the information about the game that should be known
 * by the current player.
 */
public class QPlayerGameState implements IPlayerGameState {

  private final List<Integer> scores;
  private final IMap board;
  private final int refTileCount;
  private final Bag<Tile> playerTiles;
  private final String playerName;

  public QPlayerGameState(List<Integer> scores, IMap board, int refTileCount,
                              Collection<Tile> playerTiles, String playerName) {
    this(scores, board, refTileCount, new Bag<>(playerTiles), playerName);
  }

  public QPlayerGameState(List<Integer> scores, IMap board, int refTileCount,
                              Bag<Tile> playerTiles, String playerName) {
    this.scores = new ArrayList<>(scores);
    this.board = new QMap(board.getBoardState());
    this.refTileCount = refTileCount;
    this.playerTiles = new Bag<>(playerTiles);
    this.playerName = playerName;
  }

  @Override
  public List<Integer> getPlayerScores() {
    return new ArrayList<>(scores);
  }

  @Override
  public IMap getBoard() {
    return new QMap(this.board.getBoardState());
  }

  @Override
  public int getNumberRemainingTiles() {
    return refTileCount;
  }

  @Override
  public Bag<Tile> getCurrentPlayerTiles() {
    return new Bag<>(this.playerTiles);
  }

  /**
   * Removes 
   */
  @Override
  public void makePlacement(Placement placement) {
    this.playerTiles.remove(new ArrayList<>(List.of(placement.tile())));
    this.board.placeTile(placement);
  }

  @Override
  public String getPlayerName() {
    return this.playerName;
  }

}
