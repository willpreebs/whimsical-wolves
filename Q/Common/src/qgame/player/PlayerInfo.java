package qgame.player;

import java.util.Collection;
import qgame.state.Bag;
import qgame.state.map.Tile;

import static qgame.util.ValidationUtil.nonNullObj;

public class PlayerInfo {

  private int score;
  private Bag<Tile> tiles;

  private String name;

  public PlayerInfo(int score, Collection<Tile> tiles, String name) {
    this.score = score;
    this.tiles = new Bag<Tile>(tiles);
    this.name = name;
  }


  public int score() {
    return this.score;
  }

  public String name() {
    return this.name;
  }

  public void incrementScore(int amount) {
    this.score += amount;
  }

  public Bag<Tile> tiles() {
    return new Bag<>(this.tiles);
  }

  public void setTiles(Bag<Tile> tiles) {
    this.tiles = (new Bag<>(tiles));
  }

}
