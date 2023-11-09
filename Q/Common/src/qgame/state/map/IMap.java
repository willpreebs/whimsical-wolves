package qgame.state.map;

import java.util.List;
import java.util.Map;

import qgame.state.Placement;

/**
 * A representation of the mutable state of the Observable Q Game board. Supports placing a
 * tile on the game map.
 */
public interface IMap {
  /**
   * Returns the tile in the board at the given position if it exists.
   *
   * @param posn The position of the tile being requested.
   * @return A tile stored in the board at the given position.
   * @throws IllegalArgumentException If there is no tile at the given position.
   */
  Tile getTileAtPosn(Posn posn) throws IllegalArgumentException;

  /**
   * Returns if the board has a tile at the given position.
   *
   * @param posn the location to check for a tile
   * @return boolean true if tile exists at posn
   */
  boolean posnHasTile(Posn posn);

  /**
   * Gets the current state of the board as a map. The key represents the Posn of a
   * tile, and value represents a tile placed there.
   * 
   * @return map the nested map representation of the board state
   */
  Map<Posn, Tile> getBoardState();

  /**
   * Return all positions that a tile can be placed in.
   *
   * @return A list of all positions where a tile can be placed.
   */
  List<Posn> validPositions();

  /**
   * Places a given tile at the given position on the representation of the Q Game Map
   *
   * @param placement The placement being made on the board.
   * @throws IllegalArgumentException If the tile can not be placed in the given position
   */
  void placeTile(Placement placement) throws IllegalArgumentException;

  void printMap();

  boolean equals(Object o);
}
