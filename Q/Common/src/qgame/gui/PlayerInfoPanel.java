package qgame.gui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import qgame.player.PlayerInfo;


/**
 * Represents a panel containing information about a given player's tiles. Displays the tiles in
 * the players hand.
 */
public class PlayerInfoPanel extends JPanel {

  public PlayerInfoPanel(PlayerInfo info) {
    JScrollPane pane = new JScrollPane();
    int height = 95;
    int width = 75;
    int count = Math.min(info.tiles().size(), 3);
    pane.setPreferredSize(new Dimension(count * width + 10, height));
    pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JLabel text = new JLabel("Player's hand");
    JLabel tiles = new JLabel(new ImageIcon(ImageCreator.drawPlayerInfo(info)));
    this.setPreferredSize(new Dimension(count * width, height));
    this.add(text);
    pane.setViewportView(tiles);
    this.add(pane);
  }
}
