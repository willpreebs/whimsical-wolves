package qgame.player.strategy;

import static qgame.util.ValidationUtil.validateArg;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.IPlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

/**
 * Represents a Strategy that finds a Placement that is first in row-column order.
 * See PosnUtil.rowColumnCompare
 */
public class DagStrategy extends SmallestTileStrategy {

    public DagStrategy(BoardRule boardRule, MoveRule moveRule) {
        super(boardRule, moveRule);
    }

    public DagStrategy(IPlacementRule rule) {
        super(rule.getBoardRule(), rule.getMoveRule());
    }

    /**
     * 
     * Mutates given list of posns
     */
    @Override
    public Placement getBestPlacement(IPlayerGameState state, List<Placement> move, List<Posn> posns, Tile t) {
        validateArg(list -> !list.isEmpty(), posns, "posns cannot be empty");

        ArrayList<Posn> mutablePosns = new ArrayList<>(posns);
        mutablePosns.sort(PosnUtil::rowColumnCompare);
        return new Placement(mutablePosns.get(0), t);
    }
}