package qgame.harnesses;

import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.gui.ObserverView;
import qgame.json.JsonConverter;
import qgame.state.IGameState;
import qgame.state.map.IMap;

public class ObserverViewGuiTest {
    

    public static void main (String[] args) {

        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jstate = parser.next();
        IGameState state = JsonConverter.JStateToQGameState(jstate);

        ObserverView view = new ObserverView(state, 6);
        view.setVisible(true);
    }
}
