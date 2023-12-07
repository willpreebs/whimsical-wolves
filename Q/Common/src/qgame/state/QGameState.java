package qgame.state;

import static qgame.util.ValidationUtil.nonNull;
import static qgame.util.ValidationUtil.validateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import qgame.player.PlayerInfo;
import qgame.state.map.QMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.util.ValidationUtil;

/**
 * The QGameState is responsible for keeping track of 
 * a state of the game.
 * 
 * It contains the following data:
 * 
 * - QMap board: Contains all of the Tiles placed on the board
 * - Bag<Tile> refereeTiles: Contains the Tiles not owned by any player,
 * aka the tiles 'in the bag' not yet in play. The Bag is assumed to 
 * be shuffled.
 * - List<PlayerInfo> playerInformation: All of the information of the 
 * players currently in the game, in order of their turns. The order of 
 * this list changes after each move. See PlayerInfo
 * 
 * The 'current' Player refers to the first PlayerInfo in this.playerInformation.
 * In the context of the Q game, this is the information of the next player to go. 
 * 
 * this.playerInformation is guaranteed by the Referee to match the order of the Players 
 * contained in the Referee
 */
public class QGameState implements IGameState {

  private QMap board;
  private Bag<Tile> refereeTiles;
  private List<PlayerInfo> playerInformation;

  /**
   * Constructs the initial game state at the start of the game
   * @param map, the initial Map of the board
   * @param tiles the list of tiles in a game
   * @param players list of who will play in the game
   */
  public QGameState(QMap map, Bag<Tile> tiles, List<PlayerInfo> players)
    throws IllegalArgumentException {
    validate(map, tiles, players);
    this.board = new QMap(map.getBoardState());
    this.refereeTiles = new Bag<>(tiles);
    this.playerInformation = new ArrayList<>(players);
  }

  public QGameState(IGameState state) {
    this(new QMap(state.getBoard().getBoardState()), state.getRefereeTiles(), state.getAllPlayerInformation());
  }

  public QGameState() {
    this(new QMap(new HashMap<>()), new Bag<>(), new ArrayList<>());
  }

  public QGameState(QMap map) {
    this(map, new Bag<>(), new ArrayList<>());
  }

  /**
   * Determines whether all constructor parameters are valid before creating
   * a new gameState.
   * @param map the Map of all tiles on the board
   * @param tiles the referee's tiles
   * @param players the list of players represented through their information
   */
  private void validate(QMap map,  Bag<Tile> tiles,
                   List<PlayerInfo> players) {
    ValidationUtil.nonNullObj(map, "Map cannot be null");
    nonNull(List.of(tiles.getItems()), "Tiles");
    nonNull(players, "Players");
  }


  private void validateGameHasPlayers() {
    validateState(Predicate.not(List::isEmpty), this.playerInformation,
      "There are no players in the game.");
  }

  @Override
  public List<PlayerInfo> getAllPlayerInformation() {
    List<PlayerInfo> copy = new ArrayList<>();

    for (PlayerInfo i : this.playerInformation) {
      copy.add(new PlayerInfo(i.getScore(), new Bag<>(i.getTiles()).getItems(), i.getName()));
    }

    return copy;
  }

  /**
   * Returns the current player whose turn it is
   * @return PlayerInfo corresponding to current turn's player.
   */
  @Override
  public PlayerInfo getCurrentPlayerInfo() {
    return this.playerInformation.get(0);
  }

  @Override
  public QMap getBoard() {
    return this.board;
  }

  @Override
  public Bag<Tile> getRefereeTiles() {
    return new Bag<>(this.refereeTiles.getItems());
  }

  /**
   * Returns a list of Scores in the current order of the players
   * @return
   */
  private List<Integer> allScores() {
    return new ArrayList<>(this.playerInformation
      .stream()
      .map(PlayerInfo::getScore)
      .toList());
  }

  /**
   * Returns a IPlayerGameState containing all of the data
   * that is public knowledge and the current Player's private knowledge.
   */
  @Override
  public IPlayerGameState getCurrentPlayerState() {
    List<Integer> scores = allScores();
    QMap boardState = getBoard();
    int tileCount = getRefereeTiles().size();
    Bag<Tile> playerTile = getCurrentPlayerInfo().getTiles();
    String playerName = getCurrentPlayerInfo().getName();
    return new QPlayerGameState(scores, boardState, tileCount, playerTile, playerName);
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

  /**
   * Removes the current PlayerInfo from the GameState and adds 
   * the Player's tiles to the Bag of tiles. 
   */
  @Override
  public void removeCurrentPlayer() {
    validateGameHasPlayers();
    PlayerInfo removed = this.playerInformation.remove(0);
    Bag<Tile> removedPlayerTiles = removed.getTiles();
    this.refereeTiles.addAll(removedPlayerTiles);
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
    Collection<Tile> newTiles = this.refereeTiles.removeFirstNItems(count);
    return newTiles;
  }

  @Override
  public void giveRefereeTiles(Bag<Tile> tiles) throws IllegalArgumentException {
    this.refereeTiles.addAll(tiles);
  }

  @Override
  public boolean isNextPlayerToGo(String name) {
    return this.getCurrentPlayerInfo().getName().equals(name);
  }

}
