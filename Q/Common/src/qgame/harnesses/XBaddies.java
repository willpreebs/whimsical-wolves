package qgame.harnesses;

import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.state.IGameState;
import qgame.state.QGameState;

import qgame.json.JsonConverter;
import qgame.player.Player;

public class XBaddies {


    public static void main(String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jStateJson = parser.next();
        JsonElement jActorSpecBJson = parser.next();

        IGameState state = JsonConverter.jStateToQGameState(jStateJson);
        List<Player> players = JsonConverter.playersFromJActorSpecB(jActorSpecBJson);
    }
}