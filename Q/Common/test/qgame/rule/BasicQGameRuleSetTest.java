package qgame.rule;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.ExtendsBoardRule;
import qgame.rule.placement.board.MatchTraitRule;
import qgame.rule.placement.move.ExtendSameLineRule;
import qgame.rule.placement.state.CorrectPlayerTilesRule;
import qgame.state.QPlayerGameState;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.state.map.QTile;

import static org.junit.Assert.*;

public class BasicQGameRuleSetTest {

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
  IPlayerGameState state;
  List<Placement> placements1;

  List<PlacementRule> rules = List.of( new MultiPlacementRule(new CorrectPlayerTilesRule(),
    new ExtendsBoardRule(),
    new ExtendSameLineRule(), new MatchTraitRule()));
  PlacementRule multi = new MultiPlacementRule(rules);
  @Before
  public void init() {
    Map<Posn, Tile> boardState = new HashMap<>();
    boardState = new HashMap<>();
    boardState.put(new Posn(-3, 1), new QTile(yellow, circle));
    boardState.put(new Posn(-2, -1), new QTile(green, circle));
    boardState.put(new Posn(-2, -0), new QTile(green, square));
    boardState.put(new Posn(-1, -1), new QTile(blue, star));
    boardState.put(new Posn(-1, -1), new QTile(blue, circle));
    boardState.put(new Posn(0, -1), new QTile(purple, star));
    boardState.put(new Posn(0, 0), new QTile(purple, diamond));
    boardState.put(new Posn(0, 1), new QTile(purple, circle));
    boardState.put(new Posn(1, -1), new QTile(orange, star));
    boardState.put(new Posn(2, -1), new QTile(red, star));
    boardState.put(new Posn(2, 0), new QTile(red, eight));
    boardState.put(new Posn(2, 1), new QTile(red, square));
    boardState.put(new Posn(2, 2), new QTile(red, clover));
    state = new QPlayerGameState(List.of(5, 5, 8), new QMap(boardState), 20,
      List.of(new QTile(red, square), new QTile(purple, clover)), "");
    placements1 = new ArrayList<>();
    placements1.add(new Placement(new Posn(-3, 0), new QTile(red, square)));
    placements1.add(new Placement(new Posn(0, 2), new QTile(purple, clover)));
  }

  @Test
  public void placementsSatisfyRules() {
    Map<Posn, Tile> map = new HashMap<>();
    map.put(new Posn(0, 0), new QTile(red, square));
    map.put(new Posn(0, 1), new QTile(red, circle));
    map.put(new Posn(1, 0), new QTile(green, square));
    map.put(new Posn(2, 0), new QTile(blue, square));
    map.put(new Posn(2, 1), new QTile(blue, eight));

    List<Placement> placements = List.of(new Placement(new Posn(0, 2), new QTile(red, circle)),
      new Placement(new Posn(2, 2), new QTile(blue, square)));
    IPlayerGameState state2 = new QPlayerGameState(List.of(0, 0, 0), new QMap(map),
      16, placements.stream().map(Placement::tile).toList(), "");
    assertTrue(multi.isPlacementListLegal(placements, state2));

  }

  @Test
  public void validPositionsForTile() {
  }
}