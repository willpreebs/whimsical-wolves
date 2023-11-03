package qgame.player;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
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

public class PlayerTest {


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

  Player player1;
  Player player2;

  PlayerGameState state1;


  @Before
  public void init() {
    PlacementRule rule = new MultiPlacementRule(new MatchTraitRule(), new ExtendSameLineRule(),
      new ExtendsBoardRule(), new CorrectPlayerTilesRule());
    player1 = new SimpleAIPlayer("bob", new DagStrategy(rule));
    player2 = new SimpleAIPlayer("timmy", new LdasgStrategy(rule));
  }

  @Test
  public void name() {
    assertEquals("bob", player1.name());
    assertEquals("timmy", player2.name());
  }

  @Before
  public void init3() {
    Map<Posn, Tile> boardMap = new HashMap<>();
    boardMap.put(new Posn(0, 0), new TileImpl(orange, square));
    boardMap.put(new Posn(0,-1), new TileImpl(orange, star));
    boardMap.put(new Posn(-1, 0), new TileImpl(purple, star));
    boardMap.put(new Posn(-2, 0), new TileImpl(purple, square));
    boardMap.put(new Posn(-3,0), new TileImpl(purple, star));

    QGameMap board1 = new QGameMapImpl(boardMap);
    List<Integer> scores = List.of(3, 2);
    List<Tile> playerTiles = List.of(
      new TileImpl(purple, star), new TileImpl(red, eightStar), new TileImpl(green, star),
      new TileImpl(red, star));
    state1 = new BasicPlayerGameState(scores, board1, 3, playerTiles);
  }
  @Test
  public void testMoreConstrained1() {
    TurnAction action = player2.takeTurn(state1);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-1, -1), new TileImpl(red, star)));
    expected.add(new Placement(new Posn(-3, -1), new TileImpl(green, star)));
    expected.add(new Placement(new Posn(-2, -1), new TileImpl(purple, star)));

    List<Placement> placementList = place.placements();
    assertEquals(expected, placementList);
  }

  @Test
  public void testMoreConstrained2() {
    TurnAction action = player1.takeTurn(state1);
    assertTrue(action instanceof PlaceAction);

    PlaceAction place = (PlaceAction) action;
    List<Placement> expected = new ArrayList<>();
    expected.add(new Placement(new Posn(-4, 0), new TileImpl(red, star)));
    expected.add(new Placement(new Posn(-5, 0), new TileImpl(green, star)));
    expected.add(new Placement(new Posn(-6, 0), new TileImpl(purple, star)));

    List<Placement> placementList = place.placements();
    assertEquals(expected, placementList);
  }

}