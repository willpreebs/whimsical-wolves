//package qgame.map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertThrows;
//import static org.junit.Assert.assertTrue;
//
///**
// * A test class used to test the functionality of QGameMap methods.
// */
//public class QGameMapTest {
//  private QGameMap map1;
//
//  Tile.Color red = Tile.Color.RED;
//  Tile.Color orange = Tile.Color.ORANGE;
//  Tile.Color yellow = Tile.Color.YELLOW;
//  Tile.Color green = Tile.Color.GREEN;
//  Tile.Color blue = Tile.Color.BLUE;
//  Tile.Color purple = Tile.Color.PURPLE;
//
//
//  Tile.Shape circle = Tile.Shape.CIRCLE;
//  Tile.Shape square = Tile.Shape.SQUARE;
//  Tile.Shape diamond = Tile.Shape.DIAMOND;
//  Tile.Shape clover = Tile.Shape.CLOVER;
//  Tile.Shape star = Tile.Shape.STAR;
//  Tile.Shape eight = Tile.Shape.EIGHT_STAR;
//
//  @Before
//  public void init() {
//    map1 = new QGameMapImpl(new TileImpl(green, circle), new Posn(0, 0));
//  }
//
//  @Test
//  public void testConstructor1() {
//    assertTrue(map1.posnHasTile(new Posn(0, 0)));
//    assertFalse(map1.posnHasTile(new Posn(1, 0)));
//    assertFalse(map1.posnHasTile(new Posn(-1, 0)));
//    assertFalse(map1.posnHasTile(new Posn(0, 1)));
//    assertFalse(map1.posnHasTile(new Posn(0, -1)));
//    Map<Posn, Tile> map = map1.getBoardState();
//    assertEquals(1, map.size());
//    assertEquals(map.get(new Posn(0, 0)), new TileImpl(green, circle));
//  }
//
//  @Test
//  public void testConstructor2() {
//    assertTrue(map1.posnHasTile(new Posn(0, 0)));
//    assertFalse(map1.posnHasTile(new Posn(1, 0)));
//    assertFalse(map1.posnHasTile(new Posn(-1, 0)));
//    assertFalse(map1.posnHasTile(new Posn(0, 1)));
//    assertFalse(map1.posnHasTile(new Posn(0, -1)));
//    Map<Posn, Tile> map = map1.getBoardState();
//    assertEquals(1, map.size());
//    assertEquals(map.get(new Posn(0, 0)), new TileImpl(green, circle));
//  }
//
//  @Test
//  public void testConstructorErrors1() {
//    assertThrows(IllegalArgumentException.class,
//      () -> new QGameMapImpl(null, new Posn(1,1)));
//    assertThrows(IllegalArgumentException.class,
//      () -> new QGameMapImpl(new TileImpl(green, circle), null));
//    assertThrows(IllegalArgumentException.class,
//      () -> new QGameMapImpl(null, null));
//  }
//
//  @Test
//  public void testConstructorErrors2() {
//    assertThrows(IllegalArgumentException.class,
//      () -> new QGameMapImpl(null));
//    Map<Posn, Tile> badMap = new HashMap<>();
//    badMap.put(new Posn(0, 1), new TileImpl(red, star));
//    badMap.put(new Posn(0, -1), new TileImpl(red, star));
//    assertThrows(IllegalArgumentException.class,
//      () -> new QGameMapImpl(badMap));
//  }
//  @Test
//  public void testGetBoard1() {
//  }
//
//  private void testHasPosnWorksOnMap(int highestRow, int lowestRow, int leftmostCol,
//                                     int rightmostCol, QGameMap board) {
//    Map<Posn, Tile> map = board.getBoardState();
//    for (int y = highestRow; y < lowestRow; y++) {
//      for (int x = leftmostCol; x < rightmostCol; x++) {
//        Posn posn = new Posn(y, x);
//        if (map.containsKey(posn)) {
//          assertTrue(board.posnHasTile(posn));
//        }
//        else {
//          assertFalse(board.posnHasTile(posn));
//        }
//      }
//    }
//  }
//
//  @Test
//  public void testBoardStateMatchesPosns() {
//    map1.placeTile(new TileImpl(green, square), new Posn(-1, 0));
//    map1.placeTile(new TileImpl(red, Tile.Shape.DIAMOND), new Posn(0, -1));
//    map1.placeTile(new TileImpl(purple, clover), new Posn(0, 1));
//    map1.placeTile(new TileImpl(blue, eight), new Posn(1, 1));
//
//    Map<Posn, Tile> tileMap = map1.getBoardState();
//    for (Map.Entry<Posn, Tile> entry : tileMap.entrySet()) {
//      Posn posn = entry.getKey();
//      Tile tile = entry.getValue();
//      assertTrue(map1.posnHasTile(posn));
//      assertEquals(tile, map1.getTileAtPosn(posn));
//    }
//  }
//
//  @Test
//  public void testPlacingTile1() {
//    Tile redClover = new TileImpl(red, clover);
//    Posn zeroOne = new Posn(0, 1);
//
//    map1.placeTile(redClover, zeroOne);
//    assertEquals(redClover, map1.getTileAtPosn(zeroOne));
//    assertEquals(2, map1.getBoardState().size());
//
//    testHasPosnWorksOnMap(-20, 20, -20, 20, map1);
//    Tile blueStar = new TileImpl(blue, star);
//    Posn oneZero = new Posn(1, 1);
//
//    map1.placeTile(blueStar, oneZero);
//    assertEquals(blueStar, map1.getTileAtPosn(oneZero));
//    assertEquals(3, map1.getBoardState().size());
//    testHasPosnWorksOnMap(-20, 20, -20, 20, map1);
//  }
//
//  @Test
//  public void testNotMutable() {
//    Map<Posn, Tile> tileMap = map1.getBoardState();
//    assertEquals(1, tileMap.size());
//    tileMap.put(new Posn(0, 1), new TileImpl(orange, eight));
//
//    // Do not want the actual game maps's state to be changed
//    assertFalse(map1.posnHasTile(new Posn(0, 1)));
//    Map<Posn, Tile> tileMapClone = map1.getBoardState();
//    assertNotEquals(tileMap, tileMapClone);
//  }
//}
