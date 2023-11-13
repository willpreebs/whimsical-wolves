package qgame.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import qgame.player.PlayerInfo;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.map.Tile;

public class PlayerTilesPanel extends JPanel {
    
    public PlayerTilesPanel(IGameState state) {
        List<PlayerInfo> playerInfoList = state.getAllPlayerInformation();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        generatePlayerTilesPanels(playerInfoList);
        
        this.setPreferredSize(new Dimension(500, 400));
  }

  private void generatePlayerTilesPanels(List<PlayerInfo> playerInfoList){
    JLabel panelLabel = new JLabel("Player Tiles");
    this.add(panelLabel);
    for(int i = 0; i < playerInfoList.size(); i++){
        Bag<Tile> playerTiles = playerInfoList.get(i).tiles();
        JPanel tilePanel = new TilesPanel(playerTiles, playerTiles.size());
        JLabel playerLabel = new JLabel(playerInfoList.get(i).name() + ":");
        this.add(playerLabel);
        this.add(tilePanel);
    }
  }
}
