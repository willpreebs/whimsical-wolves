package qgame.state;

import java.util.*;
import java.util.function.Predicate;

import qgame.player.PlayerInfo;
import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.util.ValidationUtil;

import static qgame.util.ValidationUtil.nonNull;
import static qgame.util.ValidationUtil.validateState;

/**
 * Represents an implementation of the Game State interface that uses the 
 * rules known as "Basic Rules". The game state updates its board 
 * and other variables like player score and turn order as per request
 * from the referee's turn visitor.
 */
public class BasicQGameState implements QGameState {

  private QGameMap board;
  private final Bag<Tile> refereeTiles;
  private final List<PlayerInfo> playerInformation; // Players should always be kept in
  // sequential order

  /**
   * Constructs the initial game state at the start of the game
   * @param map, the initial Map of the board
   * @param tiles the list of tiles in a game
   * @param players list of who will play in the game
   */
  public BasicQGameState(QGameMap map, List<Tile> tiles, List<PlayerInfo> players)
    throws IllegalArgumentException {
    validate(map, tiles, players);
    this.board = new QGameMapImpl(map.getBoardState());
    this.refereeTiles = new Bag<>(tiles);
    this.playerInformation = new ArrayList<>(players);
  }

  /**
   * Determines whether all constructor parameters are valid before creating
   * a new gameState.
   * @param map the Map of all tiles on the board
   * @param tiles the referee's tiles
   * @param players the list of players represented through their information
   */
  private void validate(QGameMap map,  List<Tile> tiles,
                   List<PlayerInfo> players) {
    ValidationUtil.nonNullObj(map, "Map cannot be null");
    nonNull(tiles, "Tiles");
    nonNull(players, "Players");
  }


  private void validateGameHasPlayers() {
    validateState(Predicate.not(List::isEmpty), this.playerInformation,
      "There are no players in the game.");
  }

  @Override
  public List<PlayerInfo> playerInformation() {
    return new ArrayList<>(this.playerInformation);
  }

  @Override
  public QGameMap viewBoard() {
    return this.board;
  }

  @Override
  public Bag<Tile> refereeTiles() {
    return new Bag<>(this.refereeTiles.viewItems());
  }

  private List<Integer> allScores() {
    return new ArrayList<>(this.playerInformation
      .stream()
      .map(PlayerInfo::score)
      .toList());
  }
  @Override
  public PlayerGameState getCurrentPlayerState() {
    List<Integer> scores = allScores();
    QGameMap boardState = viewBoard();
    int tileCount = refereeTiles().size();
    Bag<Tile> playerTile = currentPlayer().tiles();
    return new BasicPlayerGameState(scores, boardState, tileCount, playerTile);
  }

  @Override
  public void shiftCurrentToBack() throws IllegalStateException {
    validateGameHasPlayers();
    this.playerInformation.add(this.playerInformation.remove(0));
  }

  @Override
  public void addScoreToCurrentPlayer(int score) throws IllegalStateException {
    validateGameHasPlayers();
    this.playerInformation.get(0).incrementScore(score);
  }

  @Override
  public void removeCurrentPlayer() {
    validateGameHasPlayers();
    this.playerInformation.remove(0);
  }

  @Override
  public void setCurrentPlayerHand(Bag<Tile> tiles) throws IllegalStateException {
    validateGameHasPlayers();
    this.playerInformation.get(0).setTiles(tiles);
  }

  @Override
  public void placeTile(Placement placement) {
    board.placeTile(placement);
  }

  @Override
  public Collection<Tile> takeOutRefTiles(int count) throws IllegalArgumentException {
    Collection<Tile> newTiles = this.refereeTiles.getItems(count);
    this.refereeTiles.remove(newTiles);
    return newTiles;
  }

  @Override
  public void giveRefereeTiles(Bag<Tile> tiles) throws IllegalArgumentException {
    this.refereeTiles.addAll(tiles);
  }


  /**
   * Returns the current player whose turn it is
   * @return PlayerInfo corresponding to current turn's player.
   */
  private PlayerInfo currentPlayer() {
    return this.playerInformation.get(0);
  }
}
