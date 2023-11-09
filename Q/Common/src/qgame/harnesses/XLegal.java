package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.util.RuleUtil;
import qgame.state.IPlayerGameState;

public class XLegal {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jPub = parser.next();
    JsonElement jPlacements = parser.next();

    IPlayerGameState gameState = JsonConverter.playerGameStateFromJPub(jPub);
    List<Placement> placements = JsonConverter.placementsFromJPlacements(jPlacements);
    PlacementRule rules = RuleUtil.createPlaceRules();
    if (rules.isPlacementListLegal(placements, gameState)) {
      placements.forEach(gameState::makePlacement);
      JsonElement jMap = JsonConverter.jMapFromQGameMap(gameState.getBoard());
      System.out.println(jMap);
    }
    else {
      System.out.println("false");
    }
  }
}
