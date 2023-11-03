package qgame.player.strategy;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.PlayerGameState;

public class AlwaysPassStrategy implements TurnStrategy {

  @Override
  public TurnAction chooseAction(PlayerGameState state) {
    return new PassAction();
  }
}
