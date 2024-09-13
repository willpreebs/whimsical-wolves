package qgame.player;

import java.util.Collection;
import qgame.state.Bag;
import qgame.state.map.Tile;

/**
 * Represents a Player's information relating to the state of the game.
 * Includes their score and their tiles as well as their name as a unique
 * identifier of the player.
 */
public class PlayerInfo {

  private int score;
  private Bag<Tile> tiles;

  private String name;

  public PlayerInfo(int score, Collection<Tile> tiles, String name) {
    this.score = score;
    this.tiles = new Bag<Tile>(tiles);
    this.name = name;
  }

  public int getScore() {
    return this.score;
  }

  public String getName() {
    return this.name;
  }

  public void incrementScore(int amount) {
    this.score += amount;
  }

  public Bag<Tile> getTiles() {
    return new Bag<>(this.tiles);
  }

  public void setTiles(Bag<Tile> tiles) {
    this.tiles = (new Bag<>(tiles));
  }
}
