package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
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

public abstract class SmallestRowColumnTileStrategy implements TurnStrategy {

    // private StateRule stateRule;
    private BoardRule boardRule;
    private MoveRule moveRule;
    
    public SmallestRowColumnTileStrategy(BoardRule boardRule, MoveRule moveRule) {
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
    public PlacementRule getPlacementRule() {
        // return new MultiPlacementRule(stateRule, boardRule, moveRule); 
        return new MultiPlacementRule(boardRule, moveRule); 
    }

    private boolean canPlaceTile(IMap board, Tile tile) {
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

    private List<Tile> removeTilesFromList(List<Tile> tiles, List<Tile> moveTiles) {
        ArrayList<Tile> ts = new ArrayList<>(tiles);
        for (Tile t : moveTiles) {
            ts.remove(t);
        }
        return ts;
    }

    private List<Tile> getTilesLeft(IPlayerGameState state, List<Placement> move) {
        List<Tile> tilesLeft = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
        List<Tile> moveTiles = move.stream().map(p -> p.tile()).toList();
        return removeTilesFromList(tilesLeft, moveTiles);
    }

    private IMap getMapWithPlacements(IMap start, List<Placement> move) {
        IMap mapWithPlacements = new QMap(start.getBoardState());
        move.forEach(p -> mapWithPlacements.placeTile(p));
        return mapWithPlacements;
    }

    private Optional<Placement> getBestTilePlacement(IPlayerGameState state, List<Placement> move) {

        IMap mapWithPlacements = getMapWithPlacements(state.getBoard(), move);
        
        List<Tile> tilesLeft = getTilesLeft(state, move);
        tilesLeft.sort(TileUtil::smallestTile);

        for (Tile t : tilesLeft) {
            List<Placement> placements = boardRule.getValidPlacements(t, mapWithPlacements);
            if (placements.isEmpty()) {
                continue;
            }
            // placements = placements.stream()
            // .filter(p -> moveRule.canAddPlacementToMove(p, move))
            // .toList();

            // if (placements.isEmpty()) {
            //     continue;
            // }
            List<Posn> posns = placements
                .stream().map(p -> p.posn()).toList();
                return Optional.of(getBestPlacement(state, move, posns, t));
            // else {
                
            // }
        }
        return Optional.empty();
    }
    
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

    @Override
    public TurnAction chooseAction(IPlayerGameState state) {
        if (!canPlaceAnyOnBoard(state.getBoard(), state.getCurrentPlayerTiles())) {
            if (state.getNumberRemainingTiles() < state.getCurrentPlayerTiles().size()) {
                return new PassAction();
            }
            return new ExchangeAction();
        }
        return new PlaceAction(getBestMove(state));
    }

}
