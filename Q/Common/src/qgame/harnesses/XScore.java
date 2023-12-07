package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonToObject;
import qgame.rule.scoring.IScoringRule;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.map.QMap;
import qgame.util.RuleUtil;

public class XScore {
  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
    JsonElement jMap = parser.next();
    JsonElement jPlacements = parser.next();

    QMap boardState = JsonToObject.qGameMapFromJMap(jMap);
    List<Placement> placements = JsonToObject.placementsFromJPlacements(jPlacements);
    IScoringRule rules = RuleUtil.createOldScoreRules();
    IGameState state = new QGameState(boardState);
    System.out.println(rules.pointsFor(placements, state));
  }
}
