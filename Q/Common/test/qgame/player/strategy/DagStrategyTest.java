package qgame.player.strategy;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;
import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.state.BasicPlayerGameState;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

import static org.junit.Assert.*;

public class DagStrategyTest {

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
  TurnStrategy dag = new DagStrategy(rules);

  PlayerGameState state1;
  PlayerGameState state2;
  PlayerGameState state3;
  PlayerGameState state4;

  PlaceAction expected1;
  PlaceAction expected2;
  TurnAction expected3;
  TurnAction expected4;

  @Before
  public void init1() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new TileImpl(orange, square));
    boardMap.put(new Posn(-1, 0), new TileImpl(purple, square));
    boardMap.put(new Posn(-2, 0), new TileImpl(purple, star));

    QGameMap board1 = new QGameMapImpl(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new TileImpl(purple, star), new TileImpl(red, eightStar), new TileImpl(green, star),
      new TileImpl(red, star));
    state1 = new BasicPlayerGameState(scores, board1, 3, playerTiles);
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-3, 0), new TileImpl(red, star)));
    expected.add(new Placement(new Posn(-4, 0), new TileImpl(green, star)));
    expected.add(new Placement(new Posn(-5, 0), new TileImpl(purple, star)));
    expected1 = new PlaceAction(expected);
  }

  @Test
  public void testPartialPlacments1() {
    TurnAction action = dag.chooseAction(state1);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> placementList = place.placements();
    assertEquals(expected1.placements(), placementList);
  }


  @Before
  public void init2() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new TileImpl(red, square));
    boardMap.put(new Posn(-1, 0), new TileImpl(purple, square));
    boardMap.put(new Posn(-2, 0), new TileImpl(purple, star));

    QGameMap board2 = new QGameMapImpl(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new TileImpl(purple, star), new TileImpl(red, eightStar), new TileImpl(green, star),
      new TileImpl(red, star));
    state2 = new BasicPlayerGameState(scores, board2, 3, playerTiles);

    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-3, 0), new TileImpl(red, star)));
    expected.add(new Placement(new Posn(-4, 0), new TileImpl(green, star)));
    expected.add(new Placement(new Posn(-5, 0), new TileImpl(purple, star)));
    expected.add(new Placement(new Posn(1, 0), new TileImpl(red, eightStar)));
    expected2 = new PlaceAction(expected);
  }


  @Test
  public void testPartialPlacments2() {
    TurnAction action = dag.chooseAction(state2);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;

    List<Placement> placementList = place.placements();
    assertEquals(expected2.placements(), placementList);
  }

  @Before
  public void init3() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new TileImpl(red, square));
    boardMap.put(new Posn(-1, 0), new TileImpl(purple, square));
    boardMap.put(new Posn(-2, 0), new TileImpl(purple, star));

    QGameMap board = new QGameMapImpl(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new TileImpl(green, circle), new TileImpl(yellow, eightStar), new TileImpl(blue, diamond),
      new TileImpl(orange, clover));
    state3 = new BasicPlayerGameState(scores, board, 3, playerTiles);
    expected3 = new PassAction();
  }

  @Test
  public void testPass() {
    TurnAction action = dag.chooseAction(state3);
    assertTrue(action instanceof PassAction);
  }

  @Before
  public void init4() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new TileImpl(red, square));
    boardMap.put(new Posn(-1, 0), new TileImpl(purple, square));
    boardMap.put(new Posn(-2, 0), new TileImpl(purple, star));

    QGameMap board = new QGameMapImpl(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new TileImpl(green, circle), new TileImpl(yellow, eightStar), new TileImpl(blue, diamond),
      new TileImpl(orange, clover));
    state4 = new BasicPlayerGameState(scores, board, 5, playerTiles);
    expected4 = new ExchangeAction();
  }

  @Test
  public void testExchange() {
    TurnAction action = dag.chooseAction(state4);
    assertTrue(action instanceof ExchangeAction);
  }


  @Test
  public void writeTests() throws IOException {
    PlayerGameState[] states = new PlayerGameState[]{state1, state2, state3, state4};
    TurnAction[] turns = new TurnAction[]{expected1, expected2, expected3, expected4};
    for (int i = 0; i < states.length; i++) {
      boolean result = XStrategyInputCreator.createInput(states[i], dag, turns[i], "6/Tests", i);
      assertTrue(result);
    }
  }


}