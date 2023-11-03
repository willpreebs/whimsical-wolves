package qgame.action;

/**
 * An implementation of TurnAction. Supports confirming that the PassAction has been completed
 * without issue.
 */
public final class PassAction implements TurnAction {
  @Override
  public <T> T accept(TurnVisitor<T> visitor) {
    return visitor.visitPass();
  }
}
