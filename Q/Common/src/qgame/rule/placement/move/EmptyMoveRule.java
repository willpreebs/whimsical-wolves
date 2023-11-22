package qgame.rule.placement.move;

import java.util.List;

import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.board.EmptyBoardRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;

public class EmptyMoveRule extends MoveRule {

    @Override
    public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState gameState) {
        return true;
    }

    @Override
    public BoardRule getBoardRule() {
        return new EmptyBoardRule();
    }

    @Override
    public MoveRule getMoveRule() {
        return this;
    }

    @Override
    public boolean canAddPlacementToMove(Placement placement, List<Placement> placements) {
        return true;
    }
    
}
