package qgame.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;

import qgame.state.Bag;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.player.PlayerInfo;
import qgame.state.Placement;

/**
 * Represents a class that produces images of Q game components.
 */
public class ImageCreator {

  private static final int IMG_SIZE = 75;

  //top right bottom left
  private static int[][] diamondPoints(int size) {
    return new int[][]{{size / 2, size - 1, size / 2, 0}, {0, size / 2, size - 1, size / 2}};
  }

  private static int[][] starPoints(int size, int squareSize) {
    int spaceBeforeSquare = (size - squareSize) / 2;
    int[][] squareCoord = {{spaceBeforeSquare, spaceBeforeSquare + squareSize,
      spaceBeforeSquare + squareSize, spaceBeforeSquare}, {spaceBeforeSquare, spaceBeforeSquare,
      spaceBeforeSquare + squareSize, spaceBeforeSquare + squareSize}};
    return new int[][]{
      {squareCoord[0][0], size / 2, squareCoord[0][1], size - 1, squareCoord[0][2], size / 2,
        squareCoord[0][3], 0},
      {squareCoord[1][0], 0, squareCoord[1][1], size / 2, squareCoord[1][2], size - 1,
        squareCoord[1][3], size / 2}
    };
  }

  private static int[][] circleCoords(int width) {
    int miniCircleWidth = width / 4;
    return new int[][]{
      {miniCircleWidth * 2, miniCircleWidth, miniCircleWidth * 2, miniCircleWidth * 3},
      {miniCircleWidth, miniCircleWidth * 2, miniCircleWidth * 3, miniCircleWidth * 2}};
  }
  private static int[][] applyTransform(int[][] posns, double[][] matrix, int[] shift) {
    int[][] newPosn = new int[posns.length][posns[0].length];
    for(int i = 0; i < posns[0].length; i++) {
      int x = posns[0][i];
      int y = posns[1][i];
      newPosn[0][i] = (int)Math.round(matrix[0][0] * x + matrix[0][1] * y) + shift[0];
      newPosn[1][i] = (int)Math.round(matrix[1][0] * x + matrix[1][1] * y) + shift[1];
    }
    return newPosn;
  }

  private static int[][] shiftMatrix(int[][] posns, int x, int y) {
    return applyTransform(posns, new double[][]{{1, 0}, {0, 1}}, new int[]{x, y});
  }

  private static int[][] rotateCoords(int[][] posns, int size, double theta) {
    theta = Math.toRadians(theta);
    double[][] rotateMatrix = {{Math.cos(theta), -Math.sin(theta)}, {Math.sin(theta),
      Math.cos(theta)}};
    int[][] shifted = shiftMatrix(posns, -size / 2, -size / 2);
    int[][] rotated = applyTransform(shifted, rotateMatrix, new int[]{0, 0});
    return shiftMatrix(rotated, size / 2, size / 2);
  }

  private static void setColor(Tile.Color color, Graphics2D graphics) {
    switch (color) {
      case RED -> graphics.setColor(java.awt.Color.RED);
      case ORANGE -> graphics.setColor(java.awt.Color.ORANGE);
      case YELLOW -> graphics.setColor(java.awt.Color.YELLOW);
      case GREEN -> graphics.setColor(java.awt.Color.GREEN);
      case BLUE -> graphics.setColor(java.awt.Color.BLUE);
      case PURPLE -> graphics.setColor(new java.awt.Color(102, 0, 153));
      default -> throw new IllegalArgumentException("Color not supported");
    }
  }
  private static BufferedImage drawTile(Tile tile) {
    int width = IMG_SIZE;
    int height = IMG_SIZE;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = image.createGraphics();
    setColor(tile.color(), graphics);
    drawShape(tile,graphics,width,height);
    return image;
  }
  private static BufferedImage drawPlacement(Placement placement) {
    Posn posn = placement.posn();
    Tile tile = placement.tile();
    BufferedImage image = drawTile(tile);
    Graphics2D graphics = image.createGraphics();
    int width = image.getWidth();
    int height = image.getHeight();
    drawPosn(posn, graphics, width, height);
    return image;
  }

  private static void drawStar(Graphics2D graphics, int width) {
    int starSize = width * 4 / 3;
    int[][] ogPoints = starPoints(starSize, starSize / 5);
    int[][] rotated = rotateCoords(ogPoints, starSize, 45);
    int[][] starPoints = shiftMatrix(rotated, -(starSize - width) / 2,
      -(starSize - width) / 2);

    graphics.fillPolygon(starPoints[0], starPoints[1], 8);
  }

  private static void drawEightStar(Graphics2D graphics, int width){
    int[][] starPoints = starPoints(width, width / 5);
    graphics.fillPolygon(starPoints[0], starPoints[1], 8);
    int[][] rotatedPoints = rotateCoords(starPoints, width, 45);
    graphics.fillPolygon(rotatedPoints[0], rotatedPoints[1], 8);
  }

  private static void drawDiamond(Graphics2D graphics, int width) {
    int[][] diamondPoints = diamondPoints(width);
    graphics.fillPolygon(diamondPoints[0], diamondPoints[1], 4);
  }

  private static void drawClover(Graphics2D graphics, int width) {
    int miniCircleDiameter = width / 2;
    int[][] circleCoords = circleCoords(width);
    for (int i = 0; i < 4; i++) {
      int radius = miniCircleDiameter / 2;
      int y = circleCoords[0][i] - radius;
      int x = circleCoords[1][i] - radius;
      graphics.fillOval(x, y, miniCircleDiameter, miniCircleDiameter);
    }
  }

  private static void drawPosn(Posn posn, Graphics2D graphics, int width, int height) {
    graphics.setColor(Color.BLACK);
    String posnString = String.format("(%d, %d)", posn.y(), posn.x());
    Font font = new Font("Arial", Font.PLAIN, 15);
    FontMetrics metrics = graphics.getFontMetrics(font);
    int textWidth = metrics.stringWidth(posnString);
    int textHeight = metrics.getHeight();
    int x = (width - textWidth) / 2 + 3;
    int y = (height - textHeight) / 2 + metrics.getAscent();
    graphics.drawString(posnString, x, y);
  }
  private static void drawShape(Tile tile, Graphics2D graphics, int width, int height){
    switch (tile.shape()) {
      case STAR -> drawStar(graphics, width);
      case CIRCLE -> graphics.fillOval(0, 0, width, height);
      case DIAMOND -> drawDiamond(graphics, width);
      case SQUARE -> graphics.fillRect(0, 0, width, height);
      case EIGHT_STAR -> drawEightStar(graphics, width);
      case CLOVER -> drawClover(graphics,width);
    }
  }

  // public static BufferedImage drawPlayerInfo(PlayerInfo info) {
  //   Bag<Tile> tiles = info.tiles();

  //   BufferedImage result =
  //     new BufferedImage(tiles.size() * IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
  //   Graphics2D graphics = result.createGraphics();
  //   graphics.setColor(new Color(255, 216, 179));
  //   graphics.fillRect(0, 0, result.getWidth(), result.getHeight());

  //   List<BufferedImage> images = tiles.viewItems().stream().map(ImageCreator::drawTile).toList();
  //   for (int i = 0; i < images.size(); i++) {
  //     graphics.drawImage(images.get(i), IMG_SIZE * i, 0, null);
  //   }
  //   return result;
  // }

  public static BufferedImage drawTiles(Bag<Tile> refTiles, int maxTiles) {
    
    if (refTiles.size() == 0) {
      return new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
    }

    int numTiles = Math.min(refTiles.size(), maxTiles);

    BufferedImage result =
      new BufferedImage(numTiles * IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = result.createGraphics();
    graphics.setColor(new Color(255, 216, 179));
    graphics.fillRect(0, 0, result.getWidth(), result.getHeight());

    List<BufferedImage> images = refTiles.getItems(numTiles).stream().map(ImageCreator::drawTile).toList();
    for (int i = 0; i < images.size(); i++) {
      graphics.drawImage(images.get(i), IMG_SIZE * i, 0, null);
    }
    return result;
  }

  public static BufferedImage drawBoard(IMap map) {

    //map.printMap();
    Set<Posn> posns = map.getBoardState().keySet();
    int topRow = posns.stream().map(Posn::y).reduce(Integer.MAX_VALUE, Integer::min);
    int bottomRow = posns.stream().map(Posn::y).reduce(Integer.MIN_VALUE, Integer::max);
    int leftCol = posns.stream().map(Posn::x).reduce(Integer.MAX_VALUE, Integer::min);
    int rightCol = posns.stream().map(Posn::x).reduce(Integer.MIN_VALUE, Integer::max);

    int width = rightCol - leftCol + 1;
    int height = bottomRow - topRow + 1;

    BufferedImage image = new BufferedImage(width * IMG_SIZE, height * IMG_SIZE,
      BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(new Color(255, 216, 179));
    graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

    for (Map.Entry<Posn, Tile> entry : map.getBoardState().entrySet()) {
      Placement p = new Placement(entry.getKey(), entry.getValue());
      BufferedImage placementImage = drawPlacement(p);
      int y = (p.posn().y() - topRow) * IMG_SIZE;
      int x = (p.posn().x() - leftCol) * IMG_SIZE;
      graphics.drawImage(placementImage, x, y, null);
    }
    return image;
  }



}
