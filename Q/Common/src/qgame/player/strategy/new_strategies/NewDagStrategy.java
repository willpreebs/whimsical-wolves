package qgame.player.strategy.new_strategies;

import static qgame.util.ValidationUtil.validateArg;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.state.StateRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

public class NewDagStrategy extends NewSmallestRowColumnTileStrategy {

    public NewDagStrategy(BoardRule boardRule, MoveRule moveRule) {
        super(boardRule, moveRule);
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
