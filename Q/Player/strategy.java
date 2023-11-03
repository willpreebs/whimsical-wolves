package qgame.player.strategy;

import java.util.List;
import java.util.Optional;

import qgame.action.TurnAction;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

/**
 * Represents a player's decision process for when they make a turn.
 * All players have at least one strategy they use to determine what TurnAction
 * (pass, exchange, or placement) they should do in a turn.
 */
public interface TurnStrategy {
  /**
   * Decide what to do for a players turn, and return that as a TurnAction.
   * @param startState The game state to determine an action for
   * @return The action the strategy decides to take for this state.
   */
  Optional<Placement> choosePlacementOrNot(PlayerGameState startState,
                                   List<Placement> placements);
}
