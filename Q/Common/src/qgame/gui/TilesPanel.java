package qgame.gui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import qgame.player.PlayerInfo;
import qgame.state.Bag;
import qgame.state.map.Tile;


/**
 * Represents a panel containing information about a given player's tiles. Displays the tiles in
 * the players hand.
 */
public class TilesPanel extends JPanel {

  public TilesPanel(Bag<Tile> tiles, int max) {
    JScrollPane pane = new JScrollPane();
    int height = 95;
    int width = 75;
    int minWidth = Math.min(tiles.size(), 3) * width;
    pane.setPreferredSize(new Dimension(minWidth + 10, height));
    pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JLabel tileLabel = new JLabel(new ImageIcon(ImageCreator.drawTiles(tiles, max)));
    this.setPreferredSize(new Dimension(minWidth, height));
    pane.setViewportView(tileLabel);
    this.add(pane);
  }
}
