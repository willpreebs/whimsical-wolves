package qgame.harnesses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.util.RuleUtil;
import qgame.rule.placement.IPlacementRule;
import qgame.state.QPlayerGameState;
import qgame.state.IPlayerGameState;

import static qgame.json.JsonConverter.jCoordsFromPosns;
import static qgame.json.JsonConverter.qGameMapFromJMap;
import static qgame.json.JsonConverter.tileFromJTile;

//test files
public class XMap {

  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

    while (parser.hasNext()) {
      JsonElement input = parser.next();
      IMap map = qGameMapFromJMap(input);
      Tile t = tileFromJTile(parser.next());
      IPlacementRule rules = RuleUtil.createPlaceRules();
      IPlayerGameState basicGame = new QPlayerGameState(new ArrayList<>(), map, 10,
        new ArrayList<>(), "");
      List<Posn> positions = rules.validPositionsForTile(t, basicGame);
      JsonArray valid = jCoordsFromPosns(positions);
      System.out.println(valid);
    }
  }
}
