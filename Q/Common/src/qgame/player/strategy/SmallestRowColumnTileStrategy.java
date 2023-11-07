package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.QPlayerGameState;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.util.TileUtil;

public abstract class SmallestRowColumnTileStrategy implements TurnStrategy {

  protected PlacementRule placeRules;

  public SmallestRowColumnTileStrategy(PlacementRule placeRules) {
    this.placeRules = placeRules;
  }


  private IPlayerGameState copy(IPlayerGameState state) {
    return new QPlayerGameState(state.playerScores(), state.viewBoard(),
      state.remainingTiles(), state.getCurrentPlayerTiles().viewItems(), state.playerName());
  }
  private IPlayerGameState applyPlacements(IPlayerGameState state, List<Placement> placements) {
    IPlayerGameState clone = copy(state);
    for (Placement placement : placements) {
      clone.makePlacement(placement);
    }
    return clone;
  }

  private boolean canPlaceTile(Tile tile, IPlayerGameState state) {
    List<Posn> possiblePlaces = placeRules.validPositionsForTile(tile, state);
    return !possiblePlaces.isEmpty();
  }

  private boolean placementFollowsRules(Placement placement, List<Placement> priorPlacements,
                                        IPlayerGameState state) {
    List<Placement> priorPlacementClone = new ArrayList<>(priorPlacements);
    priorPlacementClone.add(placement);
    return placeRules.validPlacements(priorPlacementClone, state);
  }

  private boolean canPlaceTileGivenPlacements(Tile tile,
                                              IPlayerGameState startState,
                                              List<Placement> priorPlacements) {
    IPlayerGameState currentState = applyPlacements(startState, priorPlacements);
    List<Posn> possiblePlaces = placeRules.validPositionsForTile(tile, currentState);
    List<Placement> possiblePlacements = possiblePlaces.stream()
      .map(posn -> new Placement(posn, tile))
      .filter(placement -> placementFollowsRules(placement, priorPlacements, startState)).toList();

    return !possiblePlacements.isEmpty();
  }

  private boolean canMakePlacements(IPlayerGameState startState, List<Placement> priorPlacements) {
    IPlayerGameState currentState = applyPlacements(startState, priorPlacements);
    List<Tile> tiles =  new ArrayList<>(currentState.getCurrentPlayerTiles().viewItems());
    return tiles
      .stream()
      .anyMatch(tile -> canPlaceTileGivenPlacements(tile, startState, priorPlacements));
  }

  private boolean canMakePlacements(IPlayerGameState state) {
    return canMakePlacements(state, new ArrayList<>());
  }
  protected abstract Placement makePlacementGivenPositions(IPlayerGameState state,
                                                      List<Posn> legalPlaces);

  private Placement makePlacement(
                                  IPlayerGameState startState,
                                  List<Placement> priorPlacements) {
    IPlayerGameState currentState = applyPlacements(startState, priorPlacements);
    Tile tile = bestTile(currentState);
    List<Posn> validPositions = placeRules.validPositionsForTile(tile, currentState);
    List<Posn> legalPlaces = validPositions
      .stream()
      .map(posn -> new Placement(posn, tile))
      .filter(placement -> placementFollowsRules(placement, priorPlacements, startState))
      .map(Placement::posn)
      .toList();
    return makePlacementGivenPositions(currentState, new ArrayList<>(legalPlaces));
  }
  protected Tile bestTile(IPlayerGameState state) {
    List<Tile> tiles = new ArrayList<>(state.getCurrentPlayerTiles().viewItems());
    tiles.sort(TileUtil::smallestTile);
    for (Tile tile : tiles) {
      if (canPlaceTile(tile, state)) {
        return tile;
      }
    }

    throw new IllegalArgumentException("Cannot place any tiles on this state.");
  }

  private PlaceAction makePlacements(IPlayerGameState startState) {
    List<Placement> results = new ArrayList<>();
    IPlayerGameState currentState = copy(startState);
    while (canMakePlacements(startState, results)) {
      Placement placement = makePlacement(startState, results);
      results.add(placement);
      currentState.makePlacement(placement);
    }
    return new PlaceAction(results);
  }


  @Override
  public TurnAction chooseAction(IPlayerGameState state) {
    if (!canMakePlacements(state)) {
      if (state.remainingTiles() < state.getCurrentPlayerTiles().size()) {
        return new PassAction();
      }
      return new ExchangeAction();
    }
    return makePlacements(state);
  }
}
