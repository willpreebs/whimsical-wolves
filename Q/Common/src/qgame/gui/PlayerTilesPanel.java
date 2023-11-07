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
        List<PlayerInfo> playerInfoList = state.playerInformation();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        generatePlayerTilesPanels(playerInfoList, panel);
        
        panel.setPreferredSize(new Dimension(500, 75));
        this.add(panel);
  }

  private void generatePlayerTilesPanels(List<PlayerInfo> playerInfoList, JPanel panel){
    JLabel panelLabel = new JLabel("Player Tiles");
    panel.add(panelLabel);
    for(int i = 0; i < playerInfoList.size(); i++){
        Bag<Tile> playerTiles = playerInfoList.get(i).tiles();
        JPanel tilePanel = new TilesPanel(playerTiles, playerTiles.size());
        panel.add(tilePanel);
    }
  }
}
