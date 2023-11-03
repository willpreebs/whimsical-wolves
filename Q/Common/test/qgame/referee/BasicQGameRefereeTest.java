package qgame.referee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.player.strategy.AlwaysPassStrategy;
import qgame.state.Bag;
import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;
import qgame.player.AlwaysExchangePlayer;
import qgame.player.AlwaysPassPlayer;
import qgame.player.MockPlayer;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.player.DummyAIPlayer;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.MultiScoringRule;
import qgame.rule.scoring.PointPerContiguousSequenceRule;
import qgame.rule.scoring.PointPerTileRule;
import qgame.rule.scoring.QRule;
import qgame.rule.scoring.ScoringRule;
import qgame.state.BasicQGameState;
import qgame.state.BasicQGameStateBuilder;
import qgame.state.QGameState;
import qgame.player.DummyAIPlayer.FailStep;

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

  QGameState stateForceFirstPass;
  QGameState allPass;
  QGameReferee ref;

  QGameState onePassOneExchange1;
  QGameState onePassOneExchange2;
  QGameState placeAll;

  @Before
  public void init() {
    placementRules = new MultiPlacementRule(new MatchTraitRule(),
      new ExtendSameLineRule(),
      new ExtendsBoardRule(), new CorrectPlayerTilesRule());
    ScoringRule scoringRules =  new MultiScoringRule(new PointPerTileRule(),
      new QRule(6), new PointPerContiguousSequenceRule());

    player1 = new DummyAIPlayer("Tester", new DagStrategy(placementRules));
    player2 = new DummyAIPlayer("Second Tester", new LdasgStrategy(placementRules));
    badMove = new DummyAIPlayer("Bad player", new BadTurnStrategy());
    disconnectPlayer = new DisconnectPlayer("Disconnect");
    timeOutPlayer = new TimeOutPlayer("Bobby", 1000);
    ref = new BasicQGameReferee(placementRules, scoringRules, 900, 6);
  }

  private void init1() {
    Map<Posn, Tile> tileMap = new HashMap<>();
    tileMap.put(new Posn(0, 1), new TileImpl(red, square));
    QGameMap map = new QGameMapImpl(tileMap);


    List<PlayerInfo> info = new ArrayList<>();
    info.add(new PlayerInfo(0, List.of(new TileImpl(orange, star))));
    info.add(new PlayerInfo(0, List.of(new TileImpl(orange, square))));
    stateForceFirstPass = new BasicQGameState(map, new ArrayList<>(), info);
  }

  @Before
  public void initAllPass(){
    Map<Posn, Tile> tileMap = new HashMap<>();
    tileMap.put(new Posn(0, 1), new TileImpl(red, square));
    QGameMap map = new QGameMapImpl(tileMap);
    List<PlayerInfo> info = new ArrayList<>();
    info.add(new PlayerInfo(0, List.of(new TileImpl(orange, star))));
    info.add(new PlayerInfo(0, List.of(new TileImpl(orange, circle))));
    allPass = new BasicQGameState(map, new ArrayList<>(), info);
  }

  @Test
  public void testBadMovePlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, badMove));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.winners().size());
    assertEquals(player1.name(), results.winners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(badMove.name());
    assertEquals(breakers.size(), results.ruleBreakers().size());
    assertEquals(badMove.name(), results.ruleBreakers().get(0));
  }

  @Test
  public void testDisconnectPlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, disconnectPlayer));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.winners().size());
    assertEquals(player1.name(), results.winners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(disconnectPlayer.name());
    assertEquals(breakers.size(), results.ruleBreakers().size());
    assertEquals(disconnectPlayer.name(), results.ruleBreakers().get(0));
  }

  @Test
  public void testTimeOutPlayer() {
    init1();
    GameResults results = ref.playGame(stateForceFirstPass, List.of(player1, timeOutPlayer));
    List<String> winners = new ArrayList<>();
    winners.add(player1.name());
    assertEquals(winners.size(), results.winners().size());
    assertEquals(player1.name(), results.winners().get(0));
    List<String> breakers = new ArrayList<>();
    breakers.add(timeOutPlayer.name());
    assertEquals(breakers.size(), results.ruleBreakers().size());
    assertEquals(timeOutPlayer.name(), results.ruleBreakers().get(0));
  }

  @Test
  public void testGameEndsWithAllPass() {
    initAllPass();
    GameResults result = ref.playGame(allPass,List.of(player1,player2));
    List<String> winners = new ArrayList<>(List.of(player1.name(), player2.name()));
    assertEquals(winners.size(), result.winners().size());
    List<String> ruleBreakers = new ArrayList<>();
    assertEquals(ruleBreakers.size(), result.ruleBreakers().size());
  }


  @Before
  public void initOnePassOneExchange1() {
    onePassOneExchange1 =
      new BasicQGameStateBuilder()
        .addRefTile(new TileImpl(blue, eightStar))
        .placeTile(new Posn(0, 0), new TileImpl(red, clover))
        .addPlayerInfo(new PlayerInfo(1, List.of(new TileImpl(green, square))))
        .addPlayerInfo(new PlayerInfo(5, List.of(new TileImpl(yellow, star))))
        .build();
  }

  @Before
  public void initOnePassOneExchange2() {
    onePassOneExchange2 =
      new BasicQGameStateBuilder()
        .addRefTile(new TileImpl(blue, eightStar))
        .placeTile(new Posn(0, 0), new TileImpl(red, clover))
        .addPlayerInfo(new PlayerInfo(5, List.of(new TileImpl(red, star))))
        .addPlayerInfo(new PlayerInfo(1, List.of(new TileImpl(green, square))))
        .build();
  }

  @Test
  public void testGameEndsWithOnePassOneExchange1() {
    MockPlayer passer = new AlwaysPassPlayer(List.of(new TileImpl(blue,square)));
    List<Player> players = new ArrayList<>(List.of(passer, player1));
    GameResults results = ref.playGame(onePassOneExchange1, players);
    assertEquals(1, results.winners().size());
    assertEquals("Tester", results.winners().get(0));
//    assertEquals(List.of(new TileImpl(blue,square)), passer.returnHand());
  }

  @Test
  public void testGameEndsWithOnePassOneExchange2() {
    MockPlayer passer = new AlwaysPassPlayer(List.of(new TileImpl(blue,square)));
    MockPlayer exchanger = new AlwaysExchangePlayer(new Bag<>(List.of(new TileImpl(blue,square))));
    List<Player> players = new ArrayList<>(List.of(exchanger, passer));
    GameResults results = ref.playGame(onePassOneExchange2, players);
    assertEquals(1, results.winners().size());
    assertEquals("Exchanger", results.winners().get(0));
//    assertEquals(List.of(new TileImpl(blue,square)), passer.returnHand());
//    assertEquals(List.of(new TileImpl(blue,eightStar)), exchanger.returnHand());
  }

  @Before
  public void initEndAfterPlaceAll(){
    placeAll = new BasicQGameStateBuilder()
      .addRefTile(new TileImpl(blue, eightStar))
      .placeTile(new Posn(0, 0), new TileImpl(red, clover))
      .addPlayerInfo(new PlayerInfo(0, List.of(new TileImpl(red, eightStar),
        new TileImpl(green, eightStar))))
      .addPlayerInfo(new PlayerInfo(0, List.of(new TileImpl(green, square),
        new TileImpl(green, eightStar), new TileImpl(green, eightStar),
        new TileImpl(green, eightStar), new TileImpl(green, eightStar),
        new TileImpl(green, eightStar), new TileImpl(green, eightStar))))
      .build();
  }

  @Test
  public void testGameEndsAfterPlaceHand() {
    List<Player> players = new ArrayList<>(List.of(player1, player2));
    GameResults results = ref.playGame(placeAll, players);
    assertEquals(1, results.winners().size());
    assertEquals("Tester", results.winners().get(0));
    try {
      assertTrue(XGamesInputCreator.createTest(placeAll, players, results, "7/Tests", 0));
    }
    catch (IOException e) {
    }
  }

  @Test
  public void testPlayersFailToSetup() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(0, 0), new TileImpl(red, diamond))
      .addRefTile(new TileImpl(orange, square), new TileImpl(green, star), new TileImpl(red,
        square))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(purple, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(orange, clover), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(yellow, diamond), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(red, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("bobby", new DagStrategy(placementRules)),
      new DummyAIPlayer("terry", new LdasgStrategy(placementRules), FailStep.NEW_TILES),
      new DummyAIPlayer("ben", new DagStrategy(placementRules)),
    new DummyAIPlayer("alex", new LdasgStrategy(placementRules), FailStep.SETUP));
    GameResults results = new GameResults(List.of("ben"), List.of("alex", "terry"));
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 1);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());

  }

  @Test
  public void testAllPlayersFailToSetup() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(0, 0), new TileImpl(red, diamond))
      .addRefTile(new TileImpl(orange, square), new TileImpl(green, star), new TileImpl(red,
        square))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(purple, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(orange, clover), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(yellow, diamond), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(red, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("bobby", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("terry", new LdasgStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("ben", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("alex", new LdasgStrategy(placementRules), FailStep.SETUP));
    GameResults results = new GameResults(new ArrayList<>(),
      List.of("bobby", "terry", "ben", "alex"));
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 2);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }


  @Test
  public void test3WinnersSortedProperly() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(1, 0), new TileImpl(red, diamond))
      .addRefTile(new TileImpl(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(purple, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(orange, clover), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(yellow, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(blue, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("ben3", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben1", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben4", new DagStrategy(placementRules)),
      new DummyAIPlayer("ben2", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("ben1", "ben3", "ben4"),
      new ArrayList<>());
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 3);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }

  @Test
  public void testTakeTurnRemovesProperly() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(1, 0), new TileImpl(red, diamond))
      .addRefTile(new TileImpl(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(purple, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(orange, star), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new TileImpl(blue, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("appleeeee", new DagStrategy(placementRules)),
      new DummyAIPlayer("Appleeeee", new DagStrategy(placementRules)),
      new DummyAIPlayer("LOSER", new DagStrategy(placementRules), FailStep.TAKE_TURN));
    GameResults results = new GameResults(List.of("Appleeeee", "appleeeee"),
      List.of("LOSER"));
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 4);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }

  @Test
  public void testWinnerStillInResults() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(1, 0), new TileImpl(red, diamond))
      .addRefTile(new TileImpl(green, square))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new TileImpl(red, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(orange, eightStar), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(3,
        List.of(new TileImpl(blue, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("appleeeee", new DagStrategy(placementRules), FailStep.WIN),
      new DummyAIPlayer("Appleeeee", new DagStrategy(placementRules), FailStep.WIN),
      new DummyAIPlayer("NotALoser", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("Appleeeee", "appleeeee"),
      new ArrayList<>());
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 5);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }

  @Test
  public void testSameColorPlaceAllTilesInSecondRound() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(1, 0), new TileImpl(red, square))
      .addRefTile(new TileImpl(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(red, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(12,
        List.of(new TileImpl(orange, eightStar), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(15,
        List.of(new TileImpl(blue, clover), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("tess", new DagStrategy(placementRules)),
      new DummyAIPlayer("tessy", new DagStrategy(placementRules)),
      new DummyAIPlayer("3Peat", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("tess", "tessy"),
      new ArrayList<>());
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 6);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }


  @Test
  public void testAllRemovedVarious() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(1, 0), new TileImpl(red, square))
      .addRefTile(new TileImpl(green, square))
      .addPlayerInfo(new PlayerInfo(2,
        List.of(new TileImpl(red, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(12,
        List.of(new TileImpl(orange, eightStar), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(15,
        List.of(new TileImpl(blue, square), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("tess", new DagStrategy(placementRules), FailStep.SETUP),
      new DummyAIPlayer("tessy", new DagStrategy(placementRules), FailStep.TAKE_TURN),
      new DummyAIPlayer("3Peat", new DagStrategy(placementRules), FailStep.NEW_TILES));
    GameResults results = new GameResults(new ArrayList<>(),
      List.of("tess", "tessy", "3Peat"));
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 7);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }


  @Test
  public void testLockoutGame() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(-3, 0), new TileImpl(green, eightStar))
      .addRefTile(new TileImpl(purple, eightStar))
      .addRefTile(new TileImpl(red, clover))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(red, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(orange, eightStar), new TileImpl(green, star))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(blue, square), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("383bob", new DagStrategy(placementRules)),
      new DummyAIPlayer("smith", new DagStrategy(placementRules)),
      new DummyAIPlayer("lee", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("383bob"),
      new ArrayList<>());
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 8);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }

  @Test
  public void testLockoutGame2() throws IOException {
    QGameState state = new BasicQGameStateBuilder()
      .placeTile(new Posn(-3, 0), new TileImpl(green, eightStar))
      .addRefTile(new TileImpl(purple, eightStar))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(red, eightStar), new TileImpl(green, circle))))
      .addPlayerInfo(new PlayerInfo(0,
        List.of(new TileImpl(orange, eightStar), new TileImpl(green, clover))))
      .addPlayerInfo(new PlayerInfo(11,
        List.of(new TileImpl(blue, square), new TileImpl(green, circle))))
      .build();
    List<Player> players = List.of(
      new DummyAIPlayer("383bob", new DagStrategy(placementRules)),
      new DummyAIPlayer("smith", new DagStrategy(placementRules)),
      new DummyAIPlayer("1hero", new DagStrategy(placementRules)));
    GameResults results = new GameResults(List.of("1hero", "383bob"),
      new ArrayList<>());
    XGamesInputCreator.createTest(state, players, results, "7/Tests", 9);
    GameResults actual = ref.playGame(state, players);
    assertEquals(results.winners(), actual.winners());
    assertEquals(results.ruleBreakers(), actual.ruleBreakers());
  }
}