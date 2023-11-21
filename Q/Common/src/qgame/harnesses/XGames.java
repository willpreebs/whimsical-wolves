package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.QReferee;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class XGames {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jState = parser.next();
    JsonElement jActors = parser.next();
    PlacementRule placementRules = RuleUtil.createPlaceRules();
    ScoringRule scoringRules = RuleUtil.createOldScoreRules();
    IGameState state = JsonConverter.jStateToOldQGameState(jState);
    List<Player> players = JsonConverter.playersFromJActors(jActors, placementRules);
    state = JsonConverter.initializeNewStateWithNewPlayerList(state, players, false);

    IReferee ref = new QReferee(placementRules, scoringRules, 10000);
    GameResults results = ref.playGame(state, players);
    System.out.println(JsonConverter.jResultsFromGameResults(results));
  }
}
