package qgame.rule.placement.board;

import qgame.rule.placement.move.EmptyMoveRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.Placement;
import qgame.state.map.QMap;

/**
 * A BoardRule that always approves a Placement
 */
public class EmptyBoardRule extends BoardRule {

    @Override
    public BoardRule getBoardRule() {
        return this;
    }

    @Override
    public MoveRule getMoveRule() {
        return new EmptyMoveRule();
    }

    @Override
    public boolean isLegalPlacementOnBoard(Placement placement, QMap map) {
        return true;
    }
    
}
