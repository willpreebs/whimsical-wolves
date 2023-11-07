package qgame.harnesses;

import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.gui.GameStateView;
import qgame.json.JsonConverter;
import qgame.rule.scoring.MultiScoringRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.QPlayerGameState;
import qgame.state.map.IMap;
public class PlayerStateGuiTest {
 public static void main(String[] args) {
   JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
   IMap map = JsonConverter.qGameMapFromJMap(parser.next());
   List<Placement> placements = JsonConverter.placementsFromJPlacements(parser.next());
    renderTestPlayerState(map, placements);
 }

 private static void renderTestPlayerState(IMap map, List<Placement> placements) {
    IPlayerGameState playerState = new QPlayerGameState(List.of(10, 20), map,0,
     placements.stream().map(Placement::tile).toList(), "test player");

   GameStateView view = new GameStateView(playerState);

 }
}
