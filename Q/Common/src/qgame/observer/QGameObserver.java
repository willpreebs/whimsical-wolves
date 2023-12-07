package qgame.observer;

import static qgame.util.ValidationUtil.nonNullObj;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.google.gson.JsonElement;

import qgame.gui.ObserverView;
import qgame.json.ObjectToJson;
import qgame.state.IGameState;
import qgame.state.QGameState;

/**
 * A type of observer for Q-Game that allows for looking
 * at all states of a game and automatically saving any state
 * it receives as a PNG locally. It gets states by being
 * informed by the Referee of a QGame. This implementation
 * allows for saving a state as a JSON representation of its
 * information as well.
 * 
 * TODO: Guide for implementing GUI
 */
public class QGameObserver implements IGameObserver {

    List<IGameState> states;
    int stateIndex = -1;

    ObserverView stateFrame;
    Dimension dimension;

    boolean isGameOver = false;

    private final int REF_TILES = 6;
    private final String FILE_DIRECTORY = "../Tmp";
    private final String FILE_EXTENSION = "png";

    public QGameObserver() {
        this.states = new ArrayList<>();
        stateFrame = new ObserverView(this, new QGameState(), REF_TILES);
        stateFrame.setVisible(true);

        clearAllFiles();
    }

    /**
     * Removes all Files from the directory specified by FILE_DIRECTORY.
     */
    private void clearAllFiles() {

        File tmp = new File(FILE_DIRECTORY);
       try {
         for (File f : tmp.listFiles()) {
           f.delete();
         }
       }
       catch(NullPointerException e){
       }
    }

  /**
   * Gives the observer the game state and saves a GUI representation of the state
   * in an image. see saveStateAsPng()
   * @param state state to receive.
   */
    @Override
    public void receiveState(IGameState state) {
        nonNullObj(state, "State cannot be null");
        if (!isGameOver) {
            states.add(new QGameState(state));
            saveStateAsPng(states.size() - 1);
        }
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
            // System.out.println("Next pressed; index set to: " + stateIndex);
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
            // System.out.println("Previous pressed; index set to: " + stateIndex);
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
        // System.out.println("Rendering state at index: " + this.stateIndex);
        this.dimension = this.stateFrame.getSize();
        // this.stateFrame.getSize();
        stateFrame.updateFrame(this, this.getCurrentState());
        
        this.stateFrame.pack();
        this.stateFrame.setSize(this.dimension);
    }

    @Override
    public void save(String filepath) throws IllegalArgumentException{
        JsonElement jState = ObjectToJson.jStateFromQGameState(this.states.get(stateIndex));
        try {
            FileWriter w = new FileWriter(filepath);
            w.write(jState.toString());
            w.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot create or write to file: " + filepath);
        }
    }

  /**
   * Alerts the observer that the game is over
   * and that there will be no more states.
   */
    @Override
    public void gameOver() {
        isGameOver = true;
        this.stateFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Saves the frame at the given index as an image.
     * @param index
     */
    private void saveStateAsPng(int index) throws IllegalStateException{
        // System.out.println("save png");
        JFrame currentFrame = new ObserverView(this, this.states.get(index), REF_TILES);
        currentFrame.setVisible(true);
        BufferedImage img = new BufferedImage(currentFrame.getWidth(),
                currentFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        currentFrame.paintAll(g);
        File f = new File(FILE_DIRECTORY +"/" + index + "." + FILE_EXTENSION);
        try {
            ImageIO.write(img, FILE_EXTENSION, f);
            currentFrame.setVisible(false);
        }
        catch (Exception e) {
            throw new IllegalStateException("Issue writing to file: " + e.getMessage());
        }
    }

    public void saveStatesAsPng() {

        for (int i = 0; i < this.states.size(); i++) {
            saveStateAsPng(i);
        }
    }
}