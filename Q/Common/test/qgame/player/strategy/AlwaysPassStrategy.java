package qgame.player.strategy;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.player.strategy.TurnStrategy;
import qgame.rule.placement.PlacementRule;
import qgame.state.IPlayerGameState;

public class AlwaysPassStrategy implements TurnStrategy {

  @Override
  public TurnAction chooseAction(IPlayerGameState state) {
    return new PassAction();
  }

  @Override
  public PlacementRule getPlacementRule() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlacementRule'");
  }
}
