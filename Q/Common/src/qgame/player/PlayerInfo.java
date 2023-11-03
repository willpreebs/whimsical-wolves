package qgame.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import qgame.state.Bag;
import qgame.state.map.Tile;

import static qgame.util.ValidationUtil.nonNullObj;

public class PlayerInfo {

  private int score;
  private Bag<Tile> tiles;

  public PlayerInfo(int score, Collection<Tile> tiles) {
    if (score < 0) {
      throw new IllegalArgumentException("Score must be a natural number.");
    }
    nonNullObj(tiles, "tiles cannot be null.");
    this.score = score;
    this.tiles = new Bag<>(tiles);
  }

  public int score() {
    return this.score;
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
