package qgame.action;

import java.util.ArrayList;
import java.util.List;

import qgame.state.Placement;

import static qgame.util.ValidationUtil.nonNullObj;

/**
 * An implementation of a Turn Action. Placements should be ordered chronologically with the first
 * move at the start of the list and the last move at the end of the list. Supports returning the
 * list of placements a player wishes to make on the QGame board while keeping the order of
 * placements preserved, and confirming that the PlaceAction has been completed without issue.
 */
public final class PlaceAction implements TurnAction {

  private final List<Placement> placementList;

  /**
   * Constructs a PlaceAction.
   * @param placementList a list of placements to be made
   * @throws IllegalArgumentException if a null placement is in the list of placements
   */
  public PlaceAction(List<Placement> placementList) throws IllegalArgumentException {
    nonNullObj(placementList, "placements cannot be null.");
    this.placementList = new ArrayList<>(placementList);
  }

  /**
   * Gets the placements a player is making while maintaining the order.
   * @return The list of ordered placements
   */
  public List<Placement> placements() {
    return new ArrayList<>(this.placementList);
  }

  @Override
  public <T> T accept(TurnVisitor<T> visitor) {

    return visitor.visitPlacements(this);
  }
}
