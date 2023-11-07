package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.observer.IGameObserver;
import qgame.observer.QGameObserver;
import qgame.player.Player;
import qgame.referee.QReferee;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;

public class XGamesWithObserver {
    
    public static void main(String[]args) {
        PlacementRule rules = HarnessUtil.createPlaceRules();
        ScoringRule scoreRules = HarnessUtil.createScoreRules();
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jState = parser.next();
        JsonElement jActorSpecA = parser.next();

        IGameState state = JsonConverter.JStateToQGameState(jState);
        List<Player> players = JsonConverter.playersFromJActorSpecA(jActorSpecA, rules);

        boolean withObserver = false;
        if (args.length >= 1) {
            if (args[0].equals("-show")) {
                withObserver = true;
            }
        }
        
        IReferee ref = new QReferee(rules, scoreRules, 10000);
        if (withObserver) {
            IGameObserver observer = new QGameObserver();
            ref = new QReferee(rules, scoreRules, 10000, List.of(observer));
        }

        GameResults r = ref.playGame(state, players);
        System.out.println(JsonConverter.jResultsFromGameResults(r));
    }
}
