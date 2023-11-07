package qgame.player.strategy;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.state.map.QTile;
import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.state.QPlayerGameState;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LDASGStrategyTest {
  Tile.Color red = Tile.Color.RED;
  Tile.Color blue = Tile.Color.BLUE;
  Tile.Color green = Tile.Color.GREEN;
  Tile.Color yellow = Tile.Color.YELLOW;
  Tile.Color orange = Tile.Color.ORANGE;
  Tile.Color purple = Tile.Color.PURPLE;

  Tile.Shape square = Tile.Shape.SQUARE;
  Tile.Shape circle = Tile.Shape.CIRCLE;
  Tile.Shape diamond = Tile.Shape.DIAMOND;
  Tile.Shape clover = Tile.Shape.CLOVER;
  Tile.Shape star = Tile.Shape.STAR;
  Tile.Shape eightStar = Tile.Shape.EIGHT_STAR;

  PlacementRule rules = new MultiPlacementRule(new MatchTraitRule(), new ExtendSameLineRule(),
      new ExtendsBoardRule(), new CorrectPlayerTilesRule());
  TurnStrategy ldasg = new LdasgStrategy(rules);

  IPlayerGameState state1;
  IPlayerGameState state2;
  IPlayerGameState state3;

  @Before
  public void init1() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new QTile(orange, square));
    boardMap.put(new Posn(-1, 0), new QTile(purple, square));
    boardMap.put(new Posn(-2, 0), new QTile(purple, star));

    IMap board1 = new QMap(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new QTile(purple, star), new QTile(red, eightStar), new QTile(green, star),
      new QTile(red, star));
    state1 = new QPlayerGameState(scores, board1, 3, playerTiles, "");
  }

  @Test
  public void testPartialPlacments1() {
    TurnAction action = ldasg.chooseAction(state1);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-3, 0), new QTile(red, star)));
    expected.add(new Placement(new Posn(-4, 0), new QTile(green, star)));
    expected.add(new Placement(new Posn(-5, 0), new QTile(purple, star)));

    List<Placement> placementList = place.placements();
    assertEquals(expected, placementList);
  }

  @Before
  public void init2() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new QTile(orange, square));
    boardMap.put(new Posn(-1, 0), new QTile(purple, star));
    boardMap.put(new Posn(-2, 0), new QTile(purple, square));
    boardMap.put(new Posn(-3,0), new QTile(purple, star));

    IMap board1 = new QMap(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new QTile(purple, star), new QTile(red, eightStar), new QTile(green, star),
      new QTile(red, star));
    state2 = new QPlayerGameState(scores, board1, 3, playerTiles, "");
  }

  @Test
  public void testPlacementToLowerRowColOrder() {
    TurnAction action = ldasg.chooseAction(state2);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-4, 0), new QTile(red, star)));
    expected.add(new Placement(new Posn(-5, 0), new QTile(green, star)));
    expected.add(new Placement(new Posn(-6, 0), new QTile(purple, star)));

    List<Placement> placementList = place.placements();
    assertEquals(expected, placementList);
  }

  @Before
  public void init3() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new QTile(orange, square));
    boardMap.put(new Posn(0,-1), new QTile(orange, star));
    boardMap.put(new Posn(-1, 0), new QTile(purple, star));
    boardMap.put(new Posn(-2, 0), new QTile(purple, square));
    boardMap.put(new Posn(-3,0), new QTile(purple, star));

    IMap board1 = new QMap(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new QTile(purple, star), new QTile(red, eightStar), new QTile(green, star),
      new QTile(red, star));
    state3 = new QPlayerGameState(scores, board1, 3, playerTiles, "");
  }

  @Test
  public void testMoreConstrained() {
    TurnAction action = ldasg.chooseAction(state3);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-1, -1), new QTile(red, star)));
    expected.add(new Placement(new Posn(-3, -1), new QTile(green, star)));
    expected.add(new Placement(new Posn(-2, -1), new QTile(purple, star)));

    List<Placement> placementList = place.placements();
    assertEquals(expected, placementList);
  }

  @Test
  public void addLDASGTest() throws IOException {
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-1, -1), new QTile(red, star)));
    expected.add(new Placement(new Posn(-3, -1), new QTile(green, star)));
    expected.add(new Placement(new Posn(-2, -1), new QTile(purple, star)));
    TurnAction action = new PlaceAction(expected);
    boolean result = XStrategyInputCreator.createInput(state3, ldasg, action, "6/Tests", 4);
    assertTrue(result);
  }
}
