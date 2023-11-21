package qgame.player.strategy.new_strategies;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.state.StateRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

public class NewLdasgStrategy extends NewSmallestRowColumnTileStrategy {

    public NewLdasgStrategy(BoardRule boardRule, MoveRule moveRule) {
        super(boardRule, moveRule);
    }

    private long getNumberNeighbors(IMap map, Posn p) {
        return PosnUtil.neighbors(p)
        .stream()
        .filter(map::posnHasTile)
        .count();
    }

    private List<Posn> getMaxConstrainedPosns(IPlayerGameState state, List<Posn> posns) {
        IMap map = state.getBoard();

        List<Posn> maxList = new ArrayList<>();

        int max = 0;
        for (Posn p : posns) {
            int n = (int) getNumberNeighbors(map, p);
            if (n > max) {
                maxList = List.of(p);
                max = n;
            }
            else if (n == max) {
                maxList.add(p);
            }
        }
        return maxList;
    }

    @Override
    public Placement getBestPlacement(IPlayerGameState state, List<Posn> posns, Tile t) {
        
        List<Posn> maxNeighborList = getMaxConstrainedPosns(state, posns);
        maxNeighborList.sort(PosnUtil::rowColumnCompare);
        return new Placement(maxNeighborList.get(0), t);
    }
    
}
