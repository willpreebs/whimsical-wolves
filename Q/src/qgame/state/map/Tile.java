package qgame.state.map;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import qgame.util.ValidationUtil;

import static qgame.util.ValidationUtil.validateArg;

/**
 * A representation of a Q Game Tile. Holds all the current possible colors and possible shapes a
 * tile can have.
 */
public interface Tile {
  /**
   * An enum representing the possible colors of a tile in Q game.
   */
  enum Color {
    RED("red"), ORANGE("orange"), YELLOW("yellow"),
    GREEN("green"), BLUE("blue"), PURPLE("purple");

    private final String name;
    Color(String name) {
      this.name = name;
    }

    public String toString() {
      return this.name;
    }

    /**
     * FILL THIS IN
     * @param name
     * @return
     * @throws IllegalArgumentException
     */
    public static Color fromString(String name) throws IllegalArgumentException{
      ValidationUtil.nonNullObj(name, "Name cannot be null.");
      Map<String, Color> colors = new HashMap<>();
      Stream.of(Tile.Color.values()).forEach(val -> colors.put(val.toString(), val));
      validateArg(colors::containsKey, name, "Unsupported color");
      return colors.get(name);
    }
  }

  /**
   * An enum representing the possible shapes of a tile in Q game.
   */
  enum Shape{
    STAR("star"), EIGHT_STAR("8star"), DIAMOND("diamond"), SQUARE("square"), CIRCLE("circle"),
    CLOVER("clover");

    private final String name;

    Shape(String name) {
      this.name = name;
    }

    public String toString() {
      return this.name;
    }

    public static Shape fromString(String name) {
      ValidationUtil.nonNullObj(name, "Name cannot be null.");
      Map<String, Shape> shapes = new HashMap<>();
      Stream.of(Tile.Shape.values()).forEach(val -> shapes.put(val.toString(), val));
      validateArg(shapes::containsKey, name, "Unsupported shape");
      return shapes.get(name);
    }
  }

  /**
   * Gets the tile's color.
   *
   * @return Color The color of the tile
   */
  Color color();

  /**
   * Gets the tile's shape.
   *
   * @return Shape The shape of the tile
   */
  Shape shape();
}
