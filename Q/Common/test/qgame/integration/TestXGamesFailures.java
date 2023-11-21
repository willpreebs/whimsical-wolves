package qgame.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import qgame.TestUtil;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class TestXGamesFailures {
    
    PlacementRule placementRules;

    BoardRule bRule;
    MoveRule mRule;

    @Before
    public void init() {
        placementRules = RuleUtil.createPlaceRules();
        bRule = RuleUtil.createBoardRules();
        mRule = RuleUtil.createMoveRules();
    }

    public GameResults getGameResults(String directory, int testNum) throws FileNotFoundException {
        List<JsonElement> elements = TestUtil.getJsonTestElements(directory, testNum);

        IGameState state = JsonConverter.jStateToOldQGameState(elements.get(0));
        // List<Player> players = JsonConverter.playersFromJActors(elements.get(1), placementRules);
        List<Player> players = JsonConverter.playersFromNewJActors(elements.get(1), bRule, mRule);
        state = JsonConverter.initializeNewStateWithNewPlayerList(state, players, false);
        QReferee ref = new QReferee();

        return ref.playGame(state, players);
    }

    @Test
    public void testAllIn7() {

        for (int dir = 0; dir < 42; dir++) {
            for (int testNum = 0; testNum < 10; testNum++) {
                try {
                    performTest(dir, testNum);
                } catch (FileNotFoundException e) {
                    continue;
                } catch (AssertionError e) {
                    System.out.println("Test " + dir + "/" + testNum + " failed");
                }
            }
        }
    }

    
    public void performTest(int dir, int testNum) throws FileNotFoundException {

        String directory = "7/grade/" + dir + "/";

        GameResults r = getGameResults(directory, testNum);

        JsonElement results = TestUtil.getJsonTestResult(directory, testNum);
        
        JsonArray expectedWinners = results.getAsJsonArray().get(0).getAsJsonArray();
        JsonArray expectedCheaters = results.getAsJsonArray().get(1).getAsJsonArray();

        List<String> expectedWinnerNames = 
        expectedWinners.asList()
        .stream()
        .map(e -> e.getAsString())
        .toList();

        List<String> expectedCheaterNames =
        expectedCheaters.asList()
        .stream()
        .map(e -> e.getAsString())
        .toList();

        assertEquals(expectedWinnerNames, r.getWinners());
        assertEquals(expectedCheaterNames, r.getRuleBreakers());
    }
}
