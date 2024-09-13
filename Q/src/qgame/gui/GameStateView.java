package qgame.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import qgame.player.PlayerInfo;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

/**
 * Represents a graphical view of a Q game state. Currently, displays the game board,
 * the tiles the first player owns, the remaining referee tiles, and the scores of all the
 * players in the game.
 */
public class GameStateView extends JFrame {
  public GameStateView(IPlayerGameState state) {
    super("Q Game State");
    Dimension fiveByFive = new Dimension(500, 500);
    this.setMinimumSize(new Dimension(500, 500));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Box box = Box.createVerticalBox();
    Component boardScroll = createScroll(state);
    box.add(boardScroll);
    addPlayerInfo(state, box);
    box.setPreferredSize(fiveByFive);
    this.add(box);
    this.pack();
    this.setVisible(true);
  }

  private Component createScroll(IPlayerGameState state) {
    BufferedImage image = ImageCreator.drawBoard(state.getBoard());
    JLabel mapPanel = new JLabel(new ImageIcon(image));
    ScrollPane scrollImage = new ScrollPane();
    scrollImage.setPreferredSize(new Dimension(300,250));
    scrollImage.add(mapPanel);
    return scrollImage;
  }

  private void addPlayerInfo(IPlayerGameState state, Box box) {
    List<Tile> tiles = new ArrayList<>(state.getCurrentPlayerTiles().getItems());
    if (!tiles.isEmpty()) {
      PlayerInfo info
        = new PlayerInfo(state.getPlayerScores().get(0), tiles, state.getPlayerName());
      box.add(new TilesPanel(info.getTiles(), info.getTiles().size()));
    }
    box.add(new HUDInfoPanel(state));
  }
}
