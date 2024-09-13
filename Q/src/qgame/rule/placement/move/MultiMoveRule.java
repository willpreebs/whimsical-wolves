package qgame.rule.placement.move;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.board.EmptyBoardRule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;

public class MultiMoveRule extends MoveRule {

    private final List<MoveRule> rules;

    public MultiMoveRule(MoveRule... rules) {
        this.rules = new ArrayList<>(List.of(rules));
    }

    public MultiMoveRule(List<MoveRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    @Override
    public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState gameState) {
        for (MoveRule m : this.rules) {
            if (!m.isPlacementListLegal(placements, gameState)) {
                return false;
            }
        }
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
        for (MoveRule m : this.rules) {
            if (!m.canAddPlacementToMove(placement, placements)) {
                return false;
            }
        }
        return true;
    }
    
}
