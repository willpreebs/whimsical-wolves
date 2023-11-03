package qgame.harnesses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.rule.placement.PlacementRule;
import qgame.state.BasicPlayerGameState;
import qgame.state.PlayerGameState;

import static qgame.json.JsonConverter.jCoordsFromPosns;
import static qgame.json.JsonConverter.qGameMapFromJMap;
import static qgame.json.JsonConverter.tileFromJTile;

//test files
public class XMap {

  public static void main(String[] args) {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

    while (parser.hasNext()) {
      JsonElement input = parser.next();
      QGameMap map = qGameMapFromJMap(input);
      Tile t = tileFromJTile(parser.next());
      PlacementRule rules = HarnessUtil.createPlaceRules();
      PlayerGameState basicGame = new BasicPlayerGameState(new ArrayList<>(), map, 10,
        new ArrayList<>());
      List<Posn> positions = rules.validPositionsForTile(t, basicGame);
      JsonArray valid = jCoordsFromPosns(positions);
      System.out.println(valid);
    }
  }
}
