package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonStreamParser;

import qgame.gui.GameStateView;
import qgame.json.JsonConverter;
import qgame.json.JsonToObject;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.QPlayerGameState;
import qgame.state.map.IMap;
public class PlayerStateGuiTest {
 public static void main(String[] args) {
   JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
   IMap map = JsonToObject.qGameMapFromJMap(parser.next());
   List<Placement> placements = JsonToObject.placementsFromJPlacements(parser.next());
    renderTestPlayerState(map, placements);
 }

 private static void renderTestPlayerState(IMap map, List<Placement> placements) {
    IPlayerGameState playerState = new QPlayerGameState(List.of(10, 20), map,0,
     placements.stream().map(Placement::tile).toList(), "test player");

   GameStateView view = new GameStateView(playerState);

 }
}
