package qgame.rule.scoring;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.state.Placement;
import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;

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

  ScoringRule owned = new PlaceAllOwnedTiles(1, 6);
  ScoringRule perTile = new PointPerTileRule();
  ScoringRule contiguousTile = new PointPerContiguousSequenceRule();
  ScoringRule qRule = new QRule(6);
  ScoringRule multi = new MultiScoringRule(perTile, contiguousTile, qRule);

  QGameMap allOwnedMap;

  QGameMap oneQMap;

  QGameMap twoQMap;

  QGameMap multConsecutiveSeqMap;

  QGameMap oneTile;

  List<Placement> placements1;
  List<Placement> placements2;
  List<Placement> placements3;
  List<Placement> placements4;
  List<Placement> placements5;

  @Before
  public void init() {
    allOwnedMap = new QGameMapImpl(new TileImpl(purple, clover), new Posn(0, 0));


    placements1 = new ArrayList<>();
    placements1.add(new Placement(new Posn(0, 1), new TileImpl(purple, star)));
    placements1.add(new Placement(new Posn(0, -1), new TileImpl(purple, square)));

    Map<Posn, Tile> oneQTiles = new HashMap<>();
    oneQTiles.put(new Posn(-5, 0), new TileImpl(orange, square));
    oneQTiles.put(new Posn(-4, 0), new TileImpl(orange, diamond));
    oneQTiles.put(new Posn(-3, 0), new TileImpl(orange, star));
    oneQTiles.put(new Posn(-2, 0), new TileImpl(orange, circle));
    oneQTiles.put(new Posn(-1, 0), new TileImpl(orange, eight));

    oneQMap = new QGameMapImpl(oneQTiles);

    placements2 = new ArrayList<>();
    placements2.add(new Placement(new Posn(-6, 0), new TileImpl(orange, clover)));



    Map<Posn, Tile> twoQTiles = new HashMap<>();
    twoQTiles.put(new Posn(-5, 0), new TileImpl(red, circle));
    twoQTiles.put(new Posn(-4, 0), new TileImpl(orange, circle));
    twoQTiles.put(new Posn(-3, 0), new TileImpl(blue, circle));
    twoQTiles.put(new Posn(-2, 0), new TileImpl(green, circle));
    twoQTiles.put(new Posn(-1, 0), new TileImpl(purple, circle));
    twoQTiles.put(new Posn(-1, -1), new TileImpl(purple, diamond));

    twoQTiles.put(new Posn(0, -5), new TileImpl(yellow, clover));
    twoQTiles.put(new Posn(0, -4), new TileImpl(yellow, square));
    twoQTiles.put(new Posn(0, -3), new TileImpl(yellow, star));
    twoQTiles.put(new Posn(0, -2), new TileImpl(yellow, eight));
    twoQTiles.put(new Posn(0, -1), new TileImpl(yellow, diamond));
    twoQMap = new QGameMapImpl(twoQTiles);

    placements3 = new ArrayList<>();
    placements3.add(new Placement(new Posn(-6, 0), new TileImpl(orange, clover)));
    placements3.add(new Placement(new Posn(0, 0), new TileImpl(orange, square)));

    Map<Posn, Tile> multConsecutiveTiles = new HashMap<>();
    multConsecutiveTiles.put(new Posn(0, 0), new TileImpl(red, star));
    multConsecutiveTiles.put(new Posn(0, 1), new TileImpl(orange, star));
    multConsecutiveTiles.put(new Posn(0, 2), new TileImpl(green, star));
    multConsecutiveSeqMap = new QGameMapImpl(multConsecutiveTiles);

    placements4 = new ArrayList<>();
    placements4.add(new Placement(new Posn(1, 0), new TileImpl(red, circle)));
    placements4.add(new Placement(new Posn(1, 1), new TileImpl(orange, circle)));
    placements4.add(new Placement(new Posn(1, 2), new TileImpl(green, circle)));

    Map<Posn, Tile> oneMap = new HashMap<>();
    oneMap.put(new Posn(0, 0), new TileImpl(purple, eight));
    oneTile = new QGameMapImpl(oneMap);
    placements5 = new ArrayList<>();
    placements5.add(new Placement(new Posn(1, 0), new TileImpl(purple, clover)));

  }


  @Test
  public void testAllOwned1() {
    placements1.forEach(allOwnedMap::placeTile);
    int perScore = perTile.pointsFor(placements1, allOwnedMap);
    int seqScore = contiguousTile.pointsFor(placements1, allOwnedMap);
    int qScore = qRule.pointsFor(placements1, allOwnedMap);

//    assertEquals(6, ownedScore);
    assertEquals(2, perScore);
    assertEquals(3, seqScore);
    assertEquals(0, qScore);

    int totalScore = multi.pointsFor(placements1, allOwnedMap);

    assertEquals(5, totalScore);
  }

  @Test
  public void testOneQ1() {
    placements2.forEach(oneQMap::placeTile);
//    int ownedScore = owned.pointsFor(placements2, oneQState);
    int perScore = perTile.pointsFor(placements2, oneQMap);
    int seqScore = contiguousTile.pointsFor(placements2, oneQMap);
    int qScore = qRule.pointsFor(placements2, oneQMap);

//    assertEquals(0, ownedScore);
    assertEquals(1, perScore);
    assertEquals(6, seqScore);
    assertEquals(6, qScore);
    int totalScore = multi.pointsFor(placements2, oneQMap);
    assertEquals(13, totalScore);
  }

  @Test
  public void testOneQDoesNotCountWith7() {
    placements3.forEach(oneQMap::placeTile);
    int perScore = perTile.pointsFor(placements3, oneQMap);
    int seqScore = contiguousTile.pointsFor(placements3, oneQMap);
    int qScore = qRule.pointsFor(placements3, oneQMap);

//    assertEquals(6, ownedScore);
    assertEquals(2, perScore);
    assertEquals(7, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements3, oneQMap);
    assertEquals(9, totalScore);
  }

  @Test
  public void testOneQ2() {
    placements2.forEach(oneQMap::placeTile);
//    int ownedScore = owned.pointsFor(placements2, oneQState2);
    int perScore = perTile.pointsFor(placements2, oneQMap);
    int seqScore = contiguousTile.pointsFor(placements2, oneQMap);
    int qScore = qRule.pointsFor(placements2, oneQMap);

//    assertEquals(6, ownedScore);
    assertEquals(1, perScore);
    assertEquals(6, seqScore);
    assertEquals(6, qScore);
    int totalScore = multi.pointsFor(placements2, oneQMap);
    assertEquals(13, totalScore);
  }

  @Test
  public void testMultConsecutiveSeq() {
    placements4.forEach(multConsecutiveSeqMap::placeTile);
    int perScore = perTile.pointsFor(placements4, multConsecutiveSeqMap);
    int seqScore = contiguousTile.pointsFor(placements4, multConsecutiveSeqMap);
    int qScore = qRule.pointsFor(placements4, multConsecutiveSeqMap);

    assertEquals(3, perScore);
    assertEquals(9, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements4, multConsecutiveSeqMap);
    assertEquals(12, totalScore);
  }

  @Test
  public void testSimple() {
    placements5.forEach(oneTile::placeTile);
    int perScore = perTile.pointsFor(placements5, oneTile);
    int seqScore = contiguousTile.pointsFor(placements5, oneTile);
    int qScore = qRule.pointsFor(placements5, oneTile);

    assertEquals(1, perScore);
    assertEquals(2, seqScore);
    assertEquals(0, qScore);
    int totalScore = multi.pointsFor(placements5, oneTile);
    assertEquals(3, totalScore);
  }

  @Test
  public void testFailingInstructorTest() {
    Map<Posn, Tile> map = new HashMap<>();
    map.put(new Posn(-1, 3), new TileImpl(purple, star));
    map.put(new Posn(0, 2), new TileImpl(green, eight));
    map.put(new Posn(0, 3), new TileImpl(green, star));
    map.put(new Posn(-1, 2), new TileImpl(purple, eight));

    List<Placement> placements = new ArrayList<>(
      List.of(new Placement(new Posn(-1, 2), new TileImpl(purple, eight))));

    int points = multi.pointsFor(placements, new QGameMapImpl(map));
    assertEquals(5, points);

  }
}
