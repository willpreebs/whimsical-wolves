package qgame.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

import qgame.state.PlayerGameState;

/**
 * Represents the JPanel that contains factual info
 * of the game including the score of each player, and
 * (eventually) the remaining tiles and the
 */
public class HUDInfoPanel extends JPanel {
  public HUDInfoPanel(PlayerGameState state){
    List<Integer> playerInfoList = state.playerScores();
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    generatePlayerScores(playerInfoList,panel);
    writeRemainingTiles(state,panel);
    panel.setPreferredSize(new Dimension(500, 75));
    this.add(panel);
  }

  private void generatePlayerScores(List<Integer> playerInfoList, JPanel panel){
    JLabel score = new JLabel("Score");
    panel.add(score);
    for(int i = 0; i<playerInfoList.size(); i++){
      int pScore = playerInfoList.get(0);
      //change to accommodate player ID?
      JLabel pScoreLabel = new JLabel("Player " + i + ": " + pScore);
      panel.add(pScoreLabel);
    }
  }

  private void writeRemainingTiles(PlayerGameState state, JPanel panel){
    JLabel remainingTiles = new JLabel("Remaining Tiles: " + state.remainingTiles());
    panel.add(remainingTiles);
  }


}
