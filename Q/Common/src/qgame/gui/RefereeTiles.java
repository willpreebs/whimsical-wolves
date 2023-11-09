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
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        generateRefereeTilesPanel(state.getRefereeTiles(), max);
        // writeRemainingTiles(state,panel);
        //this.setPreferredSize(new Dimension(500, 75));
    }

  private void generateRefereeTilesPanel(Bag<Tile> refTiles, int max){
    JLabel tilesLabel = new JLabel("Referee Tiles");
    this.add(tilesLabel);
    this.add(new TilesPanel(refTiles, max));
  }
}
