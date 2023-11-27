package qgame.referee;

import java.util.ArrayList;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.observer.IGameObserver;
import qgame.observer.QGameObserver;
import qgame.state.Bag;
import qgame.state.QStateBuilder;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.util.TileUtil;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.ExtendsBoardRule;
import qgame.rule.placement.board.MatchTraitRule;
import qgame.rule.placement.move.ExtendSameLineRule;
import qgame.rule.placement.state.CorrectPlayerTilesRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.state.IGameState;

import qgame.util.RuleUtil;

import static qgame.util.ValidationUtil.validateArg;
/**
 * Represents the referee of the Q-Game, which acts as a turn-by-turn executor of the game
 * by prompting players for actions, updating the gameState to reflect said actions, and making
 * sure that the rules of the game are enforced throughout the game.
 */
public class QReferee implements IReferee {
  private final PlacementRule placementRules;
  private final ScoringRule scoringRules;

  // The amount of time given to Players to respond before they are kicked out, in milliseconds
  private final int timeOut;

  // timeout is in milliseconds
  private final int DEFAULT_TIMEOUT = 6000;

  private final int TOTAL_TILES = 300;
  private final int NUM_PLAYER_TILES = 6;

  private IGameState currentGameState;

  // list of Players in order
  private List<Player> players;

  private List<String> ruleBreakers;

  private List<IGameObserver> observers;


  private IGameState startState;

  public QReferee() {
    this.placementRules = RuleUtil.createPlaceRules();
    
    this.scoringRules = RuleUtil.createScoreRules();

    this.timeOut = DEFAULT_TIMEOUT;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = new ArrayList<>();
  }

  public QReferee(JsonElement refConfig) {

    JsonObject config = refConfig.getAsJsonObject();

    startState = JsonConverter.jStateToQGameState(config.get("state0"));

    boolean quiet = config.get("quiet").getAsBoolean();
    RefereeStateConfig rConfig = JsonConverter.parseRefereeStateConfig(config.get("config-s"));

    int perTurn = config.get("per-turn").getAsInt();
    validateArg(t -> t <= 6, perTurn, "per-turn must be less than or equal to 6");

    boolean observe = config.get("observe").getAsBoolean();
    this.observers = new ArrayList<>();

    if (observe) {
      this.observers.add(new QGameObserver());
    }


    // TODO: make timeOut in seconds? 
    this.timeOut = perTurn * 1000;

    this.placementRules = RuleUtil.createPlaceRules();
    this.scoringRules = RuleUtil.createScoreRules(rConfig.getqBonus(), rConfig.getfBonus());
  } 

  public QReferee(PlacementRule placementRules, ScoringRule scoringRules, int timeout) {
    this.placementRules = placementRules;
    this.scoringRules = scoringRules;
    validateArg(num -> num > 0, timeout, "Timeout must be positive.");
    this.timeOut = timeout;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = new ArrayList<>();
  }

  public QReferee(PlacementRule placementRules, ScoringRule scoringRules,
                  int timeout, List<IGameObserver> observers) {
    this.placementRules = placementRules;
    this.scoringRules = scoringRules;
    validateArg(num -> num > 0, timeout, "Timeout must be positive.");
    this.timeOut = timeout;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = observers;
  }


  @Override
  public GameResults playGame(IGameState state, List<Player> players) throws IllegalStateException {
    this.players = new ArrayList<>(players);
    this.currentGameState = state;
    
    setupPlayers();
    playGameRounds();
    notifyObserversOfGameOver();

    return getResults();
  }

  @Override
  public GameResults playGame(List<Player> players) {
    // System.out.println("Playing game");

    if (startState == null) {
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
   * Creates player infos for the given list of players at the start of a game
   * @param players
   * @param tileBag The Referee's Bag of tiles to 
   * @return
   */
  private List<PlayerInfo> getDefaultPlayerInfos(List<Player> players, Bag<Tile> tileBag) {

    List<PlayerInfo> infos = new ArrayList<>();

    for (Player p : players) {
      Collection<Tile> playerTiles = tileBag.removeFirstNItems(this.NUM_PLAYER_TILES);
      // tileBag.removeAll(playerTiles);
      PlayerInfo info = new PlayerInfo(0, playerTiles, p.name());
      infos.add(info);
    }

    return infos;
  }

  /**
   * Sets up all the players in the game by giving them the current map and the tiles
   * in their hand. Removes players if necessary.
   */
  private void setupPlayers() {

    List<Player> survivingPlayers = new ArrayList<>();

    while (!this.players.isEmpty()) {
      Player p = players.remove(0);
      boolean setupSucceeded = setup(p);
      if (!setupSucceeded) {
        this.removeCurrentPlayer();
      }
      else {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
      }
    }
    this.players.addAll(survivingPlayers);
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


  /**
   * Notifies the given group of Players in turn order if they've won or lost
   * @param category
   * @param hasWon
   * @return a list of the players who caused a problem on a win call
   */
  private List<String> notifyPlayersOfResult(List<Player> category, boolean hasWon) {

    List<Player> survivingPlayers = new ArrayList<>();
    List<String> ruleBreakersOnWin = new ArrayList<>();

    while (!this.players.isEmpty()) {
      Player p = players.remove(0);
      if (!category.contains(p)) {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
        continue;
      } 
      boolean callSuccessful = this.win(p, hasWon);
      if (!callSuccessful) {
        this.removeCurrentPlayer();
        ruleBreakersOnWin.add(p.name());
      }
      else {
        survivingPlayers.add(p);
        currentGameState.shiftCurrentToBack();
      }
    }
    players.addAll(survivingPlayers);
    return ruleBreakersOnWin;
  }

  // Determine all winners in a list of players and return a list of their names.
  private List<String> findWinnersAndNotifyPlayers(List<PlayerInfo> infos, int highestScore) {
    List<String> winners = new ArrayList<>();
    List<Player> winnerPlayers = new ArrayList<>();
    List<Player> loserPlayers = new ArrayList<>();

    for (int i = 0; i < infos.size(); i++) {
      Player player = players.get(i);
      if (infos.get(i).score() == highestScore) {
        winners.add(player.name());
        winnerPlayers.add(player);
      }
      else {
        loserPlayers.add(player);
      }
    }

    
    List<String> ruleBreakersOnWin = new ArrayList<>();
    ruleBreakersOnWin.addAll(notifyPlayersOfResult(winnerPlayers, true));
    ruleBreakersOnWin.addAll(notifyPlayersOfResult(loserPlayers, false));
    
    winners.removeAll(ruleBreakersOnWin);
    winners.sort(Comparator.naturalOrder());

    return winners;
  }



  //Returns the winners and rulebreakers of the game
  private GameResults getResults() {
    List<PlayerInfo> playerInfo = currentGameState.getAllPlayerInformation();
    int highestScore = maxScore(playerInfo);
    List<String> winners = findWinnersAndNotifyPlayers(playerInfo, highestScore);
    GameResults gr = new GameResults(winners, ruleBreakers);
    return gr;
  }

  /**
   * Until the game is over, keep going through each active player in the game, asking
   * for their action, and updating the game as necessary. Plays the game.
   */
  private void playGameRounds() {
    List<TurnAction> turnsTakenInRound = new ArrayList<>();
    boolean shouldGameContinue = true;
    while(!isGameOver(turnsTakenInRound) && shouldGameContinue) {
      turnsTakenInRound = new ArrayList<>();
      shouldGameContinue = playRound(turnsTakenInRound);
    }
    giveObserversStateUpdate();
  }

  /**
   * Gets the TurnAction from the player and validates the Action.
   * If the Turn is invalid, returns an empty Optional, otherwise return 
   * an Optional containing the TurnAction
   * @param currentPlayer
   * @return
   */
  private Optional<TurnAction> getAndValidateAction(Player currentPlayer) {

    Optional<TurnAction> ta = takeTurn(currentPlayer);
    if (ta.isEmpty() || !validTurn(ta.get())) {
      return Optional.empty();
    }
    return ta;
  }

  private void removeCurrentPlayer() {
    ruleBreakers.add(this.currentGameState.getCurrentPlayerInfo().name());
    this.currentGameState.removeCurrentPlayer();
  }

  // private void removePlayer(Player p) {
  //   String playerName = p.name();
  //   ruleBreakers.add(playerName);
  //   this.currentGameState.removePlayer(playerName);
  // }

  /**
   * Referee attempts to play a single round of the game. It goes through
   * each active player in the round, playing each player's turn. Removes players
   * if they have broken a rule or do not respond in time.
   * @return True if a player has placed all of their tiles
   * in a move, which indicates that the game should end.
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
      Optional<TurnAction> possibleAction = getAndValidateAction(currentPlayer);
      // possibleAction is empty if the turn is invalid
      if (possibleAction.isEmpty()) {
        removePlayer = true;
      }
      else {
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
        this.removeCurrentPlayer();
      }
      else {
        currentGameState.shiftCurrentToBack();
        nextRound.add(currentPlayer);
      }
    }

    players.addAll(nextRound);
    return gameContinue;
  }

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

  /**
   * Sends the current player a new set of tiles based on their turn action.
   * In the case of a PlaceAction, their entire set of tiles is given.
   * @param action
   * @param currentPlayer
   * @return True if currentPlayer has not been removed
   */
  private boolean givePlayerNewTiles(TurnAction action, Player currentPlayer) {

    // if (action instanceof PlaceAction) {  
    //   System.out.println("Player: " + currentPlayer.name() + " has move " + ((PlaceAction) action).placements().toString());
    // }

    return switch (action) {
      case PassAction ignored -> true;
      case ExchangeAction ignored -> newTiles(currentPlayer);
      case PlaceAction place -> newTiles(currentPlayer);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }

  /**
   * Represents a method call on a given Player with a given array of arguments
   */
  public interface PlayerLambda {
    public <T> T playerMethod(Player p, Object... args);
  }

  /**
   * Calls the given PlayerLambda on the given Player with the given array of arguments.
   * Checks that the player method returns within the timeout period.
   * @param lambda
   * @param player
   * @param args an array of arguments that represents the arguments needed for the
   * relevant player method
   * @throws TimeoutException if the player method fails to return in time
   * @throws ExecutionException if an Exception is thrown by the player method
   */
  private <T> T callPlayerMethodWithTimeout(PlayerLambda lambda, Player player, Object... args)
    throws ExecutionException, InterruptedException, TimeoutException {
    ThreadFactory factory = Thread.ofVirtual().factory();
    ExecutorService executor = Executors.newFixedThreadPool(1, factory);
    
    Future<T> getAction = executor.submit(
      () -> lambda.playerMethod(player, args));
    try {
      //TODO: Use timeout
      return getAction.get(this.timeOut, TimeUnit.MILLISECONDS);
      //return getAction.get();
    }
    finally {
      executor.shutdown();
    }
  }

  /**
   * Return an Optional containing the Player's turn as the value if
   * calling player.takeTurn() returned a turnAction successfully.
   * Otherwise return an empty Optional
   * @param player
   * @return
   */
  private Optional<TurnAction> takeTurn(Player player) {
    PlayerLambda l = new PlayerLambda() {
        public TurnAction playerMethod(Player p, Object... args) {
          return p.takeTurn((IPlayerGameState) args[0]);
        }
    };

    try {
      return Optional.of(callPlayerMethodWithTimeout(l, player, currentGameState.getCurrentPlayerState()));
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      // e.printStackTrace(System.out);
      return Optional.empty();
    }
  }

  /**
   * Calls the Setup method on the given player
   * @param player
   * @return false if they player fails to return before the timeout
   * or if the player throws an exception.
   */
  private boolean setup(Player player) {
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
    } 
    catch (ExecutionException | InterruptedException | TimeoutException e) {
      return false;
    }
  }


  /**
   * Updates the PLAYER about what their hand should look like now.
   * @param player player to update
   * @return true if successfully informed player
   */
  private boolean newTiles(Player player) {
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
    }
    catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      return false;
    }
  }

  private boolean win(Player player, boolean value) {
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
      return false;
    }
  }


  /**
   * Performs the player's action. 
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
   * @param n
   */
  private void setCurrentPlayerNTiles(int n) {
    Bag<Tile> newTiles = new Bag<>(currentGameState.takeOutRefTiles(n));
    currentGameState.setCurrentPlayerHand(newTiles);
  }



  /**
   * places tiles, updates the score of the player who placed them, updates the hand
   * and the ref tiles.
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
   * updates the internal game state's record of what the player's hand should be, removing
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
   * @param place
   * @return
   */
  private boolean isValidPlaceAction(PlaceAction place) {
    // System.out.println("Testing placement of player: " + state.getCurrentPlayer().name());
    boolean validPlacements = this.placementRules.isPlacementListLegal(
            place.placements(), currentGameState.getCurrentPlayerState());
    if (!validPlacements) {
      // System.out.println("Player: " + state.getCurrentPlayer().name() + " broke the rules");
    }
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
   * @param placements list of placements player made on this turn.
   */
  private void scorePlacements(List<Placement> placements) {
    int score = this.scoringRules.pointsFor(placements, currentGameState);
    currentGameState.addScoreToCurrentPlayer(score);
    // currentGameState.getBoard().printMap();
    // System.out.println("Player: " + currentGameState.getCurrentPlayerInfo().name() + " received: " + score + " points");
  }

  //Returns true when a list of placements has as many elements as the current player's hand.
  private boolean placedAllTiles(List<Placement> placements) {
    return getCurrentPlayerTiles().size() == placements.size();
  }

  /**
   * Checks if a game is over. A game is over if:
   * - There are no players left
   * - All actions in the given list of actions are not place actions
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

  // private Bag<Tile> getPlayerTiles(Player player) {
  //   return currentGameState.getPlayerInfo(player.name()).tiles();
  // }
}
