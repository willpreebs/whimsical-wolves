package qgame.referee;

import java.util.ArrayList;
import java.util.List;

import static qgame.util.ValidationUtil.nonNullObj;
/**
 * Represents the results of a game of Q game. It returns a list with the names of all winners of
 * this game and all rule breakers.
 */
public class GameResults {

  private final List<String> winners;

  private final List<String> ruleBreakers;

  public GameResults() {
    this.winners = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
  }

  /**
   *
   * @param winners
   * @param ruleBreakers
   */
  public GameResults(List<String> winners, List<String> ruleBreakers) {
    nonNullObj(winners, "Winners");
    nonNullObj(ruleBreakers, "Rule Breakers");
    this.winners = new ArrayList<>(winners);
    this.ruleBreakers = new ArrayList<>(ruleBreakers);
  }

  public List<String> getWinners() {
    return new ArrayList<>(this.winners);
  }

  public List<String> getRuleBreakers() {
    return new ArrayList<>(this.ruleBreakers);
  }

}
