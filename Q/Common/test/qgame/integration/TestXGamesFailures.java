package qgame.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import qgame.TestUtil;
import qgame.json.JsonConverter;
import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.state.StateRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class TestXGamesFailures {
    
    IPlacementRule placementRules;

    BoardRule bRule;
    MoveRule mRule;
    StateRule sRule;

    ScoringRule scoreRules;

    @Before
    public void init() {
        // placementRules = RuleUtil.createPlaceRules();
        bRule = RuleUtil.createBoardRules();
        mRule = RuleUtil.createMoveRules();
        sRule = RuleUtil.createStateRules();
        scoreRules = RuleUtil.createScoreRules(1, 6, 1, 6);
        placementRules = new MultiPlacementRule(bRule, mRule, sRule);
    }

    public GameResults getGameResults(String directory, int testNum) throws FileNotFoundException {
        List<JsonElement> elements = TestUtil.getJsonTestElements(directory, testNum);

        IGameState state = JsonToObject.jStateToOldQGameState(elements.get(0));
        List<Player> players = JsonToObject.playersFromJActors(elements.get(1), placementRules);
        state = JsonToObject.initializeNewStateWithNewPlayerList(state, players, false);
        QReferee ref = new QReferee(placementRules, scoreRules, 10000);

        return ref.playGame(state, players);
    }

    @Test
    public void testAllIn7() {
        int numFails = 0;
        int numSuccess = 0;

        for (int dir = -1; dir < 42; dir++) {
            for (int testNum = 0; testNum < 10; testNum++) {
                try {
                    performTest(dir, testNum);
                    numSuccess++;
                } catch (FileNotFoundException e) {
                    continue;
                } catch (AssertionError e) {
                    System.out.println("Test " + dir + "/" + testNum + " failed");
                    numFails++;
                }
            }
        }
        System.out.println("tests failed: " + numFails + " and " + numSuccess + " passed");
    }

    @Test
    public void runIndividualTest() throws FileNotFoundException {
        performTest(-1, 0);
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
