package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class XBaddies {


    public static void main(String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jStateJson = parser.next();
        JsonElement jActorSpecBJson = parser.next();

        IGameState state = JsonToObject.jStateToQGameState(jStateJson);
        List<Player> players = JsonToObject.playersFromJActorSpecB(jActorSpecBJson);

        state = JsonToObject.initializeNewStateWithNewPlayerList(state, players);

        QReferee ref = new QReferee(RuleUtil.createPlaceRules(), RuleUtil.createScoreRules(), 20000);
        GameResults gr = ref.playGame(state, players);
        System.out.println(ObjectToJson.jResultsFromGameResults(gr));
    }
}