package qgame.gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import qgame.observer.IGameObserver;
import qgame.state.IGameState;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;

public class ObserverView extends JFrame {

  private int maxRefTilesToRender;
    
  public ObserverView(IGameObserver observer, IGameState state, int maxRefTilesToRender) {
    super("Observer view");
    this.maxRefTilesToRender = maxRefTilesToRender;


    // TODO: test dimensions, make fields for constants
    // Dimension fiveByFive = new Dimension(500, 500);
    // this.setMinimumSize(new Dimension(500, 500));
    updateFrame(observer, state);
  }

  public void updateFrame(IGameObserver observer, IGameState state) {
    this.getContentPane().removeAll();
    Box box = Box.createVerticalBox();
    Component map = createMapComponent(state);

    JPanel scoreAndButtons = new JPanel();
    scoreAndButtons.setLayout(new BoxLayout(scoreAndButtons, BoxLayout.X_AXIS));
    scoreAndButtons.add(new ScorePanel(state));
    scoreAndButtons.add(new ButtonPanel(observer));

    JPanel mapAndRefTiles = new JPanel();
    mapAndRefTiles.setLayout(new BoxLayout(mapAndRefTiles, BoxLayout.X_AXIS));
    mapAndRefTiles.add(map);
    mapAndRefTiles.add(new RefereeTiles(state, maxRefTilesToRender));
    
    box.add(scoreAndButtons);
    box.add(mapAndRefTiles);
    box.add(new JScrollPane(new PlayerTilesPanel(state)));

    // JScrollPane observerPane = new JScrollPane(box);

    //box.setPreferredSize(fiveByFive);
    this.add(box);
    //this.setSize(new Dimension(this.width, this.height));
    this.pack();
  }

  // public void updateFrame(IGameObserver observer, IGameState state, int height, )

  // TODO: own class ?
  private Component createMapComponent(IGameState state) {
    BufferedImage image = ImageCreator.drawBoard(state.getBoard());
    JLabel mapPanel = new JLabel(new ImageIcon(image));
    ScrollPane scrollImage = new ScrollPane();
    scrollImage.setPreferredSize(new Dimension(300,250));
    scrollImage.add(mapPanel);
    return scrollImage;
  }

  public void paintComponents(Graphics2D g) {
    Component [] components = this.getComponents();

    for (Component c : components) {
      c.paint(g);
    }
  }
}
