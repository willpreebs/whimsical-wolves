package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.player.strategy.TurnStrategy;
import qgame.rule.placement.PlacementRule;
import qgame.state.PlayerGameState;

public class XStrategy {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement strat = parser.next();
    JsonElement jPub = parser.next();
    PlacementRule rules = HarnessUtil.createPlaceRules();
    PlayerGameState state = JsonConverter.playerGameStateFromJPub(jPub);
    TurnStrategy strategy = JsonConverter.jStrategyToStrategy(strat, rules);
    TurnAction action = strategy.chooseAction(state);
    System.out.println(JsonConverter.actionToJson(action));
  }
}
