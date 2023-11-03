package qgame.action;

/**
 * A representation of the possible moves a player can make during their turn in QGame. Supports
 * accepting a turn visitor which performs the move.
 */
public interface TurnAction {
  /**
   * Determines if the action was completed successfully.
   *
   * @param visitor a TurnVisitor
   * @return if the TurnAction was completed successfully
   */
  <T> T accept(TurnVisitor<T> visitor);
}
