package qgame.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.state.map.Posn;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.player.PlayerInfo;

public class BasicQGameStateBuilder implements StateBuilder{
  private final Map<Posn, Tile> board;
  private final Bag<Tile> refTiles;
  private final List<PlayerInfo> playerInfos;


  public BasicQGameStateBuilder() {
    this.board = new HashMap<>();
    this.refTiles = new Bag<>();
    this.playerInfos = new ArrayList<>();
  }


  @Override
  public StateBuilder placeTiles(Placement... placements) {
    for (Placement(Posn posn, Tile tile) : placements) {
      this.board.put(posn, tile);
    }
    return this;
  }

  @Override
  public StateBuilder placeTile(Posn posn, Tile tile) {
    this.board.put(posn, tile);
    return this;
  }

  @Override
  public StateBuilder addTileBag(Tile... bag) {
    this.refTiles.addAll(List.of(bag));
    return this;
  }

  @Override
  public StateBuilder addPlayerInfo(PlayerInfo... infos) {
    this.playerInfos.addAll(List.of(infos));
    return this;
  }

  public QGameState build() {
    return new BasicQGameState(new QGameMapImpl(board), refTiles, playerInfos);
  }
}
