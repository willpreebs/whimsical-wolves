package qgame.state.map;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import qgame.state.Placement;
import qgame.util.PosnUtil;
import qgame.util.ValidationUtil;

import static qgame.util.ValidationUtil.validateArg;

/**
 * An implementation of QGameMap. Supports checking if there is a tile at a given position,
 * getting the current board state, determining all the positions that extend upon previously
 * placed tiles, getting a tile at a given position, and extending the map by placing a tile next
 * to a neighbor.
 */
public class QMap implements IMap {
  private final Map<Posn, Tile> tileMap;

  /**
   * Constructor to create a QGame map from the start of the game. Generates a map.
   *
   * @param tile The starting tile for the game.
   * @param posn The position that the starting tile should be placed at.
   */
  public QMap(Tile tile, Posn posn) {
    ValidationUtil.nonNullObj(tile, "Tile cannot be null.");
    ValidationUtil.nonNullObj(posn, "Posn cannot be null.");
    this.tileMap = new HashMap<>();
    this.tileMap.put(posn, tile);
  }

  /**
   * Constructor to create a QGame map from a list of already placed tiles.
   *
   * @param tileMap A map of all used positions on the board and what tile mapped to the position
   */
  public QMap(Map<Posn, Tile> tileMap) {
    ValidationUtil.nonNullObj(tileMap, "Tile Map cannot be null.");
    this.tileMap = new HashMap<>(tileMap);
  }

  public boolean posnHasTile(Posn posn) {
    ValidationUtil.nonNullObj(posn, "Posn cannot be null");
    return this.tileMap.containsKey(posn);
  }
  

  @Override
  public Map<Posn, Tile> getBoardState() {
    return new HashMap<>(this.tileMap);
  }

  private Set<Posn> allNeighborsOfPosns() {
    return this.tileMap.keySet().stream().map(PosnUtil::neighbors)
      .flatMap(Collection::stream).collect(Collectors.toSet());
  }


  @Override
  public List<Posn> validPositions() {
    Set<Posn> acc = allNeighborsOfPosns();
    Predicate<Posn> posnIsEmpty = Predicate.not(this::posnHasTile);
    return acc.stream().filter(posnIsEmpty).toList();
  }

  public Tile getTileAtPosn(Posn posn) throws IllegalArgumentException {
    validateArg(this::posnHasTile, posn, "Position does not have a tile.");
    return tileMap.get(posn);
  }


  private boolean isNextToTile(Posn posn) {
    ValidationUtil.nonNullObj(posn, "Posn cannot be null");
    List<Posn> neighbors = PosnUtil.neighbors(posn);
    return neighbors.stream().anyMatch(this::posnHasTile);
  }

  @Override
  public void placeTile(Placement placement) throws IllegalArgumentException {
    ValidationUtil.nonNullObj(placement, "Placement cannot be null");
    Tile tile = placement.tile();
    Posn posn = placement.posn();
    ValidationUtil.nonNullObj(tile, "Tile cannot be null");
    ValidationUtil.nonNullObj(posn, "Posn cannot be null");
    validateArg(Predicate.not(this::posnHasTile), posn, "Position already has a tile.");
    this.tileMap.put(posn, tile);
  }

  @Override
  public void printMap() {

    Set<Posn> posns = this.getBoardState().keySet();
    int topRow = posns.stream().map(Posn::y).reduce(Integer.MAX_VALUE, Integer::min);
    int bottomRow = posns.stream().map(Posn::y).reduce(Integer.MIN_VALUE, Integer::max);
    int leftCol = posns.stream().map(Posn::x).reduce(Integer.MAX_VALUE, Integer::min);
    int rightCol = posns.stream().map(Posn::x).reduce(Integer.MIN_VALUE, Integer::max);


      System.out.println("Printing map ----------------");

      for (int row = bottomRow; row <= topRow; row++) {
          for (int col = leftCol; col <= rightCol; col++) {
            try {
              Tile tile = this.getTileAtPosn(new Posn(row, col));
              String simple = tile.color().toString().substring(0, 2) + 
              tile.shape().toString().substring(0, 2);
              System.out.print("[" + simple + "]");
            } catch (IllegalArgumentException e) {
              System.out.print("[    ]");
            }
          }
         System.out.println("");
      }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof IMap) {
      IMap other = (IMap) o;
      return this.tileMap.equals(other.getBoardState());
    }
    else {
      return false;
    }
  }
}
