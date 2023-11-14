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

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.observer.IGameObserver;
import qgame.state.Bag;
import qgame.state.QStateBuilder;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.util.TileUtil;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
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
  private final int timeOut;

  private final int DEFAULT_TIMEOUT = 100000;

  private final int TOTAL_TILES = 36;
  private final int NUM_PLAYER_TILES = 6;

  private IGameState currentGameState;

  // list of Players in order
  private List<Player> players;


  private List<String> ruleBreakers;

  private List<IGameObserver> observers;

  public QReferee() {
    this.placementRules = new MultiPlacementRule(new MatchTraitRule(),
      new ExtendSameLineRule(),
      new ExtendsBoardRule(), new CorrectPlayerTilesRule());
    
    this.scoringRules = RuleUtil.createScoreRules(NUM_PLAYER_TILES);

    this.timeOut = DEFAULT_TIMEOUT;
    this.players = new ArrayList<>();
    this.ruleBreakers = new ArrayList<>();
    this.observers = new ArrayList<>();
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

  public QReferee(PlacementRule placementRules, ScoringRule scoringRules, int timeout, List<IGameObserver> observers) {
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
    System.out.println("Playing game");
    Bag<Tile> tileBag = TileUtil.getTileBag(TOTAL_TILES);

    List<PlayerInfo> playerInfos = getDefaultPlayerInfos(players, tileBag);

    IGameState state = new QStateBuilder()
    .addTileBag(tileBag.getItems().toArray(new Tile[0]))
    .placeTile(new Posn(0, 0), tileBag.removeRandomItem())
    .addPlayerInfo(playerInfos.toArray(new PlayerInfo[0]))
    .build();

    return playGame(state, players);
  }

  private List<PlayerInfo> getDefaultPlayerInfos(List<Player> players, Bag<Tile> tileBag) {

    List<PlayerInfo> infos = new ArrayList<>();

    for (Player p : players) {
      PlayerInfo info = new PlayerInfo(0, tileBag.getItems(this.NUM_PLAYER_TILES), p);
      infos.add(info);
    }

    return infos;
  }

  /**
   * Sets up all the players in the game by giving them the current map and the tiles
   * in their hand.
   * @throws IllegalStateException if the list of players or game state is invalid.
   */
  private void setupPlayers() {
    // List<PlayerInfo> info = currentGameState.getAllPlayerInformation();
    // // validateState(l -> l.size() == info.size(),
    // //   players, "Players does not match playerInfo");
    for (int i = 0; i < players.size(); i++) {
      boolean removePlayer = setup(players.get(i));
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


  /**
   * Notifies the given group of Players in turn order if they've won or lost
   * @param category
   * @param hasWon
   */
  private void notifyPlayers(List<Player> category, boolean hasWon) {

    for (Player p : this.players) {
      if (category.contains(p)) {
        this.win(p, hasWon);
      }
    }
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

    notifyPlayers(winnerPlayers, true);
    notifyPlayers(loserPlayers, false);

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
  private void playGameRounds(){
    List<TurnAction> turnsTakenInRound = new ArrayList<>();
    boolean shouldGameContinue = true;
    while(!isGameOver(turnsTakenInRound, players) && shouldGameContinue) {
      turnsTakenInRound = new ArrayList<>();
      shouldGameContinue = playRound(turnsTakenInRound);
    }
    giveObserversStateUpdate();
  }

  /**
   * Gets the TurnAction from the player and validates the Action.
   * If the Turn is invalid, returns an empty Optional, otherwise return 
   * an Optional containing the TurnAction
   * @param state
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
    ruleBreakers.add(this.currentGameState.getCurrentPlayer().name());
    this.currentGameState.removeCurrentPlayer();
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

    // A Round of gameplay
    while (!players.isEmpty() && gameContinue) {
      // One player's Turn


      boolean removePlayer = false;

      // Step 1: Notify Observers
      giveObserversStateUpdate();

      // Step 2: Get current Player's Turn
      Player currentPlayer = players.remove(0);
      Optional<TurnAction> possibleAction = getAndValidateAction(currentPlayer);
      if (possibleAction.isEmpty()) {
        removePlayer = true;
      }
      else {
        TurnAction action = possibleAction.get();
        turnsTaken.add(action);

        // Step 3: Perform player's turn and determine if the game should end
        gameContinue = handleAction(action);
        
        // Step 4: Give player new tiles (returns false if method failed) 
        removePlayer = !givePlayerNewTiles(action, currentPlayer);
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
   * lambda
   *  
   */
  public interface TimeoutLambda {
    
    public <T> T playerMethod(Player p, IPlayerGameState state, boolean win);
    
  }

  private <T> T callPlayerMethodWithTimeout(TimeoutLambda l, Player player, IGameState state, boolean win)
    throws ExecutionException, InterruptedException, TimeoutException {

    ThreadFactory factory = Thread.ofVirtual().factory();
    ExecutorService executor = Executors.newFixedThreadPool(1, factory);
    IPlayerGameState finalState = state.getCurrentPlayerState();
    Future<T> getAction = executor.submit(
      () -> l.playerMethod(player, finalState, win));
    return getAction.get(this.timeOut, TimeUnit.MILLISECONDS);
    // return player.takeTurn(state.getCurrentPlayerState());
  }

  // TODO: Detect infinite loop etc.
  /**
   * Return an Optional containing the Player's turn as the value if
   * calling player.takeTurn() returned a turnAction successfully.
   * Otherwise return an 
   * @param player
   * @return
   */
  private Optional<TurnAction> takeTurn(Player player) {
    TimeoutLambda l = new TimeoutLambda() {
        public TurnAction playerMethod(Player p, IPlayerGameState state, boolean win) {
          return p.takeTurn(state);
        }
    };
    try {
      return Optional.of(callPlayerMethodWithTimeout(l, player, currentGameState, false));
      // return Optional.of(player.takeTurn(this.currentGameState.getPlayerState(player)));
    } catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      // removeCurrentPlayer();
      return Optional.empty();
    }
  }

  private boolean setup(Player player) {
    TimeoutLambda l = new TimeoutLambda() {
        public <T> T playerMethod(Player p, IPlayerGameState state, boolean win) {
          p.setup(state, state.getCurrentPlayerTiles());
          return null;
        }
    };

    try {
      callPlayerMethodWithTimeout(l, player, currentGameState, false);
      return true;
    } catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      // removeCurrentPlayer();
      return false;
    }
  }


  /**
   * Updates the PLAYER about what their hand should look like now.
   * @param player player to update
   * @return true if successfully informed player
   */
  private boolean newTiles(Player player) {
    TimeoutLambda l = new TimeoutLambda() {
        public <T> T playerMethod(Player p, IPlayerGameState state, boolean win) {
          p.newTiles(currentGameState.getPlayerInformation(player).tiles());
          return null;
        }
    };

    try {
      callPlayerMethodWithTimeout(l, player, currentGameState, false);
      return true;
    }
    catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      // removeCurrentPlayer();
      return false;
    }
  }

  private boolean win(Player player, boolean value) {
    TimeoutLambda l = new TimeoutLambda() {
        public <T> T playerMethod(Player p, IPlayerGameState state, boolean win) {
          p.win(win);
          return null;
        }
    };
    try {
      callPlayerMethodWithTimeout(l, player, currentGameState, value);
      return true;
    } catch (IllegalStateException | ExecutionException | InterruptedException | TimeoutException e) {
      // TODO: handle exception on win
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
   * Returns true if the PlaceAction satisfies all of the placement rules
   * @param place
   * @param state
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
    IMap board = currentGameState.getBoard();
    int score = this.scoringRules.pointsFor(placements, board);
    // System.out.println("Giving score: " + score + " to player: " + this.currentGameState.getCurrentPlayer().name());
    // int bonus = placedAllTiles(placements) ? ALL_TILE_BONUS : 0;
    // currentGameState.addScoreToCurrentPlayer(score + bonus);
    currentGameState.addScoreToCurrentPlayer(score);
  }

  //Returns true when a list of placements has as many elements as the current player's hand.
  private boolean placedAllTiles(List<Placement> placements) {
    return getCurrentPlayerTiles().size() == placements.size();
  }

  // Checks if a game is over, which is when there is only one or less players, or when all turns
  // played in a round are not placement actions.
  private boolean isGameOver(List<TurnAction> actions, List<Player> players) {
    return players.size() <= 1 || noPlacementsMade(actions);
  }

  private boolean noPlacementsMade(List<TurnAction> turns) {
    return !turns.isEmpty() && turns
            .stream()
            .noneMatch(turn -> turn instanceof PlaceAction);
  }

  private Bag<Tile> getCurrentPlayerTiles() {
    return currentGameState.getCurrentPlayerState().getCurrentPlayerTiles();
  }
}
