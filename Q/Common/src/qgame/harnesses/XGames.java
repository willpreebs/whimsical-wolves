package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class XGames {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jState = parser.next();
    JsonElement jActors = parser.next();
    IPlacementRule placementRules = RuleUtil.createPlaceRules();
    ScoringRule scoringRules = RuleUtil.createOldScoreRules();
    IGameState state = JsonToObject.jStateToOldQGameState(jState);
    List<Player> players = JsonToObject.playersFromJActors(jActors, placementRules);
    state = JsonToObject.initializeNewStateWithNewPlayerList(state, players, false);

    IReferee ref = new QReferee(placementRules, scoringRules, 10000);
    GameResults results = ref.playGame(state, players);
    System.out.println(ObjectToJson.jResultsFromGameResults(results));
  }
}
