package qgame.referee;

import static qgame.util.ValidationUtil.validateArg;

import java.util.ArrayList;
import java.util.Collection;
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
import qgame.observer.IGameObserver;
import qgame.observer.QGameObserver;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.scoring.ScoringRule;
import qgame.server.DebugStream;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.QStateBuilder;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.RuleUtil;
import qgame.util.TileUtil;
/**
 * Represents the Referee of the Q game.
 * 
 * The Referee is responsible for running the entire game start
 * to finish, given a List of Players.
 * 
 * The Referee controls the QGameState which contains all of the information
 * about the current state of the game.
 */
public class QReferee implements IReferee {

  private final IPlacementRule placementRules;
  private final ScoringRule scoringRules;

  // The amount of time given to Players to respond before they are kicked out, in
  // milliseconds
  private final int timeOut;

  // timeout is in milliseconds
  private final int DEFAULT_TIMEOUT = 6000;

  private final int TOTAL_TILES = 1080;
  private final int NUM_PLAYER_TILES = 6;

  private IGameState currentGameState;

  // list of Players in order
  private List<Player> players;

  private List<String> ruleBreakers;

  private List<IGameObserver> observers;

  // May be set by the configuration file
  private IGameState startState;
  private boolean quiet = false;

  // For printing error logs
  private final DebugStream DEBUG_STREAM = DebugStream.ERROR;

  private boolean demoMode = false;

  /**
   * Default constructor, uses all default values.
   */
  public QReferee() {

    this.placementRules = RuleUtil.createPlaceRules();
    this.scoringRules = RuleUtil.createScoreRules();

    this.timeOut = DEFAULT_TIMEOUT;

    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = new ArrayList<>();
  }

  /**
   * Constructs a QReferee using values in the configuration object as the
   * state of its values.
   * see RefereeConfig
   * 
   * @param refConfig
   */
  public QReferee(RefereeConfig refConfig) {
    this.quiet = refConfig.isQuiet();
    this.observers = new ArrayList<>();

    if (refConfig.isObserve()) {
      this.observers.add(new QGameObserver());
    }

    this.timeOut = refConfig.getPerTurn() * 1000;
    RefereeStateConfig configS = refConfig.getConfigS();
    this.placementRules = RuleUtil.createPlaceRules();
    this.scoringRules = RuleUtil.createScoreRules(configS);
    this.startState = refConfig.getState();

    this.ruleBreakers = new ArrayList<>();
    this.players = new ArrayList<>();
  }

  public QReferee(IPlacementRule placementRules, ScoringRule scoringRules, int timeout) {
    this(placementRules, scoringRules, timeout, List.of());
  }

  /**
   * Allows custom rules to be set
   * 
   * @param placementRules
   * @param scoringRules
   * @param timeout
   * @param observers
   */
  public QReferee(IPlacementRule placementRules, ScoringRule scoringRules,
      int timeout, List<IGameObserver> observers) {
    this.placementRules = placementRules;
    this.scoringRules = scoringRules;
    validateArg(num -> num > 0, timeout, "Timeout must be positive.");
    this.timeOut = timeout;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = observers;
  }

  /**
   * For a Demo that plays an entire game, bypassing the startState.
   */
  @Override
  public void demoMode() {
    demoMode = true;
    log("Running in demo mode");
  }

  /**
   * Plays a Q Game with the list of Players from the given GameState.
   * @param state
   * @param players
   * @throws IllegalArgumentException if the number of players given does not match,
   * or the order of the players does not match the expected order.
   */
  @Override
  public GameResults playGame(IGameState state, List<Player> players) throws IllegalArgumentException {
    if(state.getAllPlayerInformation().size() != players.size()) {
      throw new IllegalArgumentException("Number of Players must match number of PlayerInfos in the GameState");
    }
    if (!doesPlayerOrderMatch(state.getAllPlayerInformation(), players)) {
      throw new IllegalArgumentException("Order of Players must match order of PlayerInfos in the GameState");
    }

    this.players = new ArrayList<>(players);
    
    this.currentGameState = state;

    setupPlayers();
    playGameRounds();
    notifyObserversOfGameOver();

    return getResults();
  }

  /**
   * Plays a game with a list of players. Assigns the player tiles
   * and places the first tile on the board.
   */
  @Override
  public GameResults playGame(List<Player> players) {

    if (startState == null || demoMode) {
      Bag<Tile> tileBag = TileUtil.getTileBag(TOTAL_TILES);

      List<PlayerInfo> playerInfos = getDefaultPlayerInfos(players, tileBag);

      startState = new QStateBuilder()
          .addTileBag(tileBag.getItems().toArray(new Tile[0]))
          .placeTile(new Posn(0, 0), tileBag.removeRandomItem())
          .addPlayerInfo(playerInfos.toArray(new PlayerInfo[0]))
          .build();
    }

    return playGame(startState, players);
  }

  public GameResults playGame(List<Player> players, int num_ref_tiles) {
    Bag<Tile> tileBag = TileUtil.getTileBag(num_ref_tiles);

    List<PlayerInfo> playerInfos = getDefaultPlayerInfos(players, tileBag);

    IGameState state = new QStateBuilder()
        .addTileBag(tileBag.getItems().toArray(new Tile[0]))
        .placeTile(new Posn(0, 0), tileBag.removeRandomItem())
        .addPlayerInfo(playerInfos.toArray(new PlayerInfo[0]))
        .build();

    return playGame(state, players);
  }

  /**
   * Creates player infos for the given list of players at the start of a game.
   * Pulls from the given bag of tiles to give to each player.
   * 
   * @param players
   * @param tileBag The Referee's Bag of tiles
   * @return
   */
  private List<PlayerInfo> getDefaultPlayerInfos(List<Player> players, Bag<Tile> tileBag) {

    List<PlayerInfo> infos = new ArrayList<>();

    for (Player p : players) {
      Collection<Tile> playerTiles = tileBag.removeFirstNItems(this.NUM_PLAYER_TILES);
      PlayerInfo info = new PlayerInfo(0, playerTiles, p.name());
      infos.add(info);
    }

    return infos;
  }

  /**
   * Sets up all the players in the game by giving them the current map and the
   * tiles in their hand. Removes players if necessary.
   */
  private void setupPlayers() {

    List<Player> survivingPlayers = new ArrayList<>();

    while (!this.players.isEmpty()) {

      Player p = players.remove(0);
      checkIfPlayerIsNext(p);

      boolean setupSucceeded = callSetup(p);
      
      if (!setupSucceeded) {
        this.removeCurrentPlayer();
        log("Remove player on setup: " + p.name());
      } else {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
      }
    }
    this.players.addAll(survivingPlayers);
  }

  /**
   * Gets the highest score among the player infos. Returns 0 when there are no players.
   */ 
  private int maxScore(List<PlayerInfo> playerInfos) {
    return playerInfos
        .stream()
        .mapToInt(PlayerInfo::getScore)
        .max()
        .orElse(0);
  }

  /**
   * Notifies the given group of Players in turn order if they've won or lost
   * 
   * @param category
   * @param hasWon
   * @return a list of the players who caused a problem on a win call
   */
  private List<Player> notifyPlayersOfResult(List<Player> category, boolean hasWon) {

    List<Player> survivingPlayers = new ArrayList<>();
    List<Player> ruleBreakersOnWin = new ArrayList<>();

    while (!this.players.isEmpty()) {
      Player p = players.remove(0);
      if (!category.contains(p)) {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
        continue;
      }
      boolean callSuccessful = this.callWin(p, hasWon);
      if (!callSuccessful) {
        this.removeCurrentPlayer();
        ruleBreakersOnWin.add(p);
      } else {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
      }
    }
    players.addAll(survivingPlayers);
    return ruleBreakersOnWin;
  }

  private void getWinnersAndLosers(List<PlayerInfo> infos, int highestScore, List<Player> winners,
      List<Player> losers) {
    for (int i = 0; i < infos.size(); i++) {
      Player player = players.get(i);
      if (infos.get(i).getScore() == highestScore) {
        winners.add(player);
      } else {
        losers.add(player);
      }
    }
  }

  // Determine all winners in a list of players and return a list of their names.
  private List<Player> findWinnersAndNotifyPlayers(List<PlayerInfo> infos, int highestScore) {

    List<Player> winnerPlayers = new ArrayList<>();
    List<Player> loserPlayers = new ArrayList<>();
    getWinnersAndLosers(infos, highestScore, winnerPlayers, loserPlayers);

    List<Player> ruleBreakersOnWin = new ArrayList<>();
    ruleBreakersOnWin.addAll(notifyPlayersOfResult(winnerPlayers, true));
    ruleBreakersOnWin.addAll(notifyPlayersOfResult(loserPlayers, false));

    winnerPlayers.removeAll(ruleBreakersOnWin);

    return winnerPlayers;
  }

  // Returns the winners and rulebreakers of the game
  private GameResults getResults() {
    List<PlayerInfo> playerInfo = this.currentGameState.getAllPlayerInformation();
    int highestScore = maxScore(playerInfo);

    List<Player> winners = findWinnersAndNotifyPlayers(playerInfo, highestScore);

    List<String> winnerNames = winners.stream()
        .map(p -> p.name())
        .sorted()
        .toList();

    GameResults gr = new GameResults(winnerNames, this.ruleBreakers);
    return gr;
  }

  /**
   * Until the game is over, keep going through each active player in the game,
   * asking
   * for their action, and updating the game as necessary. Plays the game.
   */
  private void playGameRounds() {
    List<TurnAction> turnsTakenInRound = new ArrayList<>();
    boolean shouldGameContinue = true;
    while (!isGameOver(turnsTakenInRound) && shouldGameContinue) {
      turnsTakenInRound = new ArrayList<>();
      shouldGameContinue = playRound(turnsTakenInRound);
    }
    giveObserversStateUpdate();
  }

  /**
   * Gets the TurnAction from the player and validates the Action.
   * If the Turn is invalid, returns an empty Optional, otherwise return
   * an Optional containing the TurnAction
   * 
   * @param currentPlayer
   * @return
   */
  private Optional<TurnAction> getAndValidateAction(Player currentPlayer) {

    Optional<TurnAction> ta = callTakeTurn(currentPlayer);
    if (ta.isEmpty() || !validTurn(ta.get())) {
      return Optional.empty();
    }
    return ta;
  }

  /**
   * Adds the current Player to the list of RuleBreakers and removes the
   * Player's information from the GameState.
   */
  private void removeCurrentPlayer() {
    ruleBreakers.add(this.currentGameState.getCurrentPlayerInfo().getName());
    this.currentGameState.removeCurrentPlayer();
  }

  /**
   * Referee attempts to play a single round of the game. It goes through
   * each active player in the round, playing each player's turn. Removes players
   * if they have broken a rule or do not respond in time.
   * 
   * @return True if a player has placed all of their tiles
   *         in a move, which indicates that the game should end.
   * 
   *         TODO: also return false if all the turns have been pass/exchange
   */
  private boolean playRound(List<TurnAction> turnsTaken) {
    List<Player> nextRound = new ArrayList<>();
    boolean gameContinue = true;

    while (!this.players.isEmpty() && gameContinue) {

      boolean removePlayer = false;

      // Step 1: Notify Observers
      giveObserversStateUpdate();

      // Step 2: Get current Player's Turn
      Player currentPlayer = players.remove(0);
      checkIfPlayerIsNext(currentPlayer);

      Optional<TurnAction> possibleAction = getAndValidateAction(currentPlayer);

      // possibleAction is empty if the turn is invalid
      if (possibleAction.isEmpty()) {
        removePlayer = true;
      } else {
        TurnAction action = possibleAction.get();
        turnsTaken.add(action);

        // Step 3: Perform player's turn and determine if the game should end
        gameContinue = handleAction(action);

        if (gameContinue) {
          // Step 4: Give player new tiles (returns false if method failed)
          removePlayer = !givePlayerNewTiles(action, currentPlayer);
        }
      }
      // Step 5: Prepare for next round
      // If current player broke the rules or caused an exception, remove them
      if (removePlayer) {
        log("Removing player " + currentPlayer.name());
        this.removeCurrentPlayer();
      } else {
        currentGameState.shiftCurrentToBack();
        nextRound.add(currentPlayer);
      }
    }

    players.addAll(nextRound);
    return gameContinue;
  }

  /**
   * Sends the current player a new set of tiles based on their turn action.
   * In the case of a PlaceAction, their entire set of tiles is given.
   * 
   * @param action
   * @param currentPlayer
   * @return True if currentPlayer has not been removed
   */
  private boolean givePlayerNewTiles(TurnAction action, Player currentPlayer) {
    return switch (action) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> callNewTiles(currentPlayer);
      case PlaceAction place -> callNewTiles(currentPlayer);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }

  /**
   * Represents a method call on a given Player with a given array of arguments
   */
  private interface PlayerLambda {
    public <T> T playerMethod(Player p, Object... args);
  }

  /**
   * Calls the given PlayerLambda on the given Player with the given array of
   * arguments.
   * Checks that the player method returns within the timeout period.
   * 
   * @param lambda
   * @param player
   * @param args   an array of arguments that represents the arguments needed for
   *               the
   *               relevant player method
   * @throws TimeoutException   if the player method fails to return in time
   * @throws ExecutionException if an Exception is thrown by the player method
   */
  private <T> T callPlayerMethodWithTimeout(PlayerLambda lambda, Player player, Object... args)
      throws ExecutionException, InterruptedException, TimeoutException {
    ThreadFactory factory = Thread.ofVirtual().factory();
    ExecutorService executor = Executors.newFixedThreadPool(1, factory);

    Future<T> getAction = executor.submit(() -> lambda.playerMethod(player, args));

    try {
      return getAction.get(this.timeOut, TimeUnit.MILLISECONDS);
    } finally {
      executor.shutdown();
    }
  }

  /**
   * Return an Optional containing the Player's turn as the value if
   * calling player.takeTurn() returned a turnAction successfully.
   * Otherwise return an empty Optional
   * 
   * @param player
   * @return
   */
  private Optional<TurnAction> callTakeTurn(Player player) {
    PlayerLambda l = new PlayerLambda() {
      public TurnAction playerMethod(Player p, Object... args) {
        return p.takeTurn((IPlayerGameState) args[0]);
      }
    };

    try {
      return Optional.of(callPlayerMethodWithTimeout(l, player, currentGameState.getCurrentPlayerState()));
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      log("Problem with calling takeTurn on " + player.name());
      return Optional.empty();
    }
  }

  /**
   * Calls the Setup method on the given player
   * 
   * @param player
   * @return false if they player fails to return before the timeout
   *         or if the player throws an exception.
   */
  private boolean callSetup(Player player) {
    PlayerLambda lambda = new PlayerLambda() {
      public <T> T playerMethod(Player p, Object... args) {
        IPlayerGameState state = (IPlayerGameState) args[0];
        Bag<Tile> tiles = (Bag<Tile>) args[1];
        p.setup(state, tiles);
        return null;
      }
    };

    try {
      IPlayerGameState state = currentGameState.getCurrentPlayerState();
      Bag<Tile> tiles = this.getCurrentPlayerTiles();
      callPlayerMethodWithTimeout(lambda, player, state, tiles);
      return true;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      log("Problem with calling setup on " + player.name());
      return false;
    }
  }

  /**
   * Updates the PLAYER about what their hand should look like now.
   * 
   * @param player player to update
   * @return true if successfully informed player
   */
  private boolean callNewTiles(Player player) {
    PlayerLambda l = new PlayerLambda() {
      public <T> T playerMethod(Player p, Object... args) {
        p.newTiles((Bag<Tile>) args[0]);
        return null;
      }
    };

    try {
      Bag<Tile> tiles = getCurrentPlayerTiles();
      callPlayerMethodWithTimeout(l, player, tiles);
      return true;
    } catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      log("Problem with calling newTiles on " + player.name());
      return false;
    }
  }

  private boolean callWin(Player player, boolean value) {
    PlayerLambda l = new PlayerLambda() {
      public <T> T playerMethod(Player p, Object... args) {
        p.win((Boolean) args[0]);
        return null;
      }
    };

    try {
      callPlayerMethodWithTimeout(l, player, value);
      return true;
    } catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      log("Problem with calling win on " + player.name());
      return false;
    }
  }

  /**
   * Performs the player's action.
   * 
   * @param action
   * @return Return false if the game should end as a result of this action
   */
  private boolean handleAction(TurnAction action) {

    // boolean shouldContinue;
    return switch (action) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> exchangeTiles();
      case PlaceAction place -> placeTiles(place);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }

  /**
   * Sets the current player's tiles to brand new tiles,
   * and append the current player's tiles onto the referee's tile bag
   * 
   * @return False if this action should result in the game ending
   */
  private boolean exchangeTiles() {
    Bag<Tile> currentPlayerHand = currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
    int playerTileSize = currentPlayerHand.size();
    currentGameState.giveRefereeTiles(currentPlayerHand);
    setCurrentPlayerNTiles(playerTileSize);
    return true;
  }

  /**
   * Gives the current player a number of tiles from the bag.
   * 
   * @param n
   */
  private void setCurrentPlayerNTiles(int n) {
    Bag<Tile> newTiles = new Bag<>(currentGameState.takeOutRefTiles(n));
    currentGameState.setCurrentPlayerHand(newTiles);
  }

  /**
   * places tiles, updates the score of the player who placed them, updates the
   * hand
   * and the ref tiles.
   * 
   * @return False if this action should result in the game ending
   */
  private boolean placeTiles(PlaceAction place) {
    List<Placement> placements = place.placements();
    boolean gameContinue = !placedAllTiles(placements);
    placeTiles(placements);
    scorePlacements(placements);
    if (gameContinue) {
      updateHandInState(placements);
    }
    return gameContinue;
  }

  private void placeTiles(List<Placement> placements) {
    placements.forEach(currentGameState::placeTile);
  }

  /**
   * updates the internal game state's record of what the player's hand should be,
   * removing
   * tiles they used in the turn and replenishing the hand from the referee's bag.
   */
  private void updateHandInState(List<Placement> placements) {
    List<Tile> tilesInPlacement = placements.stream().map(Placement::tile).toList();
    Bag<Tile> playerTiles = getCurrentPlayerTiles();
    playerTiles.removeAll(tilesInPlacement);
    int amountToRemove = Math.min(tilesInPlacement.size(), currentGameState.getRefereeTiles().size());
    addCurrentPlayerNTiles(playerTiles, amountToRemove);
  }

  private void addCurrentPlayerNTiles(Bag<Tile> existing, int n) {
    Collection<Tile> newTiles = currentGameState.takeOutRefTiles(n);
    existing.addAll(newTiles);
    currentGameState.setCurrentPlayerHand(existing);
  }

  private boolean isValidExchangeAction() {
    Bag<Tile> playerTiles = currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
    return playerTiles.size() <= currentGameState.getRefereeTiles().size();
  }

  /**
   * Returns true if the PlaceAction satisfies all the placement rules
   * 
   * @param place
   * @return
   */
  private boolean isValidPlaceAction(PlaceAction place) {
    boolean validPlacements = this.placementRules.isPlacementListLegal(
        place.placements(), currentGameState.getCurrentPlayerState());
    return validPlacements;
  }

  private boolean validTurn(TurnAction turnAction) {
    return switch (turnAction) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> isValidExchangeAction();
      case PlaceAction place -> isValidPlaceAction(place);
      default -> throw new IllegalStateException("Unexpected value: " + turnAction);
    };
  }

  /**
   * Uses the scoring rules to update the current player's score based
   * on their placements
   * 
   * @param placements list of placements player made on this turn.
   */
  private void scorePlacements(List<Placement> placements) {
    int score = this.scoringRules.pointsFor(placements, currentGameState);
    currentGameState.addScoreToCurrentPlayer(score);
    // currentGameState.getBoard().printMap();
    // System.out.println("Player: " +
    // currentGameState.getCurrentPlayerInfo().name() + " received: " + score + "
    // points");
  }

  // Returns true when a list of placements has as many elements as the current
  // player's hand.
  private boolean placedAllTiles(List<Placement> placements) {
    return getCurrentPlayerTiles().size() == placements.size();
  }

  /**
   * Checks if a game is over. A game is over if:
   * - There are no players left
   * - All actions in the given list of actions are not place actions
   * 
   * @param actions
   * @param players
   * @return
   */
  private boolean isGameOver(List<TurnAction> actions) {
    return players.isEmpty() || noPlacementsMade(actions);
  }

  private boolean noPlacementsMade(List<TurnAction> turns) {
    return !turns.isEmpty() && turns
        .stream()
        .noneMatch(turn -> turn instanceof PlaceAction);
  }

  private Bag<Tile> getCurrentPlayerTiles() {
    return currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
  }

  // GameState validators: 

  /**
   * Asserts that given Player is next to go according to the GameState
   * 
   * @param p
   */
  private void checkIfPlayerIsNext(Player p) {
    assert (this.currentGameState.isNextPlayerToGo(p.name()));
  }

  /**
   * Returns true if the given list of info matches the given list of Players
   * based on name.
   * @param infos
   * @param players
   * @return
   */
  private boolean doesPlayerOrderMatch(List<PlayerInfo> infos, List<Player> players) {

    for (int i = 0; i < players.size(); i++) {
      if (!players.get(i).name().equals(infos.get(i).getName())) {
        return false;
      }
    }
    return true;
  }




  private void log(Object message) {
    if (!quiet) {
      DEBUG_STREAM.s.println("QReferee: " + message);
    }
  }


  // Observers:

  private void giveObserversStateUpdate() {
    for (IGameObserver o : observers) {
      o.receiveState(currentGameState);
    }
  }

  private void notifyObserversOfGameOver() {
    for (IGameObserver o : observers) {
      o.gameOver();
    }
  }
}