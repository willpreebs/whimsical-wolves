package qgame.map;


public interface QGameMap extends QGameMapState {
  void placeTile(Tile tile, int row, int col) throws IllegalArgumentException;
}
