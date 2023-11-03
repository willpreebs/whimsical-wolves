package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.state.map.QGameMap;
import qgame.rule.scoring.ScoringRule;
import qgame.state.Placement;

public class XScore {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jMap = parser.next();
    JsonElement jPlacements = parser.next();

    QGameMap boardState = JsonConverter.qGameMapFromJMap(jMap);
    List<Placement> placements = JsonConverter.placementsFromJPlacements(jPlacements);
    ScoringRule rules = HarnessUtil.createScoreRules();
    System.out.println(rules.pointsFor(placements, boardState));
  }
}
