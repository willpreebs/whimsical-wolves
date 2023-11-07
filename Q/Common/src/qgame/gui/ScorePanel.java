package qgame.gui;

import java.awt.Dimension;
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
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    generateScorePanel(playerInfoList, panel);
    // writeRemainingTiles(state,panel);
    panel.setPreferredSize(new Dimension(500, 75));
    this.add(panel);
  }

  private void generateScorePanel(List<PlayerInfo> playerInfoList, JPanel panel){
    JLabel score = new JLabel("Player Scores");
    panel.add(score);
    for(int i = 0; i<playerInfoList.size(); i++){
      int pScore = playerInfoList.get(i).score();
      String name = playerInfoList.get(i).name();
      JLabel pScoreLabel = new JLabel(name + ": " + pScore);
      panel.add(pScoreLabel);
    }
  }

//   private void writeRemainingTiles(IPlayerGameState state, JPanel panel){
//     JLabel remainingTiles = new JLabel("Remaining Tiles: " + state.remainingTiles());
//     panel.add(remainingTiles);
//   }

}
