//package qgame.state;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import qgame.TestUtil;
//import qgame.state.map.Posn;
//import qgame.state.map.QGameMap;
//import qgame.state.map.QGameMapImpl;
//import qgame.state.map.Tile;
//import qgame.state.map.TileImpl;
//import qgame.player.PlayerInfo;
//
//import static org.junit.Assert.*;
//
//public class QGameStateTest {
//
//  private QGameState simpleBoard;
//  private QGameState tenPieceBoard;
//
//
//  @Before
//  public void init() {
//    Map<Tile.Color, Map<Tile.Shape, Tile>> allColors = TestUtil.allTiles();
//
//    QGameMap map = new QGameMapImpl(
//      allColors.get(Tile.Color.ORANGE).get(Tile.Shape.EIGHT_STAR),
//      new Posn(4, 3));
//    List<PlayerInfo> players1 = new ArrayList<>();
//    players1.add(
//      new PlayerInfo(10, TileConstructor.createTiles("r ci g sq o d y cl")));
//    players1.add(
//      new PlayerInfo(4, TileConstructor.createTiles("g e")));
//    simpleBoard = new BasicQGameState(map, new ArrayList<>(), players1);
//
//
//    QGameMap map2 = new QGameMapImpl(
//      new TileImpl(Tile.Color.ORANGE, Tile.Shape.EIGHT_STAR),
//      new Posn(-3, 0));
//    List<Tile> refTiles =
//      TileConstructor.createTiles("p st y sq o d g e r ci b cl o cl p e g sq b d");
//    List<PlayerInfo> players2 = new ArrayList<>();
//    players2.add(
//      new PlayerInfo(4, TileConstructor.createTiles("g e")));
//    players2.add(
//      new PlayerInfo(10, TileConstructor.createTiles("r ci g sq o d y cl")));
//
//    tenPieceBoard = new BasicQGameState(map2, refTiles, players2);
//  }
//
//  @Test
//  public void simplePlayerInformationTest() {
//    List<PlayerInfo> players = simpleBoard.playerInformation();
//    assertEquals(2, players.size());
//    PlayerInfo player1 = players.get(0);
//    assertEquals(10, player1.score());
//    List<Tile> player1Tiles = new ArrayList<>(player1.tiles().viewItems());
//    assertEquals(4, player1Tiles.size());
//    Set<Tile> ownedTiles1 = new HashSet<>(TileConstructor.createTiles("r ci g sq o d y cl"));
//    assertEquals(4, player1Tiles.stream().distinct().toList().size());
//    for (Tile tile : player1Tiles) {
//      assertTrue(ownedTiles1.contains(tile));
//    }
//    List<Tile> ownedTiles2 = TileConstructor.createTiles("g e");
//    PlayerInfo player2 = players.get(1);
//    List<Tile> player2Tiles = new ArrayList<>(player2.tiles().viewItems());
//    assertEquals(1, player2Tiles.size());
//    assertEquals(ownedTiles2.get(0), player2Tiles.get(0));
//  }
//
//  @Test
//  public void viewBoard() {
//  }
//
//  @Test
//  public void remainingTilesSimple() {
//    assertEquals(0, simpleBoard.refereeTiles().size());
//    assertEquals(10, tenPieceBoard.refereeTiles().size());
//  }
//
//  @Test
//  public void remainingTilesAfterMove() {
//    assertEquals(10, tenPieceBoard.refereeTiles().size());
//    assertEquals(4, tenPieceBoard.playerInformation().get(0).score());
//    assertEquals(10, tenPieceBoard.playerInformation().get(1).score());
////    tenPieceBoard.nextTurn(new PlaceAction(
////      List.of(new Placement(new Posn(-2, 0), new TileImpl(Tile.Color.PURPLE,
////        Tile.Shape.EIGHT_STAR)))));
////    assertEquals(10, tenPieceBoard.refereeTiles().size());
////    assertEquals(4, tenPieceBoard.playerInformation().get(0).score());
////    assertEquals(10, tenPieceBoard.playerInformation().get(1).score());
//
//
//  }
//
//  // Test shows placements succeeds when Q game extension rules are met.
//  @Test
//  public void makePlacements1() {
//    assertEquals(10, tenPieceBoard.playerInformation().get(1).score());
//    String placements = "b e -2 0 b ci -1 0 g ci 0 0 r ci 0 1 r sq 0 2";
////    tenPieceBoard.nextTurn(new PlaceAction(PlacementConstructor.createPlacements(placements)));
//  }
//  @Test
//
//  // Test placements fails when breaks Q game board extension rules
//  public void makePlacementsError() {
//    assertEquals(10, tenPieceBoard.playerInformation().get(1).score());
//    String placements = "b e -1 0 g ci 0 0 r ci 0 1 r sq 0 2";
////    assertThrows(IllegalArgumentException.class, () ->
////      tenPieceBoard.nextTurn(new PlaceAction(PlacementConstructor.createPlacements(placements))));
//  }
//
//  @Test
//  public void testPass() {
//
//  }
//
//  @Test
//  public void exchange() {
//
//  }
//}