package qgame.harnesses;

import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.action.TurnAction;
import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.player.strategy.TurnStrategy;
import qgame.rule.placement.IPlacementRule;
import qgame.state.IPlayerGameState;
import qgame.util.RuleUtil;

public class XStrategy {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement strat = parser.next();
    JsonElement jPub = parser.next();
    IPlacementRule rules = RuleUtil.createPlaceRules();
    IPlayerGameState state = JsonToObject.playerGameStateFromJPub(jPub);
    TurnStrategy strategy = JsonToObject.jStrategyToStrategy(strat, rules);
    TurnAction action = strategy.chooseAction(state);
    System.out.println(ObjectToJson.actionToJson(action));
  }
}
