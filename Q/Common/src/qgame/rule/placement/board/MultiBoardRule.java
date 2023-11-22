package qgame.rule.placement.board;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.move.EmptyMoveRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class MultiBoardRule extends BoardRule {

    private final List<BoardRule> rules;

    public MultiBoardRule(BoardRule... rules) {
        this.rules = new ArrayList<>(List.of(rules));
    }

    public MultiBoardRule(List<BoardRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    @Override
    public boolean isLegalPlacementOnBoard(Placement placement, IMap map) {
        for (BoardRule r : this.rules) {
            if (!r.isLegalPlacementOnBoard(placement, map)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BoardRule getBoardRule() {
        return this;
    }

    @Override
    public MoveRule getMoveRule() {
        return new EmptyMoveRule();
    }
    
}
