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

public class TestXGamesWithObserver {
    
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
        scoreRules = RuleUtil.createScoreRules();
        placementRules = new MultiPlacementRule(bRule, mRule, sRule);
    }

    public GameResults getGameResults(String directory, int testNum) throws FileNotFoundException {
        List<JsonElement> elements = TestUtil.getJsonTestElements(directory, testNum);

        IGameState state = JsonConverter.jStateToQGameState(elements.get(0));
        List<Player> players = JsonConverter.playersFromJActorSpecA(elements.get(1));
        state = JsonConverter.initializeNewStateWithNewPlayerList(state, players, true);
        QReferee ref = new QReferee(placementRules, scoreRules, 10000);

        return ref.playGame(state, players);
    }

    @Test
    public void testAllIn8() {
        int numFails = 0;
        int numSuccess = 0;

        for (int dir = 0; dir < 42; dir++) {
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
        performTest(36, 4);
    }
    
    public void performTest(int dir, int testNum) throws FileNotFoundException {

        String directory = "8/grade/" + dir + "/";

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
