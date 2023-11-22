package qgame.harnesses;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.state.IGameState;
import qgame.util.RuleUtil;

public class XBaddies {


    public static void main(String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jStateJson = parser.next();
        JsonElement jActorSpecBJson = parser.next();

        IGameState state = JsonConverter.jStateToQGameState(jStateJson);
        List<Player> players = JsonConverter.playersFromJActorSpecB(jActorSpecBJson);

        state = JsonConverter.initializeNewStateWithNewPlayerList(state, players);

        IReferee ref = new QReferee(RuleUtil.createPlaceRules(), RuleUtil.createScoreRules(), 20000);
        GameResults gr = ref.playGame(state, players);
        System.out.println(JsonConverter.jResultsFromGameResults(gr));
    }
}