package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.json.JsonToObject;
import qgame.state.map.IMap;
import qgame.util.RuleUtil;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.state.QGameState;

public class XScore {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jMap = parser.next();
    JsonElement jPlacements = parser.next();

    IMap boardState = JsonToObject.qGameMapFromJMap(jMap);
    List<Placement> placements = JsonToObject.placementsFromJPlacements(jPlacements);
    ScoringRule rules = RuleUtil.createOldScoreRules();
    IGameState state = new QGameState(boardState);
    System.out.println(rules.pointsFor(placements, state));
  }
}
