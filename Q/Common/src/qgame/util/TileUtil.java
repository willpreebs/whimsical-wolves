package qgame.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import qgame.state.map.Tile;
import qgame.state.map.QTile;
import qgame.state.Bag;
import qgame.state.Placement;

import static qgame.util.ValidationUtil.validateArg;

/**
 * Utility class that applies general methods to Tile objects.
 */
public class TileUtil {


  private static Map<Tile.Shape, Integer> createLexShapeOrdering() {
    Map<Tile.Shape, Integer> shapeMap = new HashMap<>();
    shapeMap.put(Tile.Shape.STAR, 0);
    shapeMap.put(Tile.Shape.EIGHT_STAR, 1);
    shapeMap.put(Tile.Shape.SQUARE, 2);
    shapeMap.put(Tile.Shape.CIRCLE, 3);
    shapeMap.put(Tile.Shape.CLOVER, 4);
    shapeMap.put(Tile.Shape.DIAMOND, 5);
    return shapeMap;
  }
  /**
   *  Compares two tiles and returns the lesser of the two tiles. Tiles
   *  are ordered by their shape and color through the following ranking from
   *  lowest to highest:
   *  Shape: "star", "8star", "square", "circle", "clover", "diamond"
   *  Color: "red", "green", "blue", "yellow", "orange", "purple"
   *
   *  Tiles are ordered first by shape, and if matching shape, then by color.
   * @param tile1 The first tile we are comparing
   * @param tile2 The second tile we are comparing
   * @return an integer representing the difference in the lexicographical ordering of the tiles.
   */
  public static int smallestTile(Tile tile1, Tile tile2) {
    Map<Tile.Shape, Integer> shapeMap = createLexShapeOrdering();
    Map<Tile.Color, Integer> colorMap = createLexColorOrdering();
    int shapeComp = shapeMap.get(tile1.shape()) - shapeMap.get(tile2.shape());
    int colorComp = colorMap.get(tile1.color()) - colorMap.get(tile2.color());
    if(shapeComp == 0){
      return colorComp;
    }
    else{
      return shapeComp;
    }
  }

  /**
   * Return the lexicographically smallest tile in a collection of tiles.
   * The list of tiles should have at least one element.
   * @param tileList The list of tiles to search through
   * @return The lexicographically smallest tile in the list.
   * @throws IllegalArgumentException if the list lacks any tiles.
   */
  public static Tile smallestTileInList(List<Tile> tileList) throws IllegalArgumentException {
    validateArg(Predicate.not(List::isEmpty), tileList, "List of tiles cannot be empty.");
    List<Tile> copiedList = new ArrayList<>(tileList);
    copiedList.sort(TileUtil::smallestTile);
    return copiedList.get(0);
  }

  private static Map<Tile.Color, Integer> createLexColorOrdering(){
    Map<Tile.Color, Integer> colorMap = new HashMap<>();
    colorMap.put(Tile.Color.RED, 0);
    colorMap.put(Tile.Color.GREEN, 1);
    colorMap.put(Tile.Color.BLUE, 2);
    colorMap.put(Tile.Color.YELLOW, 3);
    colorMap.put(Tile.Color.ORANGE, 4);
    colorMap.put(Tile.Color.PURPLE, 5);
    return colorMap;
  }

  public static List<Tile> placementsToTiles(List<Placement> placements) {
    return placements
      .stream()
      .map(Placement::tile)
      .toList();
  }

  public static boolean sameColor(Tile tile1, Tile tile2) {
    return tile1.color() == tile2.color();
  }

  public static boolean sameShape(Tile tile1, Tile tile2) {
    return tile1.shape() == tile2.shape();
  }

  public static Bag<Tile> getTileBag(int numTiles) {
    Tile.Shape[] shapes = Tile.Shape.values();
    Tile.Color[] colors = Tile.Color.values();

    List<Tile> bag = new ArrayList<>();
    for (int i = 0; i < numTiles; i++) {
      i %= colors.length * shapes.length;
      Tile t = new QTile(colors[i % colors.length], shapes[i / shapes.length]);
      bag.add(t);
    }
    Collections.shuffle(bag);
    return new Bag<>(bag);
  }
}
