package qgame.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import qgame.observer.IGameObserver;
import qgame.player.PlayerInfo;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;

public class ScorePanel extends JPanel {
    
    public ScorePanel(IGameState state) {
    List<PlayerInfo> playerInfoList = state.playerInformation();
    
    this.setLayout(new GridBagLayout());
    addScorePanel(playerInfoList);
    //this.setPreferredSize(new Dimension(500, 100));
    this.setMaximumSize(new Dimension(500, 500));
    this.setMinimumSize(new Dimension(500, 300));
    // writeRemainingTiles(state,panel);
    //panel.setPreferredSize(new Dimension(1000, 1000));
  }

  private void addScorePanel(List<PlayerInfo> playerInfoList){

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    JLabel score = new JLabel("Player Scores");
    this.add(score, c);

    for(int i = 0; i<playerInfoList.size(); i++){
        c.gridy = i + 1;

      int pScore = playerInfoList.get(i).score();
      String name = playerInfoList.get(i).name();
      JLabel pScoreLabel = new JLabel(name + ": " + pScore);

      this.add(pScoreLabel, c);
    }
  }

//   private void writeRemainingTiles(IPlayerGameState state, JPanel panel){
//     JLabel remainingTiles = new JLabel("Remaining Tiles: " + state.remainingTiles());
//     panel.add(remainingTiles);
//   }

}
