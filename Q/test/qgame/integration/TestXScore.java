package qgame.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;

import qgame.TestUtil;
import qgame.json.JsonToObject;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.state.StateRule;
import qgame.rule.scoring.IScoringRule;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.map.QMap;
import qgame.util.RuleUtil;


public class TestXScore {

    IPlacementRule placementRules;

    BoardRule bRule;
    MoveRule mRule;
    StateRule sRule;

    IScoringRule scoreRules;

    @Before
    public void init() {
        // placementRules = RuleUtil.createPlaceRules();
        bRule = RuleUtil.createBoardRules();
        mRule = RuleUtil.createMoveRules();
        sRule = RuleUtil.createStateRules();
        scoreRules = RuleUtil.createScoreRules(1, 6, 1, 6);
        placementRules = new MultiPlacementRule(bRule, mRule, sRule);
    }

    public int getTestResults(String directory, int testNum) throws FileNotFoundException {
        List<JsonElement> elements = TestUtil.getJsonTestElements(directory, testNum);

        QMap boardState = JsonToObject.qGameMapFromJMap(elements.get(0));
        List<Placement> placements = JsonToObject.placementsFromJPlacements(elements.get(1));

        IScoringRule rules = RuleUtil.createOldScoreRules();
        IGameState state = new QGameState(boardState);

        return rules.pointsFor(placements, state);
    }

    @Test
    public void testAllIn5() {
        int numFails = 0;
        int numSuccess = 0;

        for (int dir = -1; dir < 43; dir++) {
            for (int testNum = 0; testNum < 5; testNum++) {
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

        for (int testNum = 0; testNum < 5; testNum++) {

        }


        System.out.println("tests failed: " + numFails + " and " + numSuccess + " passed");
    }

    @Test
    public void runIndividualTest() throws FileNotFoundException {
        performTest(1, 0);
    }

    
    public void performTest(int dir, int testNum) throws FileNotFoundException {

        String directory = "5/grade/" + dir + "/";

        int r = getTestResults(directory, testNum);

        JsonElement results = TestUtil.getJsonTestResult(directory, testNum);
        int expected = results.getAsInt();

        assertEquals(expected, r);
    }
}
