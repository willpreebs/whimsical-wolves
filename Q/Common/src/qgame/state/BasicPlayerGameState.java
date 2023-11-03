package qgame.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;

/**
 * Contains all the information about the game that should be known
 * by the current player.
 */
public class BasicPlayerGameState implements PlayerGameState {

  private final List<Integer> scores;
  private final QGameMap board;
  private final int refTileCount;
  private final Bag<Tile> playerTiles;

  public BasicPlayerGameState(List<Integer> scores, QGameMap board, int refTileCount,
                              Collection<Tile> playerTiles) {
    this(scores, board, refTileCount, new Bag<>(playerTiles));
  }

  public BasicPlayerGameState(List<Integer> scores, QGameMap board, int refTileCount,
                              Bag<Tile> playerTiles) {
    this.scores = new ArrayList<>(scores);
    this.board = new QGameMapImpl(board.getBoardState());
    this.refTileCount = refTileCount;
    this.playerTiles = new Bag<>(playerTiles);
  }

  @Override
  public List<Integer> playerScores() {
    return new ArrayList<>(scores);
  }

  @Override
  public QGameMap viewBoard() {
    return new QGameMapImpl(this.board.getBoardState());
  }

  @Override
  public int remainingTiles() {
    return refTileCount;
  }

  @Override
  public Bag<Tile> getCurrentPlayerTiles() {
    return new Bag<>(this.playerTiles);
  }

  @Override
  public void makePlacement(Placement placement) {
    this.playerTiles.remove(new ArrayList<>(List.of(placement.tile())));
    this.board.placeTile(placement);
  }

}
