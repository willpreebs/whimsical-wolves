package qgame.rule.placement.state;

import qgame.rule.placement.ARule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;

/**
 * A State Rule is a placement rule that takes in a GameState to determine if the placement is legal
 */
public abstract class StateRule extends ARule {

    public abstract boolean canAddPlacementGivenState(Placement p, IPlayerGameState state);
}
