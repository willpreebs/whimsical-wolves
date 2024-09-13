package qgame.harnesses;

import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.gui.ObserverView;
import qgame.json.JsonToObject;
import qgame.observer.QGameObserver;
import qgame.state.IGameState;

public class ObserverViewGuiTest {
    

    public static void main (String[] args) {

        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement jstate = parser.next();
        IGameState state = JsonToObject.jStateToQGameState(jstate);

        ObserverView view = new ObserverView(new QGameObserver(), state, 6);
        view.setVisible(true);
    }
}
