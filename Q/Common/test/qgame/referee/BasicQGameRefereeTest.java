package qgame.referee;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.TestUtil;
import qgame.player.CheatingAIPlayer;
import qgame.player.LoopingAIPlayer;
import qgame.player.SimpleAIPlayer;
import qgame.state.Bag;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.util.RuleUtil;
import qgame.state.map.QTile;
import qgame.player.AlwaysExchangePlayer;
import qgame.player.AlwaysPassPlayer;
import qgame.player.MockPlayer;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.player.DummyAIPlayer;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.ExtendsBoardRule;
import qgame.rule.placement.board.MatchTraitRule;
import qgame.rule.placement.move.ExtendSameLineRule;
import qgame.rule.placement.state.CorrectPlayerTilesRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.QGameState;
import qgame.state.QStateBuilder;
import qgame.state.IGameState;
import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;

import static org.junit.Assert.*;

public class BasicQGameRefereeTest {

  Tile.Color red = Tile.Color.RED;
  Tile.Color blue = Tile.Color.BLUE;
  Tile.Color green = Tile.Color.GREEN;
  Tile.Color yellow = Tile.Color.YELLOW;
  Tile.Color orange = Tile.Color.ORANGE;
  Tile.Color purple = Tile.Color.PURPLE;

  Tile.Shape square = Tile.Shape.SQUARE;
  Tile.Shape circle = Tile.Shape.CIRCLE;
  Tile.Shape diamond = Tile.Shape.DIAMOND;
  Tile.Shape clover = Tile.Shape.CLOVER;
  Tile.Shape star = Tile.Shape.STAR;
  Tile.Shape eightStar = Tile.Shape.EIGHT_STAR;

  PlacementRule placementRules;
  Player player1;
  Player player2;
  Player badMove;
  Player disconnectPlayer;
  Player timeOutPlayer;

  Player cheatingPlayer1;

  Player loopPlayer;

  IGameState stateForceFirstPass;
  IGameState allPass;
  IReferee ref;

  IGameState onePassOneExchange1;
  IGameState onePassOneExchange2;
  IGameState placeAll;

  IGameState cheatState;
  IGameState loopState;

  @Before
  public void init() {
    placementRules = new MultiPlacementRule(new MatchTraitRule(),
      new ExtendSameLineRule(),
      new ExtendsBoardRule(), new CorrectPlayerTilesRule());
    ScoringRule scoringRules =  RuleUtil.createOldScoreRules();

    player1 = new DummyAIPlayer("Tester", new DagStrategy(placementRules));
    player2 = new DummyAIPlayer("SecondTester", new LdasgStrategy(placementRules));
    badMove = new DummyAIPlayer("Bad player", new BadTurnStrategy());
    disconnectPlayer = new DisconnectPlayer("Disconnect");
    timeOutPlayer = new TimeOutPlayer("Bobby", 1000);
    ref = new QReferee(placementRules, scoringRules, 900);
  }

  private void init1() {
    Map<Posn, Tile> tileMap = new HashMap<>();
    tileMap.put(new Posn(0, 1), new QTile(red, square));
    IMap map = new QMap(tileMap);


    List<PlayerInfo> info = new ArrayList<>();
    info.add(new PlayerInfo(0, List.of(new QTile(orange, star)), ""));
    info.add(new PlayerInfo(0, List.of(new QTile(orange, square)), ""));
    stateForceFirstPass = new QGameState(map, new Bag<>(), info);
  }

  @Before
  public void initAllPass(){
    Map<Posn, Tile> tileMap = new HashMap<>();
    tileMap.put(new Posn(0, 1), new QTile(red, square));
    IMap map = new QMap(tileMap);
    List<PlayerInfo> info = new ArrayList<>();
    info.add(new PlayerInfo(0, List.of(new QTile(orange, star)), "Tester"));
    info.add(new PlayerInfo(0, List.of(new QTile(orange, circle)), "SecondTester"));
    allPass = new QGameState(map, new Bag<>(), info);
  }

  @Test
  public void testBadMovePlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, badMove));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.getWinners().size());
    assertEquals(player1.name(), results.getWinners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(badMove.name());
    assertEquals(breakers.size(), results.getRuleBreakers().size());
    assertEquals(badMove.name(), results.getRuleBreakers().get(0));
  }

  @Test
  public void testDisconnectPlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, disconnectPlayer));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.getWinners().size());
    assertEquals(player1.name(), results.getWinners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(disconnectPlayer.name());
    assertEquals(breakers.size(), results.getRuleBreakers().size());
    assertEquals(disconnectPlayer.name(), results.getRuleBreakers().get(0));
  }

  @Test
  public void testTimeOutPlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, timeOutPlayer));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.getWinners().size());
    assertEquals(player1.name(), results.getWinners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(timeOutPlayer.name());
    assertEquals(breakers.size(), results.getRuleBreakers().size());
    assertEquals(timeOutPlayer.name(), results.getRuleBreakers().get(0));
  }

  @Test
  public void testGameEndsWithAllPass() {
    initAllPass();
    GameResults result = ref.playGame(allPass,List.of(player1,player2));
    List<String> winners = new ArrayList<>(List.of(player1.name(), player2.name()));
    assertEquals(winners.size(), result.getWinners().size());
    List<String> ruleBreakers = new ArrayList<>();
    assertEquals(ruleBreakers.size(), result.getRuleBreakers().size());
  }


  @Before
  public void initOnePassOneExchange1() {
    onePassOneExchange1 =
      new QStateBuilder()
        .addTileBag(new QTile(blue, eightStar))
        .placeTile(new Posn(0, 0), new QTile(red, clover))
        .addPlayerInfo(new PlayerInfo(1, List.of(new QTile(green, square)), ""))
        .addPlayerInfo(new PlayerInfo(5, List.of(new QTile(yellow, star)), ""))
        .build();
  }

  @Before
  public void initOnePassOneExchange2() {
    onePassOneExchange2 =
      new QStateBuilder()
        .addTileBag(new QTile(blue, eightStar))
        .placeTile(new Posn(0, 0), new QTile(red, clover))
        .addPlayerInfo(new PlayerInfo(5, List.of(new QTile(red, star)), ""))
        .addPlayerInfo(new PlayerInfo(1, List.of(new QTile(green, square)), ""))
        .build();
  }

  @Test
  public void testGameEndsWithOnePassOneExchange1() {
    MockPlayer passer = new AlwaysPassPlayer(List.of(new QTile(blue,square)));
    List<Player> players = new ArrayList<>(List.of(passer, player1));
    GameResults results = ref.playGame(onePassOneExchange1, players);
    assertEquals(1, results.getWinners().size());
    assertEquals("Tester", results.getWinners().get(0));
//    assertEquals(List.of(new TileImpl(blue,square)), passer.returnHand());
  }

  @Test
  public void testGameEndsWithOnePassOneExchange2() {
    MockPlayer passer = new AlwaysPassPlayer(List.of(new QTile(blue,square)));
    MockPlayer exchanger = new AlwaysExchangePlayer(new Bag<>(List.of(new QTile(blue,square))));
    List<Player> players = new ArrayList<>(List.of(exchanger, passer));
    GameResults results = ref.playGame(onePassOneExchange2, players);
    assertEquals(1, results.getWinners().size());
    assertEquals("Exchanger", results.getWinners().get(0));
//    assertEquals(List.of(new TileImpl(blue,square)), passer.returnHand());
//    assertEquals(List.of(new TileImpl(blue,eightStar)), exchanger.returnHand());
  }

  @Before
  public void initEndAfterPlaceAll(){
    placeAll = new QStateBuilder()
      .addTileBag(new QTile(blue, eightStar))
      .placeTile(new Posn(0, 0), new QTile(red, clover))
      .addPlayerInfo(new PlayerInfo(0, List.of(new QTile(red, eightStar),
        new QTile(green, eightStar)), "Tester"))
      .addPlayerInfo(new PlayerInfo(0, List.of(new QTile(green, square),
        new QTile(green, eightStar), new QTile(green, eightStar),
        new QTile(green, eightStar), new QTile(green, eightStar),
        new QTile(green, eightStar), new QTile(green, eightStar)), "SecondTester"))
      .build();
  }

  @Test
  public void testGameEndsAfterPlaceHand() {
    List<Player> players = new ArrayList<>(List.of(player1, player2));
    GameResults results = ref.playGame(placeAll, players);
    assertEquals(1, results.getWinners().size());
    assertEquals("Tester", results.getWinners().get(0));
    try {
      assertTrue(XGamesInputCreator.createHarnessTest(placeAll,
              players, results, "7/Tests", 0));
    }
    catch (IOException e) {
    }
  }

  @Test
  public void testPlayersFailToSetup() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(0, 0), new QTile(red, diamond))
      .addTileBag(new QTile(orange, square), new QTile(green, star), new QTile(red,
        square))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(purple, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(orange, clover), new QTile(green, star)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(yellow, diamond), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(red, clover), new QTile(green, circle)), ""))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("bobby", new DagStrategy(placementRules)),
      new DummyAIPlayer("terry", new LdasgStrategy(placementRules), FailStep.NEW_TILES),
      new DummyAIPlayer("ben", new DagStrategy(placementRules)),
    new DummyAIPlayer("alex", new LdasgStrategy(placementRules), FailStep.SETUP));
    GameResults results = new GameResults(List.of("ben"), List.of("alex", "terry"));
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 1);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testAllPlayersFailToSetup() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(0, 0), new QTile(red, diamond))
      .addTileBag(new QTile(orange, square), new QTile(green, star), new QTile(red,
        square))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(purple, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(orange, clover), new QTile(green, star)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(yellow, diamond), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(red, clover), new QTile(green, circle)), ""))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("bobby", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("terry", new LdasgStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("ben", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("alex", new LdasgStrategy(placementRules), FailStep.SETUP));
    GameResults results = new GameResults(new ArrayList<>(),
      List.of("bobby", "terry", "ben", "alex"));
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 2);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void test3WinnersSortedProperly() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(1, 0), new QTile(red, diamond))
      .addTileBag(new QTile(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(purple, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(orange, clover), new QTile(green, star)), ""))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(yellow, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(blue, clover), new QTile(green, circle)), ""))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("ben3", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben1", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben4", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben2", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("ben1", "ben3", "ben4"),
      new ArrayList<>());
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 3);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testTakeTurnRemovesProperly() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(1, 0), new QTile(red, diamond))
      .addTileBag(new QTile(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(purple, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(orange, star), new QTile(green, star)), ""))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new QTile(blue, clover), new QTile(green, circle)), ""))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("appleeeee", new DagStrategy(placementRules)),
      new DummyAIPlayer("Appleeeee", new DagStrategy(placementRules)),
      new DummyAIPlayer("LOSER", new DagStrategy(placementRules), FailStep.TAKE_TURN));
    GameResults results = new GameResults(List.of("Appleeeee", "appleeeee"),
      List.of("LOSER"));
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 4);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testWinnerStillInResults() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(1, 0), new QTile(red, diamond))
      .addTileBag(new QTile(green, square))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new QTile(red, eightStar), new QTile(green, circle)), ""))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(orange, eightStar), new QTile(green, star)), ""))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new QTile(blue, clover), new QTile(green, circle)), ""))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("appleeeee", new DagStrategy(placementRules), FailStep.WIN),
      new DummyAIPlayer("Appleeeee", new DagStrategy(placementRules), FailStep.WIN),
      new DummyAIPlayer("NotALoser", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("Appleeeee", "appleeeee"),
      new ArrayList<>());
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 5);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testSameColorPlaceAllTilesInSecondRound() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(1, 0), new QTile(red, square))
      .addTileBag(new QTile(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(red, eightStar), new QTile(green, circle)), "tess"))
      .addPlayerInfo(new PlayerInfo(12,
        List.of(new QTile(orange, eightStar), new QTile(green, star)), "tessy"))
      .addPlayerInfo(new PlayerInfo(15,
        List.of(new QTile(blue, clover), new QTile(green, circle)), "3Peat"))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("tess", new DagStrategy(placementRules)),
      new DummyAIPlayer("tessy", new DagStrategy(placementRules)),
      new DummyAIPlayer("3Peat", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("tess", "tessy"),
      new ArrayList<>());
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 6);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void testAllRemovedVarious() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(1, 0), new QTile(red, square))
      .addTileBag(new QTile(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new QTile(red, eightStar), new QTile(green, circle)), "tess"))
      .addPlayerInfo(new PlayerInfo(12,
        List.of(new QTile(orange, eightStar), new QTile(green, star)), "tessy"))
      .addPlayerInfo(new PlayerInfo(15,
        List.of(new QTile(blue, square), new QTile(green, circle)), "3Peat"))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("tess", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("tessy", new DagStrategy(placementRules), FailStep.TAKE_TURN),
      new DummyAIPlayer("3Peat", new DagStrategy(placementRules), FailStep.NEW_TILES));
    GameResults results = new GameResults(new ArrayList<>(),
      List.of("tess", "tessy", "3Peat"));
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 7);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void testLockoutGame() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(-3, 0), new QTile(green, eightStar))
      .addTileBag(new QTile(purple, eightStar))
      .addTileBag(new QTile(red, clover))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(red, eightStar), new QTile(green, circle)), "383bob"))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(orange, eightStar), new QTile(green, star)), "smith"))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(blue, square), new QTile(green, circle)), "lee"))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("383bob", new DagStrategy(placementRules)),
      new DummyAIPlayer("smith", new DagStrategy(placementRules)),
      new DummyAIPlayer("lee", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("383bob"),
      new ArrayList<>());
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 8);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testLockoutGame2() throws IOException {
    IGameState state = new QStateBuilder()
      .placeTile(new Posn(-3, 0), new QTile(green, eightStar))
      .addTileBag(new QTile(purple, eightStar))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(red, eightStar), new QTile(green, circle)), "383bob"))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new QTile(orange, eightStar), new QTile(green, clover)), "smith"))
      .addPlayerInfo(new PlayerInfo(11,
        List.of(new QTile(blue, square), new QTile(green, circle)), "1hero"))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("383bob", new DagStrategy(placementRules)),
      new DummyAIPlayer("smith", new DagStrategy(placementRules)),
      new DummyAIPlayer("1hero", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("1hero", "383bob"),
      new ArrayList<>());
    XGamesInputCreator.createHarnessTest(state, players, results, "7/Tests", 9);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  public void initCheat() {
    placementRules = RuleUtil.createPlaceRules();
    ScoringRule scoringRules =  RuleUtil.createScoreRules();

    player1 = new DummyAIPlayer("Tester", new DagStrategy(placementRules));
    player2 = new DummyAIPlayer("SecondTester", new LdasgStrategy(placementRules));
    ref = new QReferee(placementRules, scoringRules, 900);
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(green, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(green, circle)),
                    "Cheater1"))
            .build();
  }

  @Test
  public void testCheaterIsKicked() throws IOException {
    initCheat();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater 1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 0);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void testNonAdjacentCoordCheater() throws IOException {
    initCheat();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_ADJACENT);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 1);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNotInLineCheat() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(green, eightStar)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_INLINE);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 2);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNotEnoughTilesCheat() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(green, eightStar)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_ENOUGH_TILES);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 3);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNotLegalNeighborsCheater() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, square)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(green, eightStar)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_LEGAL_NEIGHBOR);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 4);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNotEnoughTilesCheaterForcedNormal() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar),new QTile(purple, eightStar),
                    new QTile(purple, eightStar),new QTile(purple, eightStar),
                    new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(green, eightStar)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_ENOUGH_TILES);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Cheater1"),
            new ArrayList<>());
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 5);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNotALineCheaterPlaysNormally() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_INLINE);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Cheater1"),
            new ArrayList<>());
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 6);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testNoFitPrioritizeCheating() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square), new QTile(orange, clover)),
                    "Cheater1"))
            .build();
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_LEGAL_NEIGHBOR);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 7);
    GameResults actual = ref.playGame(cheatState, players);
    //assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testCheatersLoseBeforeDummiesFail() throws IOException {
    initCheat();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(red, eightStar), new QTile(purple, circle)), "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(blue, square)),
                    "Cheater1"))
            .build();
    player1 = new CheatingAIPlayer("Tester", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    player2 = new DummyAIPlayer("SecondTester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN);
    List<Player> players = List.of(
            player1, cheatingPlayer1, player2);
    GameResults results = new GameResults(List.of(),
            List.of("Tester" , "SecondTester", "Cheater1"));
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 8);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testCheatersDontCheat() throws IOException {
    initCheat();
    List<Tile> tiles1 = TestUtil.generateOneEachTile();
    List<Tile> tiles2 = TestUtil.generateOneEachTile();
    cheatState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(blue, eightStar))
            .addTileBag(new QTile(purple, eightStar))
            .addPlayerInfo(new PlayerInfo(0,
                    tiles1, "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    tiles2,
                    "Cheater1"))
            .build();
    player1 = new CheatingAIPlayer("Tester", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    cheatingPlayer1  = new CheatingAIPlayer("Cheater1", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    List<Player> players = List.of(
            player1, player2, cheatingPlayer1);
    GameResults results = new GameResults(List.of("Tester"),
            new ArrayList<>());
    XGamesInputCreator.createHarnessTest(cheatState, players, results, "8/Tests", 9);
    GameResults actual = ref.playGame(cheatState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  public void initLoop(){
    placementRules = RuleUtil.createPlaceRules();
    ScoringRule scoringRules =  RuleUtil.createScoreRules();
    player1 = new SimpleAIPlayer("Tester", new DagStrategy(placementRules));
    player2 = new SimpleAIPlayer("SecondTester", new LdasgStrategy(placementRules));
    ref = new QReferee(placementRules, scoringRules, 900);
    loopState = new QStateBuilder()
            .placeTile(new Posn(-3, 0), new QTile(green, eightStar))
            .addTileBag(new QTile(purple, eightStar), new QTile(green, circle))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover))
                    , "Tester"))
            .addPlayerInfo(new PlayerInfo(0,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "SecondTester"))
            .addPlayerInfo(new PlayerInfo(11,
                    List.of(new QTile(orange, square), new QTile(green, clover)),
                    "looper"))
            .build();
  }
  @Test
  public void testLoopingTakeTurnIsKicked() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.TAKE_TURN,2);
    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("SecondTester"),
            List.of("looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 0);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testLoopingNewTilesIsKicked() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,1);
    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("SecondTester"),
            List.of("looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 1);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void testLoopingSetupIsKicked() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.SETUP,1);
    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("SecondTester"),
            List.of("looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 2);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  //not sure if failing on win removes you from the game state or not
  //would impact what message you broadcast in the previous .win()
  @Test
  public void testLoopingWinIsKicked() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.WIN,1);
    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of(),
            List.of("looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 3);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }


  @Test
  public void testExcessiveSetupLooperNotKicked() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.SETUP,3);
    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("looper"),
            new ArrayList<>());
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 4);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testMultiLoopers() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,1);
    player2 = new LoopingAIPlayer("SecondTester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN,1);

    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("SecondTester","looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 5);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testCheatersandLoopers() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,2);
    player2 = new CheatingAIPlayer("SecondTester",new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    player1 = new LoopingAIPlayer("Tester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN,2);

    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(new ArrayList<>(),
            List.of("SecondTester","Tester","looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 6);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testExnAndLoopers() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,2);
    player2 = new DummyAIPlayer("SecondTester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN);

    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("Tester"),
            List.of("SecondTester","looper"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 7);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testExnLooperCheater() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,5);
    player1 = new CheatingAIPlayer("Tester", new DagStrategy(placementRules),
            CheatingAIPlayer.Cheat.NOT_OWNED);
    player2 = new DummyAIPlayer("SecondTester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN);

    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("looper"),
            List.of("Tester","SecondTester"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 8);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }

  @Test
  public void testMoreLoopThanNecessary() throws IOException {
    initLoop();
    loopPlayer = new LoopingAIPlayer("looper", new DagStrategy(placementRules),
            FailStep.NEW_TILES,7);
    player2 = new DummyAIPlayer("SecondTester", new DagStrategy(placementRules),
            FailStep.TAKE_TURN);

    List<Player> players = List.of(
            player1, player2, loopPlayer);
    GameResults results = new GameResults(List.of("looper"),
            List.of("SecondTester"));
    XGamesInputCreator.createHarnessTest(loopState, players, results, "9/Tests", 9);
    GameResults actual = ref.playGame(loopState, players);
    assertEquals(results.getWinners(), actual.getWinners());
    assertEquals(results.getRuleBreakers(), actual.getRuleBreakers());
  }
}