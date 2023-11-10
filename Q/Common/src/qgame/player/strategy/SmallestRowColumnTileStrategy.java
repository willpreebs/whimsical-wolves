// package qgame.player.strategy;

// import java.util.ArrayList;
// import java.util.List;

// import qgame.action.ExchangeAction;
// import qgame.action.PassAction;
// import qgame.action.PlaceAction;
// import qgame.action.TurnAction;
// import qgame.state.QQPlayerGameState;
// import qgame.state.map.Posn;
// import qgame.state.map.Tile;
// import qgame.rule.placement.PlacementRule;
// import qgame.state.Placement;
// import qgame.state.IQPlayerGameState;
// import qgame.util.TileUtil;

// public abstract class SmallestRowColumnTileStrategy implements TurnStrategy {

//   protected PlacementRule placeRules;

//   /**
//    * From the list of Posn, return the optimal Placement containing the best possible tile
//    * @param state
//    * @param legalPosns
//    * @return
//    */
//   protected abstract Placement getBestPlacement(IQPlayerGameState state, List<Posn> legalPosns, Tile bestTile);

//   public SmallestRowColumnTileStrategy(PlacementRule placeRules) {
//     this.placeRules = placeRules;
//   }

//   public PlacementRule getPlacementRule() {
//     return this.placeRules;
//   }

//   /**
//    * Makes a copy of the gameState and returns the copy
//    * @param state
//    * @return
//    */
//   private IQPlayerGameState copy(IQPlayerGameState state) {
//     return new QQPlayerGameState(state.getPlayerScores(), state.getBoard(),
//       state.getNumberRemainingTiles(), state.getCurrentPlayerTiles().getItems(), state.getPlayerName());
//   }


//   /**
//    * Applies the given list of Placements to a copy of the given state
//    * and returns the modified state 
//    * @param state
//    * @param placements
//    * @return
//    */
//   private IQPlayerGameState applyPlacements(IQPlayerGameState state, List<Placement> placements) {
//     IQPlayerGameState clone = copy(state);
//     for (Placement placement : placements) {
//       clone.makePlacement(placement);
//     }
//     return clone;
//   }

//   /**
//    * Returns true if a Placement exists on the board that satisfies all of the placement rules
//    * @param tile A tile that may or may not fit on the board
//    * @param state 
//    * @return
//    */
//   private boolean canPlaceTile(Tile tile, IQPlayerGameState state) {
//     List<Posn> possiblePlaces = placeRules.validPositionsForTile(tile, state);
//     return !possiblePlaces.isEmpty();
//   }

//   /**
//    * Returns true if the given placement satisfies all of the rules, including its position relative
//    * to other placements in this move
//    * @param placement
//    * @param moveSoFar
//    * @param state
//    * @return
//    */
//   private boolean placementFollowsRules(Placement placement, List<Placement> moveSoFar,
//                                         IQPlayerGameState state) {
//     List<Placement> move = new ArrayList<>(moveSoFar);
//     move.add(placement);
//     return placeRules.isPlacementListLegal(move, state);
//   }

//   /**
//    * Returns true if a Tile can be placed according to the rules, as well as fits 
//    * with the previous placements.
//    * 
//    * @param tile A Tile that the current player owns
//    * @param startState The GameState before the placements are made
//    * @param move
//    * @return
//    */
//   private boolean canAddTileToMove(Tile tile,
//                                               IQPlayerGameState startState,
//                                               List<Placement> move) {

//     IQPlayerGameState currentState = applyPlacements(startState, move);
//     List<Posn> possiblePlaces = placeRules.validPositionsForTile(tile, currentState);
//     List<Placement> possiblePlacements = possiblePlaces.stream()
//       .map(posn -> new Placement(posn, tile))
//       .filter(placement -> placementFollowsRules(placement, move, currentState)).toList();

//     return !possiblePlacements.isEmpty();
//   }

//   /**
//    * Returns true if any of the current player's tiles can be added to the board in addition
//    * to the given list of Placements 
//    * @param state
//    * @param move A list of Placements representing part of a Move on the board
//    * @return
//    */
//   private boolean canAddToMove(IQPlayerGameState state, List<Placement> move) {
//     IQPlayerGameState currentState = applyPlacements(state, move);
//     List<Tile> playerTiles =  new ArrayList<>(currentState.getCurrentPlayerTiles().getItems());
//     System.out.println("Player has: " + playerTiles.size() + " tiles left");
//     return playerTiles
//       .stream()
//       .anyMatch(tile -> canAddTileToMove(tile, state, move));
//   }

//   /**
//    * Determine if any of the player's tiles can be placed on the current board
//    * @param state
//    * @return True if one of the current player's tiles can legally be placed on the board
//    */
//   private boolean canMakePlacement(IQPlayerGameState state) {
//     List<Tile> playerTiles = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
//     return playerTiles.stream().anyMatch(t -> canPlaceTile(t, state));
//   }

//   /**
//    * From a list of Posns, filter for the Posns that fit into the given moveSoFar
//    * according to the PlacementRules
//    * @param moveSoFar The list of Placements that a new Placement must not conflict with
//    * according to the Placement Rules
//    * @param state 
//    * @param validPosns A list of all Posns that the given Tile can already be placed
//    * @param tile The tile that all Placements being tested must include
//    * @return
//    */
//   private List<Posn> getAllMoveExtensions(List<Placement> moveSoFar, IQPlayerGameState state, List<Posn> validPosns, Tile tile) {   
//     return validPosns
//       .stream()
//       .map(posn -> new Placement(posn, tile))
//       .filter(placement -> placementFollowsRules(placement, moveSoFar, state))
//       .map(Placement::posn)
//       .toList();
//   }

//   /**
//    * Determines the best Placement that follows the Placement rules
//    * and fits relative to the given list of Placements representing an incomplete move
//    * @param state
//    * @param moveSoFar
//    * @return
//    */
//   private Placement getBestMoveExtension(IQPlayerGameState state,
//                                   List<Placement> moveSoFar) {

//     IQPlayerGameState stateWithPlacements = applyPlacements(state, moveSoFar);
//     Tile bestTile = getBestTile(stateWithPlacements);
//     List<Posn> validPositions = placeRules.validPositionsForTile(bestTile, stateWithPlacements);
//     List<Posn> legalMoveExtensions = getAllMoveExtensions(moveSoFar, state, validPositions, bestTile);
//     return getBestPlacement(stateWithPlacements, legalMoveExtensions, bestTile);
//   }

//   protected Tile getBestTile(IQPlayerGameState state) {
//     List<Tile> tiles = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
//     tiles.sort(TileUtil::smallestTile);
//     for (Tile tile : tiles) {
//       if (canPlaceTile(tile, state)) {
//         return tile;
//       }
//     }

//     throw new IllegalArgumentException("Cannot place any tiles on this state.");
//   }

//   /**
//    * Determines the best PlaceAction given the implementation of Strategy.
//    * Iterates over the method getBestMoveExtension to determine the optimal
//    * complete move
//    * @param state
//    * @return
//    */
//   private PlaceAction getBestPlaceAction(IQPlayerGameState state) {
//     List<Placement> move = new ArrayList<>();
//     IQPlayerGameState currentState = copy(state);

//     while (canAddToMove(currentState, move)) {
//       Tile t = getBestTile(currentState);
//       List<Posn> validPositions = placeRules.validPositionsForTile(t, currentState);
//       if (validPositions.size() == 0) {
//         break;
//       }
//       Placement best = getBestPlacement(currentState, validPositions, t);
//       move.add(best);
//       // Placement placement = getBestMoveExtension(state, move);
//       // move.add(placement);
//       // currentState.makePlacement(best);
//     }
//     System.out.println("Best move determined, length is: " + move.size());
//     return new PlaceAction(move);
//   }

//   @Override
//   public TurnAction chooseAction(IQPlayerGameState state) {
//     if (!canMakePlacement(state)) {
//       if (state.getNumberRemainingTiles() < state.getCurrentPlayerTiles().size()) {
//         return new PassAction();
//       }
//       return new ExchangeAction();
//     }
//     return getBestPlaceAction(state);
//   }
// }
package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  public PlacementRule getPlacementRule() {
    return this.placeRules;
  }


  private IPlayerGameState copy(IPlayerGameState state) {
    return new QPlayerGameState(state.getPlayerScores(), state.getBoard(),
      state.getNumberRemainingTiles(), state.getCurrentPlayerTiles().getItems(), state.getPlayerName());
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
    return placeRules.isPlacementListLegal(priorPlacementClone, state);
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
    List<Tile> tiles =  new ArrayList<>(currentState.getCurrentPlayerTiles().getItems());
    return tiles
      .stream()
      .anyMatch(tile -> canPlaceTileGivenPlacements(tile, startState, priorPlacements));
  }

  private boolean canMakePlacements(IPlayerGameState state) {
    return canMakePlacements(state, new ArrayList<>());
  }
  protected abstract Placement makePlacementGivenPositions(IPlayerGameState state,
                                                      List<Posn> legalPlaces);

  private Optional<Placement> makePlacement(
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
    if (legalPlaces.size() == 0) {
      return Optional.empty();
    }
    else {
      return Optional.of(makePlacementGivenPositions(currentState, new ArrayList<>(legalPlaces)));
    }
  }
  protected Tile bestTile(IPlayerGameState state) {
    List<Tile> tiles = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
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
      Optional<Placement> placement = makePlacement(startState, results);
      if (placement.isEmpty()) {
        break;
      }
      else {
        Placement p = placement.get();  
        results.add(p);
        currentState.makePlacement(p);
      }
    }
    return new PlaceAction(results);
  }


  @Override
  public TurnAction chooseAction(IPlayerGameState state) {
    if (!canMakePlacements(state)) {
      if (state.getNumberRemainingTiles() < state.getCurrentPlayerTiles().size()) {
        return new PassAction();
      }
      return new ExchangeAction();
    }
    return makePlacements(state);
  }
}

