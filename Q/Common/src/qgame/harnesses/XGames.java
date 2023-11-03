package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.BasicQGameReferee;
import qgame.referee.GameResults;
import qgame.referee.QGameReferee;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.QGameState;

public class XGames {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jState = parser.next();
    JsonElement jActors = parser.next();
    PlacementRule placementRules = HarnessUtil.createPlaceRules();
    ScoringRule scoringRules = HarnessUtil.createScoreRules();
    QGameState state = JsonConverter.JStateToQGameState(jState);
    List<Player> players = JsonConverter.playersFromJActors(jActors, placementRules);
    QGameReferee ref = new BasicQGameReferee(placementRules, scoringRules, 10000, 6);
    GameResults results = ref.playGame(state, players);
    System.out.println(JsonConverter.jResultsFromGameResults(results));
  }
}
