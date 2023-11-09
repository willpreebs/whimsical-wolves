package qgame.player.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import qgame.action.ExchangeAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.player.CheatingAIPlayer.Cheat;
import qgame.rule.placement.PlacementRule;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;
import qgame.util.TileUtil;

public class CheatStrategy implements TurnStrategy {

    Cheat cheat;
    TurnStrategy backupStrategy;

    public CheatStrategy(Cheat cheat, TurnStrategy backupStrategy) {
        this.cheat = cheat;
        this.backupStrategy = backupStrategy;
    }

    public PlacementRule getPlacementRule() {
        return backupStrategy.getPlacementRule();
    }

    private Posn getNotAdjacentPosn(IMap map) {
        Set<Posn> posns = map.getBoardState().keySet();
        int topRow = posns.stream().map(Posn::y).reduce(Integer.MAX_VALUE, Integer::min);
        int leftCol = posns.stream().map(Posn::x).reduce(Integer.MAX_VALUE, Integer::min);

        // A posn 2 up and 2 to the left of the top left most posn will never have a neighbor.
        return new Posn(topRow - 2, leftCol - 2);
    }

    private TurnAction notAdjacent(IPlayerGameState state) {
        
        Posn notAdjacentPosn = getNotAdjacentPosn(state.getBoard());

        List<Placement> move = new ArrayList<>();

        // gets the first tile from the player (tile is arbitrary)
        Tile t = (Tile) state.getCurrentPlayerTiles().getItems().toArray()[0];

        move.add(new Placement(notAdjacentPosn, t));
        return new PlaceAction(move);
    }

    private List<Posn> getValidPosns(Tile t, IPlayerGameState state) {
        return this.getPlacementRule().validPositionsForTile(t, state);
    }

    private List<Placement> getValidPlacements(Tile t, IPlayerGameState state) {
        List<Posn> posns = this.getValidPosns(t, state);
        List<Placement> placements = new ArrayList<>();

        posns.stream().forEach(posn -> placements.add(new Placement(posn, t)));

        return placements;
    }

    private Optional<Placement> getArbitraryValidPlacement(Tile t, IPlayerGameState state) {
        List<Posn> validPosns = getValidPosns(t, state);
        if (validPosns.size() != 0) {
            return Optional.of(new Placement(validPosns.get(0), t));
        }
        else {
            return Optional.empty();
        }
    }

    private TurnAction notOwned(IPlayerGameState state) {

        int numUniqueTiles = TileUtil.getNumberUniqueTiles();
        Bag<Tile> allPossibleTiles = TileUtil.getTileBag(numUniqueTiles); 
        Bag<Tile> playerTiles = state.getCurrentPlayerTiles();
        
        List<Tile> notOwnedTiles = allPossibleTiles.getItems()
        .stream()
        .filter(t -> !playerTiles.contains(t))
        .toList();

        Optional<Placement> move = getFirstArbitraryValidPlacement(notOwnedTiles, state);

        // if none of the tiles that a player doesn't own can be placed, default to backup strategy
        if (move.isPresent()) {
            return new PlaceAction(List.of(move.get()));
        }
        else {
            return backupStrategy.chooseAction(state);
        }
    }

    /**
     * Returns a list containing zero or one placements.
     * The placement contained in the list is an arbitrary placement
     * that satisfies the placement rules of the backup strategy and
     * contains a tile out of the given collection of tiles.  
     * @param tiles
     * @param state
     * @return
     */
    private Optional<Placement> getFirstArbitraryValidPlacement(Collection<Tile> tiles, IPlayerGameState state) {

        return tiles.stream()
        .map(t -> getArbitraryValidPlacement(t, state))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .findFirst();
    }

    private boolean inLine(Placement a, Placement b) {
        return PosnUtil.sameCol(a.posn(), b.posn()) || PosnUtil.sameRow(a.posn(), b.posn());
    }

    private Optional<Placement> getValidNotInlinePlacement(Placement existing, Collection<Tile> playerTiles, IPlayerGameState state) {

        return playerTiles.stream()
        .map(t -> getValidPlacements(t, state))
        .map(list -> list.stream().filter(p -> !inLine(p, p)).findFirst())
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .findFirst();
    }

    private TurnAction notInline(IPlayerGameState state) {

        Collection<Tile> playerTiles = state.getCurrentPlayerTiles().getItems(); // .stream().toList();

        Optional<Placement> p1 = getFirstArbitraryValidPlacement(playerTiles, state);

        if (p1.isEmpty()) {
            return backupStrategy.chooseAction(state);
        }

        Placement a = p1.get();

        playerTiles.remove(a.tile());

        Optional<Placement> p2 = getValidNotInlinePlacement(a, playerTiles, state);

        if (p2.isEmpty()) {
            return backupStrategy.chooseAction(state);
        }

        Placement b = p2.get();
        return new PlaceAction(List.of(a, b));
    }

    private TurnAction notEnoughTiles(IPlayerGameState state) {

        if (state.getNumberRemainingTiles() < state.getCurrentPlayerTiles().size()) {
            return new ExchangeAction();
        }
        else {
            return backupStrategy.chooseAction(state);
        }
    }

    private TurnAction notLegalNeighbor(IPlayerGameState state) {
        Collection<Tile> playerTiles = state.getCurrentPlayerTiles().getItems();
        List<Posn> validBoardExtensions = state.getBoard().validPositions();

        for (Tile t : playerTiles) {
            List<Posn> validPosns = this.getValidPosns(t, state);        
            Optional<Posn> op = validBoardExtensions.stream().filter(posn -> !validPosns.contains(posn)).findFirst();
            if (op.isPresent()) {
                return new PlaceAction(List.of(new Placement(op.get(), t)));
            }
        }
        // If all of the player's tiles can somehow be placed everywhere on the board:
        return this.backupStrategy.chooseAction(state);
    }

    @Override
    public TurnAction chooseAction(IPlayerGameState state) {

        switch (cheat) {
            case NOT_ADJACENT:
                return notAdjacent(state);
            case NOT_OWNED:
                return notOwned(state);
            case NOT_INLINE:
                return notInline(state);
            case NOT_ENOUGH_TILES:
                return notEnoughTiles(state);
            case NOT_LEGAL_NEIGHBOR:
                return notLegalNeighbor(state);
            case NONE:
            default:
                return this.backupStrategy.chooseAction(state);
        }
    }
}
