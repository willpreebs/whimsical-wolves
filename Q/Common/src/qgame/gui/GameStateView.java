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
import qgame.state.PlayerGameState;
import qgame.state.map.Tile;

/**
 * Represents a graphical view of a Q game state. Currently, displays the game board,
 * the tiles the first player owns, the remaining referee tiles, and the scores of all the
 * players in the game.
 */
public class GameStateView extends JFrame {
  public GameStateView(PlayerGameState state) {
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

  private Component createScroll(PlayerGameState state) {
    BufferedImage image = ImageCreator.drawBoard(state.viewBoard());
    JLabel mapPanel = new JLabel(new ImageIcon(image));
    ScrollPane scrollImage = new ScrollPane();
    scrollImage.setPreferredSize(new Dimension(300,250));
    scrollImage.add(mapPanel);
    return scrollImage;
  }

  private void addPlayerInfo(PlayerGameState state, Box box) {
    List<Tile> tiles = new ArrayList<>(state.getCurrentPlayerTiles().viewItems());
    if (!tiles.isEmpty()) {
      PlayerInfo info
        = new PlayerInfo(state.playerScores().get(0), tiles);
      box.add(new PlayerInfoPanel(info));
    }
    box.add(new HUDInfoPanel(state));
  }
}
