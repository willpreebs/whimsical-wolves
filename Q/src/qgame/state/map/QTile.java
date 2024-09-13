package qgame.state.map;

/**
 * An implementation of Tile. Supports getting a tile's shape and getting a tile's color.
 */
public record QTile(Color color, Shape shape) implements Tile {
}
