package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

public class XLegal {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jPub = parser.next();
    JsonElement jPlacements = parser.next();

    PlayerGameState gameState = JsonConverter.playerGameStateFromJPub(jPub);
    List<Placement> placements = JsonConverter.placementsFromJPlacements(jPlacements);
    PlacementRule rules = HarnessUtil.createPlaceRules();
    if (rules.validPlacements(placements, gameState)) {
      placements.forEach(gameState::makePlacement);
      JsonElement jMap = JsonConverter.jMapFromQGameMap(gameState.viewBoard());
      System.out.println(jMap);
    }
    else {
      System.out.println("false");
    }
  }
}
