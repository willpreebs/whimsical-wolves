package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.observer.IGameObserver;
import qgame.observer.QGameObserver;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class XGamesWithObserver {
    
    public static void main(String[]args) {

        IPlacementRule rules = RuleUtil.createPlaceRules();
        ScoringRule scoreRules = RuleUtil.createScoreRules();
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jState = parser.next();
        JsonElement jActorSpecA = parser.next();

        IGameState state = JsonToObject.jStateToQGameState(jState);
        List<Player> players = JsonToObject.playersFromJActorSpecA(jActorSpecA);

        state = JsonToObject.initializeNewStateWithNewPlayerList(state, players);

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
        System.out.println(ObjectToJson.jResultsFromGameResults(r));
    }
}
