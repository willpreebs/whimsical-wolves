package qgame.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.player.PlayerInfo;

public class QStateBuilder implements IStateBuilder{
  private final Map<Posn, Tile> board;
  private final Bag<Tile> refTiles;
  private final List<PlayerInfo> playerInfos;


  public QStateBuilder() {
    this.board = new HashMap<>();
    this.refTiles = new Bag<>();
    this.playerInfos = new ArrayList<>();
  }


  @Override
  public IStateBuilder placeTiles(Placement... placements) {
    for (Placement(Posn posn, Tile tile) : placements) {
      this.board.put(posn, tile);
    }
    return this;
  }

  @Override
  public IStateBuilder placeTile(Posn posn, Tile tile) {
    this.board.put(posn, tile);
    return this;
  }

  @Override
  public IStateBuilder addTileBag(Tile... bag) {
    this.refTiles.addAll(List.of(bag));
    return this;
  }

  @Override
  public IStateBuilder addPlayerInfo(PlayerInfo... infos) {
    this.playerInfos.addAll(List.of(infos));
    return this;
  }

  public IGameState build() {
    return new QGameState(new QMap(board), refTiles, playerInfos);
  }
}
