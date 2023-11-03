package qgame.map;

import java.util.List;
import java.util.Map;

/**
 * A representation of the state of a Q Game board. Supports getting all the tiles in the board,
 * checking if a tile is at a position, and accessing tiles at their positions. The coordinate
 * system of the Game State is the cartesian coordinate system, with the first tile in the board
 * being at (0, 0), and having all future placements being relative to that.
 */
public interface QGameMapState {
  /**
   * Returns the tile in the board at the given position if it exists.
   * @param posn The position of the tile being requested.
   * @return A tile stored in the board at the given position.
   * @throws IllegalArgumentException If there is no tile at the given position.
   */
  Tile getTileAtPosn(Posn posn) throws IllegalArgumentException;

  /**
   * Returns if the board has a tile at the given position.
   * @param posn
   * @return
   */
  boolean posnHasTile(Posn posn);
  Map<Integer, Map<Integer, Tile>> getBoardState();

  /**
   * Return all positions that the given tile can be placed in.
   * @param tile the tile to check for.
   * @return A list of all posns where the tile can be placed.
   */
  List<Posn> validPositions(Tile tile);
}
