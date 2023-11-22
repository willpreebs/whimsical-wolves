package qgame.rule.placement.move;

import java.util.List;

import qgame.rule.placement.ARule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;

public abstract class MoveRule extends ARule {

    public abstract boolean canAddPlacementToMove(Placement placement, List<Placement> placements);
}
