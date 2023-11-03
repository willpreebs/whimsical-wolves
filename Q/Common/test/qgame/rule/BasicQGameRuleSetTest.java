package qgame.rule;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.player.PlayerInfo;
import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.state.BasicPlayerGameState;
import qgame.state.Placement;
import qgame.state.PlayerGameState;
import qgame.state.map.Posn;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;

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
  PlayerGameState state;
  List<Placement> placements1;

  List<PlacementRule> rules = List.of( new MultiPlacementRule(new CorrectPlayerTilesRule(),
    new ExtendsBoardRule(),
    new ExtendSameLineRule(), new MatchTraitRule()));
  PlacementRule multi = new MultiPlacementRule(rules);
  @Before
  public void init() {
    Map<Posn, Tile> boardState = new HashMap<>();
    boardState = new HashMap<>();
    boardState.put(new Posn(-3, 1), new TileImpl(yellow, circle));
    boardState.put(new Posn(-2, -1), new TileImpl(green, circle));
    boardState.put(new Posn(-2, -0), new TileImpl(green, square));
    boardState.put(new Posn(-1, -1), new TileImpl(blue, star));
    boardState.put(new Posn(-1, -1), new TileImpl(blue, circle));
    boardState.put(new Posn(0, -1), new TileImpl(purple, star));
    boardState.put(new Posn(0, 0), new TileImpl(purple, diamond));
    boardState.put(new Posn(0, 1), new TileImpl(purple, circle));
    boardState.put(new Posn(1, -1), new TileImpl(orange, star));
    boardState.put(new Posn(2, -1), new TileImpl(red, star));
    boardState.put(new Posn(2, 0), new TileImpl(red, eight));
    boardState.put(new Posn(2, 1), new TileImpl(red, square));
    boardState.put(new Posn(2, 2), new TileImpl(red, clover));
    state = new BasicPlayerGameState(List.of(5, 5, 8), new QGameMapImpl(boardState), 20,
      List.of(new TileImpl(red, square), new TileImpl(purple, clover)));
    placements1 = new ArrayList<>();
    placements1.add(new Placement(new Posn(-3, 0), new TileImpl(red, square)));
    placements1.add(new Placement(new Posn(0, 2), new TileImpl(purple, clover)));
  }

  @Test
  public void placementsSatisfyRules() {
    Map<Posn, Tile> map = new HashMap<>();
    map.put(new Posn(0, 0), new TileImpl(red, square));
    map.put(new Posn(0, 1), new TileImpl(red, circle));
    map.put(new Posn(1, 0), new TileImpl(green, square));
    map.put(new Posn(2, 0), new TileImpl(blue, square));
    map.put(new Posn(2, 1), new TileImpl(blue, eight));

    List<Placement> placements = List.of(new Placement(new Posn(0, 2), new TileImpl(red, circle)),
      new Placement(new Posn(2, 2), new TileImpl(blue, square)));
    PlayerGameState state2 = new BasicPlayerGameState(List.of(0, 0, 0), new QGameMapImpl(map),
      16, placements.stream().map(Placement::tile).toList());
    assertTrue(multi.validPlacements(placements, state2));

  }

  @Test
  public void validPositionsForTile() {
  }
}