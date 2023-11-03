package qgame.state;

import qgame.state.map.Posn;
import qgame.state.map.Tile;

/**
 * A representation of a tile placement on the Q Game board.
 *
 * @param posn the position of the tile
 * @param tile the tile that is being placed
 */
public record Placement(Posn posn, Tile tile) {
}
