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
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class TestXGamesFailures {
    
    PlacementRule placementRules;

    @Before
    public void init() {
        placementRules = RuleUtil.createPlaceRules();
    }

    public GameResults getGameResults(String directory, int testNum) throws FileNotFoundException {
        List<JsonElement> elements = TestUtil.getJsonTestElements(directory, testNum);

        IGameState state = JsonConverter.jStateToOldQGameState(elements.get(0));
        List<Player> players = JsonConverter.playersFromJActors(elements.get(1), placementRules);
        state = JsonConverter.initializeNewStateWithNewPlayerList(state, players, false);
        QReferee ref = new QReferee();

        return ref.playGame(state, players);
    }

    @Test
    public void test7_0_4() throws FileNotFoundException {

        GameResults r = getGameResults("7/grade/0/", 4);

        JsonElement results = TestUtil.getJsonTestResult("7/grade/0/", 4);
        
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
