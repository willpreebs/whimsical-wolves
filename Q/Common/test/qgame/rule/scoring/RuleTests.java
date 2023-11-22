package qgame.rule.scoring;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.observer.IGameObserver;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.state.map.QTile;

import static junit.framework.TestCase.assertEquals;


public class RuleTests {

  Tile.Color purple = Tile.Color.PURPLE;
  Tile.Color orange = Tile.Color.ORANGE;
  Tile.Color blue = Tile.Color.BLUE;
  Tile.Color red = Tile.Color.RED;
  Tile.Color yellow = Tile.Color.YELLOW;
  Tile.Color green = Tile.Color.GREEN;

  Tile.Shape circle = Tile.Shape.CIRCLE;
  Tile.Shape clover = Tile.Shape.CLOVER;
  Tile.Shape square = Tile.Shape.SQUARE;
  Tile.Shape diamond = Tile.Shape.DIAMOND;
  Tile.Shape star = Tile.Shape.STAR;
  Tile.Shape eight = Tile.Shape.EIGHT_STAR;

  ScoringRule owned = new PlaceAllOwnedTiles(6);
  ScoringRule perTile = new PointPerTileRule(1);
  ScoringRule contiguousTile = new PointPerContiguousSequenceRule(1);
  ScoringRule qRule = new QRule(6);
  ScoringRule multi = new MultiScoringRule(perTile, contiguousTile, qRule);

  IMap allOwnedMap;

  IMap oneQMap;

  IMap twoQMap;

  IMap multConsecutiveSeqMap;

  IMap oneTile;

  List<Placement> placements1;
  List<Placement> placements2;
  List<Placement> placements3;
  List<Placement> placements4;
  List<Placement> placements5;

  @Before
  public void init() {
    allOwnedMap = new QMap(new QTile(purple, clover), new Posn(0, 0));


    placements1 = new ArrayList<>();
    placements1.add(new Placement(new Posn(0, 1), new QTile(purple, star)));
    placements1.add(new Placement(new Posn(0, -1), new QTile(purple, square)));

    Map<Posn, Tile> oneQTiles = new HashMap<>();
    oneQTiles.put(new Posn(-5, 0), new QTile(orange, square));
    oneQTiles.put(new Posn(-4, 0), new QTile(orange, diamond));
    oneQTiles.put(new Posn(-3, 0), new QTile(orange, star));
    oneQTiles.put(new Posn(-2, 0), new QTile(orange, circle));
    oneQTiles.put(new Posn(-1, 0), new QTile(orange, eight));

    oneQMap = new QMap(oneQTiles);

    placements2 = new ArrayList<>();
    placements2.add(new Placement(new Posn(-6, 0), new QTile(orange, clover)));



    Map<Posn, Tile> twoQTiles = new HashMap<>();
    twoQTiles.put(new Posn(-5, 0), new QTile(red, circle));
    twoQTiles.put(new Posn(-4, 0), new QTile(orange, circle));
    twoQTiles.put(new Posn(-3, 0), new QTile(blue, circle));
    twoQTiles.put(new Posn(-2, 0), new QTile(green, circle));
    twoQTiles.put(new Posn(-1, 0), new QTile(purple, circle));
    twoQTiles.put(new Posn(-1, -1), new QTile(purple, diamond));

    twoQTiles.put(new Posn(0, -5), new QTile(yellow, clover));
    twoQTiles.put(new Posn(0, -4), new QTile(yellow, square));
    twoQTiles.put(new Posn(0, -3), new QTile(yellow, star));
    twoQTiles.put(new Posn(0, -2), new QTile(yellow, eight));
    twoQTiles.put(new Posn(0, -1), new QTile(yellow, diamond));
    twoQMap = new QMap(twoQTiles);

    placements3 = new ArrayList<>();
    placements3.add(new Placement(new Posn(-6, 0), new QTile(orange, clover)));
    placements3.add(new Placement(new Posn(0, 0), new QTile(orange, square)));

    Map<Posn, Tile> multConsecutiveTiles = new HashMap<>();
    multConsecutiveTiles.put(new Posn(0, 0), new QTile(red, star));
    multConsecutiveTiles.put(new Posn(0, 1), new QTile(orange, star));
    multConsecutiveTiles.put(new Posn(0, 2), new QTile(green, star));
    multConsecutiveSeqMap = new QMap(multConsecutiveTiles);

    placements4 = new ArrayList<>();
    placements4.add(new Placement(new Posn(1, 0), new QTile(red, circle)));
    placements4.add(new Placement(new Posn(1, 1), new QTile(orange, circle)));
    placements4.add(new Placement(new Posn(1, 2), new QTile(green, circle)));

    Map<Posn, Tile> oneMap = new HashMap<>();
    oneMap.put(new Posn(0, 0), new QTile(purple, eight));
    oneTile = new QMap(oneMap);
    placements5 = new ArrayList<>();
    placements5.add(new Placement(new Posn(1, 0), new QTile(purple, clover)));

  }


  @Test
  public void testAllOwned1() {
    placements1.forEach(allOwnedMap::placeTile);
    IGameState state = new QGameState(allOwnedMap);
    int perScore = perTile.pointsFor(placements1, state);
    int seqScore = contiguousTile.pointsFor(placements1, state);
    int qScore = qRule.pointsFor(placements1, state);

//    assertEquals(6, ownedScore);
    assertEquals(2, perScore);
    assertEquals(3, seqScore);
    assertEquals(0, qScore);

    int totalScore = multi.pointsFor(placements1, state);

    assertEquals(5, totalScore);
  }

  @Test
  public void testOneQ1() {
    placements2.forEach(oneQMap::placeTile);
    IGameState state = new QGameState(oneQMap);
//    int ownedScore = owned.pointsFor(placements2, state);
    int perScore = perTile.pointsFor(placements2, state);
    int seqScore = contiguousTile.pointsFor(placements2, state);
    int qScore = qRule.pointsFor(placements2, state);

//    assertEquals(0, ownedScore);
    assertEquals(1, perScore);
    assertEquals(6, seqScore);
    assertEquals(6, qScore);
    int totalScore = multi.pointsFor(placements2, state);
    assertEquals(13, totalScore);
  }

  @Test
  public void testOneQDoesNotCountWith7() {
    placements3.forEach(oneQMap::placeTile);
    IGameState state = new QGameState(oneQMap);
    int perScore = perTile.pointsFor(placements3, state);
    int seqScore = contiguousTile.pointsFor(placements3, state);
    int qScore = qRule.pointsFor(placements3, state);

//    assertEquals(6, ownedScore);
    assertEquals(2, perScore);
    assertEquals(7, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements3, state);
    assertEquals(9, totalScore);
  }

  @Test
  public void testOneQ2() {
    placements2.forEach(oneQMap::placeTile);
    IGameState state = new QGameState(oneQMap);
//    int ownedScore = owned.pointsFor(placements2, oneQState2);
    int perScore = perTile.pointsFor(placements2, state);
    int seqScore = contiguousTile.pointsFor(placements2, state);
    int qScore = qRule.pointsFor(placements2, state);

//    assertEquals(6, ownedScore);
    assertEquals(1, perScore);
    assertEquals(6, seqScore);
    assertEquals(6, qScore);
    int totalScore = multi.pointsFor(placements2, state);
    assertEquals(13, totalScore);
  }

  @Test
  public void testMultConsecutiveSeq() {
    placements4.forEach(multConsecutiveSeqMap::placeTile);
    IGameState state = new QGameState(multConsecutiveSeqMap);
    int perScore = perTile.pointsFor(placements4, state);
    int seqScore = contiguousTile.pointsFor(placements4, state);
    int qScore = qRule.pointsFor(placements4, state);

    assertEquals(3, perScore);
    assertEquals(9, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements4, state);
    assertEquals(12, totalScore);
  }

  @Test
  public void testSimple() {
    placements5.forEach(oneTile::placeTile);
    IGameState state = new QGameState(oneTile);
    int perScore = perTile.pointsFor(placements5, state);
    int seqScore = contiguousTile.pointsFor(placements5, state);
    int qScore = qRule.pointsFor(placements5, state);

    assertEquals(1, perScore);
    assertEquals(2, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements5, state);
    assertEquals(3, totalScore);
  }

  @Test
  public void testFailingInstructorTest() {
    Map<Posn, Tile> map = new HashMap<>();
    map.put(new Posn(-1, 3), new QTile(purple, star));
    map.put(new Posn(0, 2), new QTile(green, eight));
    map.put(new Posn(0, 3), new QTile(green, star));
    map.put(new Posn(-1, 2), new QTile(purple, eight));

    List<Placement> placements = new ArrayList<>(
      List.of(new Placement(new Posn(-1, 2), new QTile(purple, eight))));

    int points = multi.pointsFor(placements, new QGameState(new QMap(map)));
    assertEquals(5, points);

  }
}
