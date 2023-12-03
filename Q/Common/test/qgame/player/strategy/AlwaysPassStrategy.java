package qgame.player.strategy;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.rule.placement.IPlacementRule;
import qgame.state.IPlayerGameState;

public class AlwaysPassStrategy implements TurnStrategy {

  @Override
  public TurnAction chooseAction(IPlayerGameState state) {
    return new PassAction();
  }

  @Override
  public IPlacementRule getPlacementRule() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlacementRule'");
  }
}
