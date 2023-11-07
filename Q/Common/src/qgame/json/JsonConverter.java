package qgame.json;

import com.google.gson.*;
import java.util.*;
import java.util.stream.Stream;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.player.DummyAIPlayer;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.player.strategy.TurnStrategy;
import qgame.referee.GameResults;
import qgame.rule.placement.PlacementRule;
import qgame.state.Bag;
import qgame.state.QPlayerGameState;
import qgame.state.QGameState;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.state.IGameState;
import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.map.Tile;
import qgame.state.map.QTile;
import qgame.util.PosnUtil;

import static java.util.Arrays.stream;
import static qgame.util.ValidationUtil.nonNullObj;
import static qgame.util.ValidationUtil.validateArg;

/**
 * Supports reading in a JMap and creating it to a QGameMap, reading in a JTile and converting it
 * to a QGame Tile, and taking a list of positions and turning it into a JsonArray of jCoordinates.
 */
public class JsonConverter {

  private static int getAsInt(JsonElement element) {
    validateArg(JsonElement::isJsonPrimitive, element, "Must be number");
    JsonPrimitive prim = element.getAsJsonPrimitive();
    validateArg(JsonPrimitive::isNumber, prim, "Must be number");
    return prim.getAsInt();
  }

  private static String getAsString(JsonElement element) {
    validateArg(JsonElement::isJsonPrimitive, element, "Must be string");
    JsonPrimitive prim = element.getAsJsonPrimitive();
    validateArg(JsonPrimitive::isString, prim, "Must be string");
    return prim.getAsString();
  }

  private static JsonElement[] getAsArray(JsonElement element) {
    validateArg(JsonElement::isJsonArray, element, "Must be json array");
    JsonArray arr = element.getAsJsonArray();
    JsonElement[] res = new JsonElement[arr.size()];
    for (int i = 0; i < arr.size(); i++) {
      res[i] = arr.get(i);
    }
    return res;
  }

  private static void updateMapFromJCellAndRow(Map<Posn, Tile> map, JsonElement element, int yVal) {
    JsonArray jCell = element.getAsJsonArray();
    int xVal = getAsInt(jCell.get(0));
    map.put(new Posn(yVal, xVal), tileFromJTile(jCell.get(1)));
  }

  private static void updateRowFromJRow(Map<Posn, Tile> map, JsonElement element) {
    JsonElement[] arr = getAsArray(element);
    int yVal = getAsInt(arr[0]);
    Stream.of(arr)
      .skip(1)
      .forEach(ele -> updateMapFromJCellAndRow(map, ele, yVal));
  }

  /**
   * Reads in a JMap and converts the JMap to a QGameMap.
   * @param element the JMap that is a 2D Jason array
   * @return a QGameMap
   */
  public static IMap qGameMapFromJMap(JsonElement element) {
    Map<Posn, Tile> map = new HashMap<>();
    JsonElement[] arr = getAsArray(element);
    Stream.of(arr)
      .forEach(ele -> updateRowFromJRow(map, ele));
    return new QMap(map);
  }

  private static SortedMap<Integer, SortedMap<Integer, Tile>> create2DMapFromState(Map<Posn, Tile> posnMap) {
    SortedMap<Integer, SortedMap<Integer, Tile>> rowMap = new TreeMap<>();
    for (Map.Entry<Posn, Tile> entry : posnMap.entrySet()) {
      Posn posn = entry.getKey();
      Tile tile = entry.getValue();
      int y = posn.y();
      int x = posn.x();
      if (!rowMap.containsKey(y)) {
        rowMap.put(y, new TreeMap<>());
      }
      rowMap.get(y).put(x, tile);
    }
    return rowMap;
  }

  private static void addJCellsFromRowToJRow(JsonArray row, SortedMap<Integer, Tile> cells) {
    for (Map.Entry<Integer, Tile> cellEntry : cells.entrySet()) {
      int col = cellEntry.getKey();
      Tile tile = cellEntry.getValue();
      JsonArray cellArray = new JsonArray();
      cellArray.add(col);
      cellArray.add(jTileFromTile(tile));
      row.add(cellArray);
    }
  }
  private static JsonElement createJMapFrom2DMap(SortedMap<Integer, SortedMap<Integer, Tile>> map) {
    JsonArray jMap = new JsonArray();
    for (int rowNum : map.keySet()) {
      SortedMap<Integer, Tile> cells = map.get(rowNum);
      JsonArray rowArray = new JsonArray();
      rowArray.add(rowNum);
      addJCellsFromRowToJRow(rowArray, cells);
      jMap.add(rowArray);
    }
    return jMap;
  }

  public static JsonElement jMapFromQGameMap(IMap map) {
    SortedMap<Integer, SortedMap<Integer, Tile>> rowMap = create2DMapFromState(map.getBoardState());
    return createJMapFrom2DMap(rowMap);
  }


  public static JsonElement jTileFromTile(Tile tile) {
    JsonObject obj = new JsonObject();
    obj.add("color", new JsonPrimitive(tile.color().toString()));
    obj.add("shape", new JsonPrimitive(tile.shape().toString()));
    return obj;
  }

  public static JsonElement jTilesFromTiles(Collection<Tile> tiles) {
    JsonArray arr = new JsonArray();
    tiles.forEach(tile -> arr.add(jTileFromTile(tile)));
    return arr;
  }


  /**
   * Takes in a JTile and converts it to a Tile that works with QGame.
   *
   * @param element the passed in JTile
   * @return a QGame tile
   */
  public static Tile tileFromJTile(JsonElement element) {
    JsonObject obj = element.getAsJsonObject();
    String colorName = obj.get("color").getAsString();
    String shapeName = obj.get("shape").getAsString();

    return new QTile(Tile.Color.fromString(colorName), Tile.Shape.fromString(shapeName));
  }

  public static Collection<Tile> tilesFromJTileArray(JsonElement element) {
    JsonElement[] tiles = getAsArray(element);
    return Arrays.stream(tiles).map(JsonConverter::tileFromJTile).toList();
  }

  private static JsonObject jCoordFromPosn(Posn posn) {
    JsonObject obj = new JsonObject();
    obj.add("column", new JsonPrimitive(posn.x()));
    obj.add("row", new JsonPrimitive(posn.y()));
    return obj;
  }

  private static Posn posnFromJCoord(JsonElement element) {
    JsonObject object = element.getAsJsonObject();
    int y = object.get("row").getAsInt();
    int x = object.get("column").getAsInt();
    return new Posn(y, x);
  }

  /**
   * Takes in a list of positions and returns a corresponding list of jCoordinates.
   * Sorted in row-column order because of XMap.
   * @param posns List of positions
   * @return Json Array of jCoordinates
   * @throws IllegalArgumentException if passed a null posn
   */
  public static JsonArray jCoordsFromPosns(List<Posn> posns) throws IllegalArgumentException {
    nonNullObj(posns, "Positions cannot be null");
    SortedSet<Posn> set = new TreeSet<>(PosnUtil::rowColumnCompare);
    set.addAll(posns);
    JsonArray result = new JsonArray();
    set.forEach(posn -> result.add(jCoordFromPosn(posn)));
    return result;
  }

  private static Placement placementFromJPlacement(JsonElement jPlacement) {
    JsonObject object = jPlacement.getAsJsonObject();
    Tile tile = tileFromJTile(object.get("1tile"));
    Posn posn = posnFromJCoord(object.get("coordinate"));
    return new Placement(posn, tile);
  }

  public static List<Placement> placementsFromJPlacements(JsonElement jPlacements) throws IllegalArgumentException {
    JsonElement[] arr = getAsArray(jPlacements);
    return Stream.of(arr)
      .map(JsonConverter::placementFromJPlacement)
      .toList();
  }

  private static List<Tile> tilesFromJPlayer(JsonElement player) {
    JsonObject playerObj = player.getAsJsonObject();
    Collection<Tile> tiles = tilesFromJTileArray(playerObj.get("tile*"));
    return new ArrayList<>(tiles);
  }

  private static int scoreFromJPlayer(JsonElement jPlayer) {
    JsonObject playerObj = jPlayer.getAsJsonObject();
    return getAsInt(playerObj.get("score"));
  }

  private static int playerInfoFromJsonElement(JsonElement element) {
    if (element.isJsonObject()) {
      return scoreFromJPlayer(element);
    }
    return getAsInt(element);
  }

  private static PlayerInfo playerFromJPlayer(JsonElement element) {
   JsonObject jPlayer = element.getAsJsonObject();
   int score = getAsInt(jPlayer.get("score"));
   Collection<Tile> tiles = tilesFromJTileArray(jPlayer.get("tile*"));
   String name = getAsString(jPlayer.get("name"));
   return new PlayerInfo(score, tiles, name);
  }

  public static List<PlayerInfo> playerInfosFromJPlayers(JsonElement element) {
    JsonElement[] players = getAsArray(element);
    return new ArrayList<>(Stream.of(players).map(JsonConverter::playerFromJPlayer).toList());
  }
  private static List<Integer> scoresFromJPlayers(JsonElement element) {
    JsonElement[] arr = getAsArray(element);
    return Stream.of(arr)
      .map(JsonConverter::playerInfoFromJsonElement)
      .toList();
  }

  public static IPlayerGameState playerGameStateFromJPub(JsonElement jPub)
    throws IllegalArgumentException {
    JsonObject jPubAsObj = jPub.getAsJsonObject();
    IMap board = qGameMapFromJMap(jPubAsObj.get("map"));
    int tiles = getAsInt(jPubAsObj.get("tile*"));
    List<Integer> scores = scoresFromJPlayers(jPubAsObj.get("players"));
    JsonElement[] playerArr = getAsArray(jPubAsObj.get("players"));
    List<Tile> currentPlayerTiles = tilesFromJPlayer(playerArr[0]);
    return new QPlayerGameState(scores, board, tiles, currentPlayerTiles, "");
  }

  private static JsonElement jPlayerFromPlayerState(IPlayerGameState state) {
    JsonObject player = new JsonObject();
    player.add("score", new JsonPrimitive(state.playerScores().get(0)));
    player.add("tile*", jTilesFromTiles(state.getCurrentPlayerTiles().viewItems()));
    return player;
  }

  public static JsonElement playerStateToJPub(IPlayerGameState state) {
    JsonObject jPub = new JsonObject();
    jPub.add("map", jMapFromQGameMap(state.viewBoard()));
    jPub.add("tile*", new JsonPrimitive(state.remainingTiles()));
    JsonArray arr = new JsonArray();
    arr.add(jPlayerFromPlayerState(state));
    state
      .playerScores()
      .stream()
      .skip(1)
      .forEach(score -> arr.add(new JsonPrimitive(score)));
    jPub.add("players", arr);
    return jPub;
  }
  public static JsonElement strategyToJson(TurnStrategy strategy) {
    return switch (strategy) {
      case DagStrategy dag -> new JsonPrimitive("dag");
      case LdasgStrategy ldasg -> new JsonPrimitive("ldasg");
      default -> throw new IllegalStateException("Unexpected value: " + strategy);
    };
  }

  public static JsonElement onePlacementFromPlacement(Placement placement) {
    JsonObject onePlacement = new JsonObject();
    onePlacement.add("coordinate", jCoordFromPosn(placement.posn()));
    onePlacement.add("1tile", jTileFromTile(placement.tile()));
    return onePlacement;
  }

  public static TurnStrategy jStrategyToStrategy(JsonElement element, PlacementRule rule) {
    String type = getAsString(element);
    return switch (type) {
      case "dag" -> new DagStrategy(rule);
      case "ldasg" -> new LdasgStrategy(rule);
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };
  }


  public static JsonElement actionToJson(TurnAction action) {
    return switch (action) {
      case PassAction pass -> new JsonPrimitive("pass");
      case ExchangeAction exchangeAction -> new JsonPrimitive("replace");
      case PlaceAction place -> onePlacementFromPlacement(place.placements().get(0));
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }


  public static IGameState JStateToQGameState(JsonElement jState) {
    JsonObject jStateObj = jState.getAsJsonObject();
    IMap gameMap = qGameMapFromJMap(jStateObj.get("map"));
    Bag<Tile> tiles = new Bag<>(tilesFromJTileArray(jStateObj.get("tile*")));
    List<PlayerInfo> players = playerInfosFromJPlayers(jStateObj.get("players"));
    return new QGameState(gameMap, tiles, players);
  }

  private static DummyAIPlayer.FailStep failStepFromExn(JsonElement element) {
    String exn = getAsString(element);
    return switch (exn) {
      case "setup" -> DummyAIPlayer.FailStep.SETUP;
      case "take-turn" -> DummyAIPlayer.FailStep.TAKE_TURN;
      case "new-tiles" -> DummyAIPlayer.FailStep.NEW_TILES;
      case "win" -> DummyAIPlayer.FailStep.WIN;
      default -> throw new IllegalStateException("Unexpected value: " + exn);
    };
  }

    private static DummyAIPlayer.Cheat cheatFromJCheat(JsonElement element) {
    String exn = getAsString(element);
    return switch (exn) {
      case "non-adjacent-coordinate" -> DummyAIPlayer.Cheat.NOT_ADJACENT;
      case "tile-not-owned" -> DummyAIPlayer.Cheat.NOT_OWNED;
      case "not-a-line" -> DummyAIPlayer.Cheat.NOT_INLINE;
      case "bad-ask-for-tiles" -> DummyAIPlayer.Cheat.NOT_ENOUGH_TILES;
      case "no-fit" -> DummyAIPlayer.Cheat.NOT_LEGAL_PLACEMENT;
      default -> throw new IllegalStateException("Unexpected value: " + exn);
    };
  }

  private static Player playerFromJActorSpec(JsonElement element, PlacementRule rule) {
    JsonElement[] spec = getAsArray(element);
    validateArg(size -> size >= 2, spec.length, "Spec needs at least 2 elements");
    String name = getAsString(spec[0]);
    validateArg(size -> size <= 20, name.length(), "Name must be at most 20 characters");
    TurnStrategy strat = jStrategyToStrategy(spec[1], rule);
    DummyAIPlayer.FailStep step = DummyAIPlayer.FailStep.NONE;
    if (spec.length == 3) {
      step = failStepFromExn(spec[2]);
    }
    return new DummyAIPlayer(name, strat, step);
  }

  private static Player playerFromJActorSpecA(JsonElement element, PlacementRule rule) {
    JsonElement[] spec = getAsArray(element);
    validateArg(size -> size >= 2, spec.length, "Spec needs at least 2 elements");
    String name = getAsString(spec[0]);
    validateArg(size -> size <= 20, name.length(), "Name must be at most 20 characters");
    TurnStrategy strat = jStrategyToStrategy(spec[1], rule);
    DummyAIPlayer.FailStep step = DummyAIPlayer.FailStep.NONE;
    DummyAIPlayer.Cheat cheat = DummyAIPlayer.Cheat.NONE;
    if (spec.length == 3) {
      step = failStepFromExn(spec[2]);
    }
    if (spec.length == 4) {
      cheat = cheatFromJCheat(spec[3]);
    }
    return new DummyAIPlayer(name, strat, step, cheat);
  }

  public static List<Player> playersFromJActors(JsonElement element, PlacementRule rule) {
    JsonElement[] players = getAsArray(element);
    return new ArrayList<>(stream(players).map(spec -> playerFromJActorSpec(spec, rule)).toList());
  }

  public static List<Player> playersFromJActorSpecA(JsonElement element, PlacementRule rule) {
    JsonElement[] players = getAsArray(element);
    return new ArrayList<>(stream(players).map(spec -> playerFromJActorSpecA(spec, rule)).toList());
  }

  public static JsonElement jResultsFromGameResults(GameResults results) {
    List<String> winners = results.winners();
    List<String> ruleBreakers = results.ruleBreakers();
    winners.sort(Comparator.naturalOrder());
    JsonArray winnersArray = new JsonArray();
    JsonArray breakersArray = new JsonArray();
    winners.forEach(winnersArray::add);
    ruleBreakers.forEach(breakersArray::add);

    JsonArray result = new JsonArray();
    result.add(winnersArray);
    result.add(breakersArray);
    return result;
  }

  private static JsonElement jPlayerFromPlayerInfo(PlayerInfo info) {
    JsonObject object = new JsonObject();
    object.add("score", new JsonPrimitive(info.score()));
    object.add("tile*", jTilesFromTiles(info.tiles().viewItems()));
    return object;
  }
  public static JsonElement jStateFromQGameState(IGameState state) {
    JsonElement map = jMapFromQGameMap(state.viewBoard());
    JsonElement tiles = jTilesFromTiles(state.refereeTiles().viewItems());
    JsonArray players = new JsonArray();
    state.playerInformation()
      .stream()
      .map(JsonConverter::jPlayerFromPlayerInfo)
      .forEach(players::add);
    JsonObject result = new JsonObject();
    result.add("map", map);
    result.add("tile*", tiles);
    result.add("players", players);
    return result;
  }



}
