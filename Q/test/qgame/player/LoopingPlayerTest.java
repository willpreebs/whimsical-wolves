package qgame.player;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonToObject;
import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.DagStrategy;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
import qgame.state.IGameState;
import qgame.util.RuleUtil;
import qgame.util.ValidationUtil;

public class LoopingPlayerTest {

    String testDirectory = "9/Tests/";

    public List<JsonElement> parallelizeTest(String filepath) throws FileNotFoundException {
        File f = new File(filepath);

        InputStream in = new FileInputStream(f);

        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));

        List<JsonElement> elements = new ArrayList<>();

        while (parser.hasNext()) {
            elements.add(parser.next());
        }

        return elements;
    }
    

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
    public void testPlayerLoopOnWin() throws FileNotFoundException {
        DagStrategy d = new DagStrategy(RuleUtil.createPlaceRules());
        FailStep f = FailStep.WIN;

        LoopingAIPlayer looper = new LoopingAIPlayer("looper", d, f, 1);
        SimpleAIPlayer normal = new SimpleAIPlayer("Tester", d);
        SimpleAIPlayer normal2 = new SimpleAIPlayer("SecondTester", d);

        List<JsonElement> els = parallelizeTest(testDirectory + "0-in.json");
        IGameState state = JsonToObject.jStateToQGameState(els.get(0));

        List<Player> players = List.of(normal, normal2, looper);

        state = JsonToObject.initializeNewStateWithNewPlayerList(state, players);

        QReferee r = new QReferee();
        GameResults results = r.playGame(state, players);

        List<String> expectedWinners = List.of();
        List<String> expectedCheaters = List.of(looper.name());
        
        assertEquals(expectedWinners, results.getWinners());
        assertEquals(expectedCheaters, results.getRuleBreakers());
    }
    
    public GameResults getResultsFromJsonTest(int testNo) throws FileNotFoundException {
        List<JsonElement> elements = parallelizeTest(testDirectory + testNo + "-in.json");
        ValidationUtil.validateArg(list -> list.size() == 2, elements, "problem with input file");

        JsonElement jStateJson = elements.get(0);
        JsonElement jActorSpecBJson = elements.get(1);

        IGameState state = JsonToObject.jStateToQGameState(jStateJson);
        List<Player> players = JsonToObject.playersFromJActorSpecB(jActorSpecBJson);

        state = JsonToObject.initializeNewStateWithNewPlayerList(state, players);

        QReferee ref = new QReferee(RuleUtil.createPlaceRules(), RuleUtil.createScoreRules(), 20000);
        GameResults gr = ref.playGame(state, players);
        return gr;
    }

    @Test
    public void parallelTestTwo() throws FileNotFoundException {

        GameResults gr = getResultsFromJsonTest(2);

        List<String> expectedWinners = List.of();
        List<String> expectedCheaters = List.of("looper");

        assertEquals(expectedWinners, gr.getWinners());
        assertEquals(expectedCheaters, gr.getRuleBreakers());
    }
}
