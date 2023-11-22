package qgame.player.strategy;

import static qgame.util.ValidationUtil.validateArg;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

public class DagStrategy extends SmallestRowColumnTileStrategy {

    public DagStrategy(BoardRule boardRule, MoveRule moveRule) {
        super(boardRule, moveRule);
    }

    public DagStrategy(PlacementRule rule) {
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