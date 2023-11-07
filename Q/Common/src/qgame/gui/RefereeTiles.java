package qgame.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import qgame.player.PlayerInfo;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.map.Tile;

public class RefereeTiles extends JPanel {
    
    public RefereeTiles(IGameState state, int max) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        generateRefereeTilesPanel(state.refereeTiles(), panel, max);
        // writeRemainingTiles(state,panel);
        panel.setPreferredSize(new Dimension(500, 75));
        this.add(panel);
    }

  private void generateRefereeTilesPanel(Bag<Tile> refTiles, JPanel panel, int max){
    JLabel score = new JLabel("Referee Tiles");
    panel.add(score);
    panel.add(new TilesPanel(refTiles, max));
  }
}
