package qgame.observer;

import static qgame.util.ValidationUtil.nonNullObj;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import qgame.state.QGameState;
import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;

public class QGameObserver implements IGameObserver {

    // List<IGameState> previous;
    // IGameState current;
    // IGameState next;

    // GameStates in order that they occur in the game
    List<IGameState> states;
    int stateIndex = -1;

    ObserverView stateFrame;

    private final int REF_TILES = 6;
    // states:
    // [0]
    // size = 1
    // index = 0

    public QGameObserver() {
        this.states = new ArrayList<>();
        //this.states.add(new QGameState());
        stateFrame = new ObserverView(this, new QGameState(), REF_TILES);
        stateFrame.setVisible(true);
    }

  /**
   * Gives the observer the game state and saves a GUI representation of the state
   * in an image. see saveStateAsPng()
   * @param state
   */
    @Override
    public void receiveState(IGameState state) {
        nonNullObj(state, "State cannot be null");
        states.add(new QGameState(state));
        saveStateAsPng(states.size() - 1);
    }

    /**
     * Render the 
     */
    @Override
    public void next() {
        if (stateIndex < this.states.size() - 1) {
            stateIndex++;
            System.out.println("Next pressed; index set to: " + stateIndex);
        }
        renderCurrentState();
    }

    @Override
    public void previous() {
        // System.out.println("previous");
        if (stateIndex > 0) {
            stateIndex--;
            System.out.println("Previous pressed; index set to: " + stateIndex);
        }
        renderCurrentState();
    }

    private IGameState getCurrentState() {
        return this.states.get(this.stateIndex);
    }

    private void renderCurrentState() {
        System.out.println("Rendering state at index: " + this.stateIndex);
        stateFrame.updateFrame(this, this.getCurrentState());
        this.stateFrame.pack();
    }

    @Override
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

    /**
     * Saves the frame at the given index as an image.
     * @param index
     */
    private void saveStateAsPng(int index) {
        JFrame currentFrame = new ObserverView(this, this.states.get(index), REF_TILES);
        currentFrame.setVisible(true);
        BufferedImage img = new BufferedImage(currentFrame.getWidth(), currentFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        currentFrame.paintAll(g);
        File f = new File("8/Tmp/" + index + ".png");
        try {
            ImageIO.write(img, "png", f);
            currentFrame.setVisible(false);
        }
        catch (IOException e) {
            throw new IllegalStateException("Issue writing to file: " + e.getMessage());
        }
    }

    public void saveStatesAsPng() {

        for (int i = 0; i < this.states.size(); i++) {
            JFrame frame = new ObserverView(this, states.get(i), REF_TILES);
            frame.setVisible(true);
            BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            frame.paintAll(g);
            //frame.paint(g);
            File f = new File("8/Tmp/" + i + ".png");
            try {
                ImageIO.write(img, "png", f);
            }
            catch (IOException e) {
                throw new IllegalStateException("Issue writing to file: " + e.getMessage());
            }
        }
    }
}