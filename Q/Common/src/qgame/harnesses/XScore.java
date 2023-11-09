package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.state.map.IMap;
import qgame.util.RuleUtil;
import qgame.rule.scoring.ScoringRule;
import qgame.state.Placement;

public class XScore {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jMap = parser.next();
    JsonElement jPlacements = parser.next();

    IMap boardState = JsonConverter.qGameMapFromJMap(jMap);
    List<Placement> placements = JsonConverter.placementsFromJPlacements(jPlacements);
    ScoringRule rules = RuleUtil.createOldScoreRules();
    System.out.println(rules.pointsFor(placements, boardState));
  }
}
