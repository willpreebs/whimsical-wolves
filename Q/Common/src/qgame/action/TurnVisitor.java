package qgame.action;

/**
 * A visitor that performs operations on TurnActions. Assumes that the TurnAction interface is
 * only implemented by ExchangeAction, PassAction, and PlaceAction.
 */
public interface TurnVisitor<T> {

  /**
   * Visit a turn action, so that the action can accept the visitor and the visitor can operate
   * on the specific implementation of turn action
   * @param action The action the visitor wants to operate on.
   * @throws IllegalArgumentException If the given action is null.
   */
  T visit(TurnAction action) throws IllegalArgumentException;

  /**
   * Perform functionality intended for a pass action.
   */
  T visitPass();

  /**
   * Perform functionality that is intended for an exchange action.
   * @param action The action the visitor wants to perform its functionality on.
   * @throws IllegalArgumentException If the given action is null.
   */
  T visitExchange(ExchangeAction action);

  /**
   *
   * @param action The action the visitor wants to perform its functionality on.
   * @throws IllegalArgumentException If the given action is null. Or if the PlaceAction contains
   * nulls in it.
   */
  T visitPlacements(PlaceAction action) throws IllegalArgumentException;
}
