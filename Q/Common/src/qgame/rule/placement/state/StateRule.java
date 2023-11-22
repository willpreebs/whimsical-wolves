package qgame.rule.placement.state;

import java.util.List;

import qgame.rule.placement.ARule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;

public abstract class StateRule extends ARule {

    public abstract boolean canAddPlacementGivenState(Placement p, IPlayerGameState state);
}
