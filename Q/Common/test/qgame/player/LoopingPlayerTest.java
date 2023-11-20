package qgame.player;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.DagStrategy;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
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
}
