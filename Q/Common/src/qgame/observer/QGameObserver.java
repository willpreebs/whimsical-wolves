package qgame.observer;

import static qgame.util.ValidationUtil.nonNullObj;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import qgame.gui.ObserverView;
import qgame.json.JsonConverter;
import qgame.player.PlayerInfo;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class QGameObserver implements IGameObserver {

    // List<IGameState> previous;
    // IGameState current;
    // IGameState next;

    // GameStates in order that they occur in the game
    List<IGameState> states;
    int stateIndex = 0;

    private final int REF_TILES = 6;


    @Override
    public void receiveState(IGameState state) {
        nonNullObj(state, "State cannot be null");
        states.add(state);
        // render gui?
    }

    public void next() {
        stateIndex++;
    }

    public void previous() {
        stateIndex--;
    }

    public void save(String filepath) {
        JsonElement jState = JsonConverter.jStateFromQGameState(this.states.get(stateIndex));
        try {
            // File f = new File(filepath, )
            FileWriter w = new FileWriter(filepath);
            w.write(jState.toString());
            w.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot create or write to file: " + filepath);
        }
    }


    @Override
    public void gameOver() {
        // ...
    }

    public void saveStatesAsPng() {

        for (int i = 0; i < this.states.size(); i++) {
            JFrame frame = new ObserverView(states.get(i), REF_TILES);
            BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
            frame.paint(img.createGraphics());
            try {
                ImageIO.write(img, "png", new File("/Tmp/" + i + ".png"));
            }
            catch (IOException e) {
                throw new IllegalStateException("Issue writing to file: " + e.getMessage());
            }
        }
    }
}