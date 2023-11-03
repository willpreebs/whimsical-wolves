package qgame.player.strategy;

import qgame.action.TurnAction;
import qgame.state.PlayerGameState;

/**
 * Represents a player's decision process for when they make a turn.
 * All players have at least one strategy they use to determine what TurnAction
 * (pass, exchange, or placement) they should do in a turn.
 */
public interface TurnStrategy {
  /**
   * Decide what to do for a players turn, and return that as a TurnAction.
   * @param state The game state to determine an action for
   * @return The action the strategy decides to take for this state.
   */
  TurnAction chooseAction(PlayerGameState state);
}
