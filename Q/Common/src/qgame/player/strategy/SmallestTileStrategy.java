package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.util.TileUtil;

/**
 * Represents a TurnStrategy that considers the 'smallest' tiles first for adding to a move.
 * 
 * Tiles are sorted in shape-color order to determine their 'size'.
 * see TileUtil.smallestTile
 */
public abstract class SmallestTileStrategy implements TurnStrategy {

    // private StateRule stateRule;
    private BoardRule boardRule;
    private MoveRule moveRule;
    
    public SmallestTileStrategy(BoardRule boardRule, MoveRule moveRule) {
        // this.stateRule = stateRule;
        this.boardRule = boardRule;
        this.moveRule = moveRule;
    }

    // public StateRule getStateRule() {
    //     return stateRule;
    // }

    public BoardRule getBoardRule() {
        return boardRule;
    }

    public MoveRule getMoveRule() {
        return moveRule;
    }
    
    public abstract Placement getBestPlacement(IPlayerGameState state, List<Placement> move, List<Posn> posns, Tile t);
    
    @Override
    public IPlacementRule getPlacementRule() {
        // return new MultiPlacementRule(stateRule, boardRule, moveRule); 
        return new MultiPlacementRule(boardRule, moveRule); 
    }

    private boolean canPlaceTile(IMap board, Tile tile) {
        // TODO: can optimize
        return !boardRule.getValidPlacements(tile, board).isEmpty();
    }

    /**
     * Returns true if you can place any of the given tiles on the board
     * @param board
     * @param tiles
     * @return
     */
    private boolean canPlaceAnyOnBoard(IMap board, Bag<Tile> tiles) {
        return tiles.getItems()
        .stream()
        .anyMatch(tile -> canPlaceTile(board, tile));
    }

    /**
     * Returns a list of tiles containing the current player's tiles after each tile contained
     * in the given move is removed.
     * 
     * Assumes that the current player's tiles includes every tile contained in the move.
     * @param state
     * @param move
     * @return
     */
    private List<Tile> getTilesLeft(IPlayerGameState state, List<Placement> move) {
        List<Tile> tilesLeft = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
        List<Tile> moveTiles = move.stream().map(p -> p.tile()).toList();

        ArrayList<Tile> ts = new ArrayList<>(tilesLeft);

        for (Tile t : moveTiles) {
            ts.remove(t);
        }
        
        return ts;
    }

    /**
     * Returns an IMap that is the given IMap with
     * every placement in the given list placed on it.
     * 
     * Assumes that every Placement in move is legal when
     * placed on the board in order.
     * @param start
     * @param move
     * @return
     */
    private IMap getMapWithPlacements(IMap start, List<Placement> move) {
        IMap mapWithPlacements = new QMap(start.getBoardState());
        move.forEach(p -> mapWithPlacements.placeTile(p));
        return mapWithPlacements;
    }

    /**
     * Gets the best Placement considering the current player's remaining tiles.
     * 
     * Returns an Optional that is either:
     * - A Placement if a Placement is found that is legal on the board updated with the
     * placements contained in the given move.
     * - Empty if none of the current player's remaining tiles can be placed.
     * @param state
     * @param move
     * @return
     */
    private Optional<Placement> getBestTilePlacement(IPlayerGameState state, List<Placement> move) {

        IMap mapWithPlacements = getMapWithPlacements(state.getBoard(), move);
        
        List<Tile> tilesLeft = getTilesLeft(state, move);
        tilesLeft.sort(TileUtil::smallestTile);

        for (Tile t : tilesLeft) {
            List<Placement> placements = boardRule.getValidPlacements(t, mapWithPlacements);
            if (placements.isEmpty()) {
                continue;
            }
            List<Posn> posns = placements
                .stream().map(p -> p.posn()).toList();
            return Optional.of(getBestPlacement(state, move, posns, t));
        }
        return Optional.empty();
    }
    
    /**
     * Returns the optimal list of placements for this player according to this
     * strategy.
     * Iterates and adds to a list of placements until the strategy propses a Placement
     * that does not fit with the rest of the placements in the move.
     * @param state
     * @return
     */
    private List<Placement> getBestMove(IPlayerGameState state) {

        List<Placement> move = new ArrayList<>();
        boolean canAddToMove = true;

        while (canAddToMove) {
            Optional<Placement> p = getBestTilePlacement(state, move);

            if (p.isEmpty()) {
                canAddToMove = false;
            }
            else {
                Placement pl = p.get();
                if (moveRule.canAddPlacementToMove(pl, move)) {
                    move.add(pl);
                }
                else {
                    break;
                }
            }
        }
        return move;
    }   

/**
   * Decide what to do for the current player's turn, and return that as a TurnAction.
   * 
   * If the current player can place on the board, then a PlaceAction is returned containing
   * the best move according to this strategy. Otherwise an ExchangeAction is returned representing
   * a player's decision to trade their tiles. If the referee doesn't have enough tiles to honor
   * the trade, then a PassAction is returned representing a pass.
   * @param state The game state to determine an action for
   * @return The action the strategy decides to take for this state.
   */
    @Override
    public TurnAction chooseAction(IPlayerGameState state) {
        // TODO: Don't need to double check if you can place on the board (optimization)
        if (!canPlaceAnyOnBoard(state.getBoard(), state.getCurrentPlayerTiles())) {
            if (state.getNumberRemainingTiles() < state.getCurrentPlayerTiles().size()) {
                return new PassAction();
            }
            return new ExchangeAction();
        }
        return new PlaceAction(getBestMove(state));
    }
}
