package qgame.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import qgame.player.PlayerInfo;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;


public class ObserverView extends JFrame {
    
    public ObserverView(IGameState state, int maxRefTilesToRender) {
        super("Observer view");



    // TODO: test dimensions, make fields for constants
    Dimension fiveByFive = new Dimension(500, 500);
    this.setMinimumSize(new Dimension(500, 500));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Box box = Box.createVerticalBox();
    Component map = createMapComponent(state);
    
    box.add(new ScorePanel(state));
    box.add(map);
    box.add(new RefereeTiles(state, maxRefTilesToRender));
    box.add(new JScrollPane(new PlayerTilesPanel(state)));

    // JScrollPane observerPane = new JScrollPane(box);

    //box.setPreferredSize(fiveByFive);
    this.add(box);
    this.pack();
    //this.setVisible(true);
  }

  // TODO: own class ?
  private Component createMapComponent(IGameState state) {
    BufferedImage image = ImageCreator.drawBoard(state.viewBoard());
    JLabel mapPanel = new JLabel(new ImageIcon(image));
    ScrollPane scrollImage = new ScrollPane();
    scrollImage.setPreferredSize(new Dimension(300,250));
    scrollImage.add(mapPanel);
    return scrollImage;
  }
}
