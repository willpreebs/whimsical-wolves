package qgame.player.strategy;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.IPlayerGameState;

public class AlwaysPassStrategy implements TurnStrategy {

  @Override
  public TurnAction chooseAction(IPlayerGameState state) {
    return new PassAction();
  }
}
