package qgame.referee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.BasicQGameState;
import qgame.state.BasicQGameStateBuilder;
import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.util.TileUtil;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.Placement;
import qgame.state.PlayerGameState;
import qgame.state.QGameState;

import static qgame.util.ValidationUtil.validateArg;
import static qgame.util.ValidationUtil.validateState;
/**
 * Represents the referee of the Q-Game, which acts as a turn-by-turn executor of the game
 * by prompting players for actions, updating the gameState to reflect said actions, and making
 * sure that the rules of the game are enforced throughout the game.
 */
public class BasicQGameReferee implements QGameReferee {
  private final PlacementRule placementRules;
  private final ScoringRule scoringRules;
  private final int timeOut;
  private final int ALL_TILE_BONUS;

  private final int TOTAL_TILES = 1080;
  private final int NUM_PLAYER_TILES = 6;

  private QGameState currentGameState;
  private List<Player> players;
  private List<String> ruleBreakers;

  public BasicQGameReferee(PlacementRule placementRules, ScoringRule scoringRules, int timeout,
                           int bonus) {
    this.placementRules = placementRules;
    this.scoringRules = scoringRules;
    validateArg(num -> num > 0, timeout, "Timeout must be positive.");
    this.timeOut = timeout;
    this.ALL_TILE_BONUS = bonus;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
  }

  @Override
  public GameResults playGame(QGameState state, List<Player> players) throws IllegalStateException {
    this.players = new ArrayList<>(players);
    this.currentGameState = state;
    setupPlayers();
    playGameRounds();
    return getResults();
  }

  @Override
  public GameResults playGame(List<Player> players) {

    Bag<Tile> tileBag = TileUtil.getTileBag(TOTAL_TILES);

    List<PlayerInfo> playerInfos = getDefaultPlayerInfos(players, tileBag);

    QGameState state = new BasicQGameStateBuilder()
    .addTileBag(tileBag.viewItems().toArray(new Tile[0]))
    .placeTile(new Posn(0, 0), tileBag.removeRandomItem())
    .addPlayerInfo(playerInfos.toArray(new PlayerInfo[0]))
    .build();

    return playGame(state, players);
  }

  private List<PlayerInfo> getDefaultPlayerInfos(List<Player> players, Bag<Tile> tileBag) {

    List<PlayerInfo> infos = new ArrayList<>();

    for (Player p : players) {
      PlayerInfo info = new PlayerInfo(0, tileBag.getItems(this.NUM_PLAYER_TILES));
      infos.add(info);
    }

    return infos;
  }

  private void handlePlayerException(Player player) {
      ruleBreakers.add(player.name());
      this.currentGameState.removeCurrentPlayer();
  }


  private void setupPlayer(Player player, Bag<Tile> tiles, QGameMap board) {

    try {
      player.setup(board, tiles);
    } catch (IllegalStateException e) {
      handlePlayerException(player);
    }
  }
  /**
   * Sets up all the players in the game by giving them the current map and the tiles
   * in their hand.
   * @throws IllegalStateException if the list of players or game state is invalid.
   */
  private void setupPlayers() {
    List<PlayerInfo> info = currentGameState.playerInformation();
    // validateState(l -> l.size() == info.size(),
    //   players, "Players does not match playerInfo");

    for (int i = 0; i < players.size(); i++) {
      setupPlayer(players.get(i), info.get(i).tiles(), currentGameState.viewBoard());
    }
  }

  // Highest score among the player infos. Returns 0 when there are no players.
  private int maxScore(List<PlayerInfo> playerInfos) {
    return
      playerInfos
      .stream()
      .mapToInt(PlayerInfo::score)
      .max()
      .orElse(0);
  }

  // Determine all winners in a list of players and return a list of their names.
  private List<String> findWinners(List<Player> players, List<PlayerInfo> infos, int highestScore) {
    List<String> winners = new ArrayList<>();
    for (int i = 0; i < infos.size(); i++) {
      Player player = players.get(i);
      if (infos.get(i).score() == highestScore) {
        winners.add(player.name());
        win(player, true);
      }
      else {
        win(player, false);
      }
    }
    winners.sort(Comparator.naturalOrder());
    return winners;
  }

  private void win(Player player, boolean value) {
    try {
      player.win(value);
    } catch (IllegalStateException ignored) {
    }
  }

  //Determines the winners is a game state
  private GameResults getResults() {
    List<PlayerInfo> playerInfo = currentGameState.playerInformation();
    int highestScore = maxScore(playerInfo);
    List<String> winners = findWinners(players, playerInfo, highestScore);
    return new GameResults(winners, ruleBreakers);
  }

  /**
   * Until the game is over, keep going through each active player in the game, asking
   * for their action, and updating the game as necessary. Plays the game.
   */
  private void playGameRounds(){
    List<TurnAction> turnsTakenInRound = new ArrayList<>();
    boolean shouldGameContinue = true;
    while(!isGameOver(turnsTakenInRound, players) && shouldGameContinue) {
      turnsTakenInRound = new ArrayList<>();
      shouldGameContinue = playRound(turnsTakenInRound);
    }

  }

  private Optional<TurnAction> getAndValidateAction(QGameState state, Player currentPlayer) {
    TurnAction action;
    try {
      action = getAction(state, currentPlayer);
    } catch (IllegalStateException | TimeoutException | InterruptedException |
             ExecutionException e) {
      return Optional.empty();
    }
    if (!validTurn(action, state)) {
      return Optional.empty();
    }
    return Optional.of(action);
  }
  /**
   * Referee attempts to play a single round of the game. It goes through
   * each active player in the round, playing each player's turn.
   *                     for potential punishment.
   * @return True if the game should continue, false if it should end.
   */
  private boolean playRound(List<TurnAction> turnsTaken) {
    List<Player> nextRound = new ArrayList<>();
    boolean gameContinue = true;
    while (!players.isEmpty() && gameContinue) {
      Player currentPlayer = players.remove(0);
      Optional<TurnAction> possibleAction = getAndValidateAction(currentGameState, currentPlayer);
      if (possibleAction.isEmpty()) {
        ruleBreakers.add(currentPlayer.name());
        this.currentGameState.removeCurrentPlayer();
        continue;
      }
      TurnAction action = possibleAction.get();
      turnsTaken.add(action);
      gameContinue = handleAction(action);
      boolean informResult = informPlayerOfActionResult(action, currentPlayer);
      if (informResult) {
        currentGameState.shiftCurrentToBack();
        nextRound.add(currentPlayer);
      }
    }
    players.addAll(nextRound);
    return gameContinue;
  }

  private boolean informPlayerOfActionResult(TurnAction action, Player currentPlayer) {
    return switch (action) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> newTiles(currentPlayer);
      case PlaceAction place -> newTiles(currentPlayer);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }
  private TurnAction getAction(QGameState state, Player player)
    throws ExecutionException, InterruptedException, TimeoutException {
    ThreadFactory factory = Thread.ofVirtual().factory();
    ExecutorService executor = Executors.newFixedThreadPool(1, factory);
    PlayerGameState finalState = state.getCurrentPlayerState();
    Future<TurnAction> getAction = executor.submit(
      () -> player.takeTurn(finalState));
    return getAction.get(this.timeOut, TimeUnit.MILLISECONDS);
  }


  private boolean newTiles(Player player) {
    try {
      player.newTiles(currentGameState.getCurrentPlayerState().getCurrentPlayerTiles());
      return true;
    }
    catch (IllegalStateException e) {
      ruleBreakers.add(player.name());
      currentGameState.removeCurrentPlayer();
      return false;
    }
  }
  /**
   * Performs action on a game state. Returns false if the game should not continue after the action
   * Does NOT move the current player to the end of the turn rotation.
   */
  private boolean handleAction(TurnAction action) {
    boolean shouldContinue;
    switch (action) {
      case PassAction ignored -> { return true;}
      case ExchangeAction ignored -> shouldContinue = exchangeTiles();
      case PlaceAction place -> shouldContinue = handlePlacement(place);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    }
    return shouldContinue;
  }

  /**
   * Performs the exchange action if the referee has enough tiles to replace
   * the current player's hand
   * @return new QGameState with updated information reflecting this action.
   * replace the player's hand.
   */
  private boolean exchangeTiles() {
    Bag<Tile> currentPlayerHand = currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
    int playerTileSize = currentPlayerHand.size();
    currentGameState.giveRefereeTiles(currentPlayerHand);
    setCurrentPlayerNTiles(currentGameState, playerTileSize);
    return true;
  }

  private void setCurrentPlayerNTiles(QGameState state, int n) {
    Bag<Tile> newTiles = new Bag<>(state.takeOutRefTiles(n));
    state.setCurrentPlayerHand(newTiles);
  }

  private void addCurrentPlayerNTiles(Bag<Tile> existing, int n) {
    Collection<Tile> newTiles = currentGameState.takeOutRefTiles(n);
    existing.addAll(newTiles);
    currentGameState.setCurrentPlayerHand(existing);
  }

  /**
   * places tiles, updates the score of the player who placed them, updates the hand
   * and the ref tiles.
   */
  private boolean handlePlacement(PlaceAction place) {
    List<Placement> placements = place.placements();
    boolean gameContinue = !placedAllTiles(placements);
    placeTiles(placements);
    scorePlacements(placements);
    if (gameContinue) {
      updateHand(placements);
    }
    return gameContinue;
  }

  private void placeTiles(List<Placement> placements) {
    placements.forEach(currentGameState::placeTile);
  }

  private void updateHand(List<Placement> placements) {
    List<Tile> tilesRemoved = placements.stream().map(Placement::tile).toList();
    Bag<Tile> playerTiles = getPlayerTiles();
    playerTiles.remove(tilesRemoved);
    int amountToRemove = Math.min(tilesRemoved.size(), currentGameState.refereeTiles().size());
    addCurrentPlayerNTiles(playerTiles, amountToRemove);
  }


  private boolean canExchange(QGameState state) {
    Bag<Tile> playerTiles = state.getCurrentPlayerState().getCurrentPlayerTiles();
    return playerTiles.size() <= state.refereeTiles().size();
  }

  private boolean canPlace(PlaceAction place, QGameState state) {
    return this.placementRules.validPlacements(place.placements(), state.getCurrentPlayerState());
  }

  private boolean validTurn(TurnAction turnAction, QGameState state) {
    return switch (turnAction) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> canExchange(state);
      case PlaceAction place -> canPlace(place, state);
      default -> throw new IllegalStateException("Unexpected value: " + turnAction);
    };
  }

  /**
   * Uses the scoring rules to update the current player's score based
   * on their placements
   * @param placements list of placements player made on this turn.
   */
  private void scorePlacements(List<Placement> placements) {
    QGameMap board = currentGameState.viewBoard();
    int score = this.scoringRules.pointsFor(placements, board);
    int bonus = placedAllTiles(placements) ? ALL_TILE_BONUS : 0;
    currentGameState.addScoreToCurrentPlayer(score + bonus);
  }

  private boolean noPlacementsMade(List<TurnAction> turns) {
    return !turns.isEmpty() && turns
      .stream()
      .noneMatch(turn -> turn instanceof PlaceAction);
  }

  //Returrns true when a list of placements has as many elements as the current player's hand.
  private boolean placedAllTiles(List<Placement> placements) {
    return getPlayerTiles().size() == placements.size();
  }

  // Checks if a game is over, which is when there is only one or less players, or when all turns
  // played in a round are not placement actions.
  private boolean isGameOver(List<TurnAction> actions, List<Player> players) {
    return players.size() <= 1 || noPlacementsMade(actions);
  }

  private Bag<Tile> getPlayerTiles() {
    return currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
  }
}
