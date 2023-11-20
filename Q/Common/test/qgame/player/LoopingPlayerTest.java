package qgame.player;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.DagStrategy;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.state.IGameState;
import qgame.state.QGameState;
import qgame.util.RuleUtil;

public class LoopingPlayerTest {
    

    @Test
    public void testPlayerLoopOnSetup() {

        DagStrategy d = new DagStrategy(RuleUtil.createPlaceRules());
        FailStep f = FailStep.SETUP;

        LoopingAIPlayer looper = new LoopingAIPlayer("looper", d, f, 1);
        SimpleAIPlayer normal = new SimpleAIPlayer("normal", d);

        QReferee r = new QReferee();
        GameResults results = r.playGame(List.of(looper, normal), 100);

        List<String> expectedWinners = List.of(normal.name());
        List<String> expectedCheaters = List.of(looper.name());
        
        assertEquals(expectedWinners, results.getWinners());
        assertEquals(expectedCheaters, results.getRuleBreakers());
    }

    @Test
    public void testPlayerLoopOnWin() {
        DagStrategy d = new DagStrategy(RuleUtil.createPlaceRules());
        FailStep f = FailStep.WIN;

        LoopingAIPlayer looper = new LoopingAIPlayer("looper", d, f, 1);
        SimpleAIPlayer normal = new SimpleAIPlayer("normal", d);

        QReferee r = new QReferee();
        GameResults results = r.playGame(List.of(looper, normal), 100);

        List<String> expectedWinners = List.of();
        List<String> expectedCheaters = List.of(looper.name());
        
        assertEquals(expectedWinners, results.getWinners());
        assertEquals(expectedCheaters, results.getRuleBreakers());
    }

    @Test
    public void parallelTestTwo() {
        String stateString = "{\"map\":[[-3,[0,{\"color\":\"green\",\"shape\":\"8star\"}]]],\"tile*\":[{\"color\":\"purple\",\"shape\":\"8star\"},{\"color\":\"green\",\"shape\":\"circle\"}],\"players\":[{\"score\":0,\"name\":\"Tester\",\"tile*\":[{\"color\":\"orange\",\"shape\":\"square\"},{\"color\":\"green\",\"shape\":\"clover\"}]},{\"score\":0,\"name\":\"SecondTester\",\"tile*\":[{\"color\":\"orange\",\"shape\":\"square\"},{\"color\":\"green\",\"shape\":\"clover\"}]},{\"score\":11,\"name\":\"looper\",\"tile*\":[{\"color\":\"orange\",\"shape\":\"square\"},{\"color\":\"green\",\"shape\":\"clover\"}]}]}";
        String actorString = "[[\"Tester\",\"dag\"],[\"SecondTester\",\"ldasg\"],[\"looper\",\"dag\", \"win\", 1]]";
        
        String totalString = stateString + actorString;

        InputStream in = new ByteArrayInputStream(totalString.getBytes());
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));

        JsonElement jStateJson = parser.next();
        JsonElement jActorSpecBJson = parser.next();

        IGameState state = JsonConverter.jStateToQGameState(jStateJson);
        List<Player> players = JsonConverter.playersFromJActorSpecB(jActorSpecBJson);

        state = JsonConverter.initializeNewStateWithNewPlayerList(state, players);

        IReferee ref = new QReferee(RuleUtil.createPlaceRules(), RuleUtil.createScoreRules(6), 20000);
        GameResults gr = ref.playGame(state, players);

        List<String> expectedWinners = List.of();
        List<String> expectedCheaters = List.of("looper");

        assertEquals(expectedWinners, gr.getWinners());
        assertEquals(expectedCheaters, gr.getRuleBreakers());
    }
}
