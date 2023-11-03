//package qgame.harnesses;
//
//import com.google.gson.JsonStreamParser;
//
//import java.io.InputStreamReader;
//import java.util.List;
//
//import qgame.gui.GameStateView;
//import qgame.json.JsonConverter;
//import qgame.map.QGameMap;
//import qgame.rule.scoring.MultiScoringRule;
//import qgame.rule.scoring.ScoringRule;
//import qgame.state.BasicPlayerGameState;
//import qgame.state.Placement;
//import qgame.state.PlayerGameState;
//
//public class guiTest {
//  public static void main(String[] args) {
//    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
//    QGameMap map = JsonConverter.qGameMapFromJMap(parser.next());
//    List<Placement> placements = JsonConverter.placementsFromJPlacements(parser.next());
//
//    PlayerGameState plaerState = new BasicPlayerGameState(List.of(10, 20), map,0,
//      placements.stream().map(Placement::tile).toList());
//
//    GameStateView view = new GameStateView(plaerState);
//
//    ScoringRule set = new MultiScoringRule();
//    System.out.println(set.pointsFor(placements, plaerState));
//  }
//}
