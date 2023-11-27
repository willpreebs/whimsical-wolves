package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

/**
 * Represents a Strategy that chooses the most constrained Placement, as in the Placement
 * with the most neighbors on the board. Breaks the tie using row-column order. 
 * See PosnUtil.rowColumnCompare
 */
public class LdasgStrategy extends SmallestTileStrategy {

    public LdasgStrategy(BoardRule boardRule, MoveRule moveRule) {
        super(boardRule, moveRule);
    }

    public LdasgStrategy(PlacementRule rule) {
        super(rule.getBoardRule(), rule.getMoveRule());
    }

    private long getNumberNeighbors(IMap map, Posn p) {
        return PosnUtil.neighbors(p)
        .stream()
        .filter(map::posnHasTile)
        .count();
    }

    private List<Posn> getMaxConstrainedPosns(IPlayerGameState state, List<Placement> move, List<Posn> posns) {
        IMap map = state.getBoard();
        move.forEach(p -> map.placeTile(p));

        List<Posn> maxList = new ArrayList<>();

        int max = 0;
        for (Posn p : posns) {
            int n = (int) getNumberNeighbors(map, p);
            if (n > max) {
                maxList = new ArrayList<>();
                maxList.add(p);
                max = n;
            }
            else if (n == max) {
                maxList.add(p);
            }
        }
        return maxList;
    }

    @Override
    public Placement getBestPlacement(IPlayerGameState state, List<Placement> move, List<Posn> posns, Tile t) {

        ArrayList<Posn> maxNeighborList = new ArrayList<>(getMaxConstrainedPosns(state, move, posns));
        maxNeighborList.sort(PosnUtil::rowColumnCompare);
        return new Placement(maxNeighborList.get(0), t);
    }
    
}
