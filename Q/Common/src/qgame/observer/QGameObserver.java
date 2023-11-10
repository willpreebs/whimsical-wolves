package qgame.observer;

import static qgame.util.ValidationUtil.nonNullObj;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.google.gson.JsonElement;

import qgame.gui.ObserverView;
import qgame.json.JsonConverter;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.QGameState;

public class QGameObserver implements IGameObserver {

    // List<IGameState> previous;
    // IGameState current;
    // IGameState next;

    // GameStates in order that they occur in the game
    List<IGameState> states;
    int stateIndex = -1;

    ObserverView stateFrame;

    private final int REF_TILES = 6;
    private final String FILE_DIRECTORY = "/8/Tmp";
    private final String FILE_EXTENSION = "png";

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

        //saveStateAsPng(states.size() - 1);
    }

    /**
     * Render the next state if available.
     * If there are no later states available, then the GUI is not changed.
     * When this is called for the first time, the state at index 0 is rendered.
     */
    @Override
    public void next() {
        if (stateIndex < this.states.size() - 1) {
            stateIndex++;
            System.out.println("Next pressed; index set to: " + stateIndex);
        }
        renderCurrentState();
    }

    /**
     * Render the previous state if available.
     * If there are no earlier states available, then the GUI is not changed
     */
    @Override
    public void previous() {
        // System.out.println("previous");
        if (stateIndex > 0) {
            stateIndex--;
            System.out.println("Previous pressed; index set to: " + stateIndex);
        }
        renderCurrentState();
    }

    protected IGameState getCurrentState() {
        return this.states.get(this.stateIndex);
    }

    protected List<IGameState> getStates() {
        return this.states;
    }

    protected String getFilePath(int index) {
        return FILE_DIRECTORY + index + FILE_EXTENSION;
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
            // throw new IllegalArgumentException("Cannot create or write to file: " + filepath);
        }
    }

  /**
   * Alerts the observer that the game is over
   * and that there will be no more states.
   */
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
        File f = new File(FILE_DIRECTORY +"/" + index + "." + FILE_EXTENSION);
        try {
            ImageIO.write(img, FILE_EXTENSION, f);
            currentFrame.setVisible(false);
        }
        catch (Exception e) {
            // throw new IllegalStateException("Issue writing to file: " + e.getMessage());
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