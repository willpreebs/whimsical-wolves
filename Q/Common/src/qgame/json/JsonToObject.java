package qgame.json;

import static java.util.Arrays.stream;
import static qgame.util.ValidationUtil.validateArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.player.CheatingAIPlayer;
import qgame.player.DummyAIPlayer;
import qgame.player.LoopingAIPlayer;
import qgame.player.Player;
import qgame.player.PlayerInfo;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.player.strategy.TurnStrategy;
import qgame.referee.RefereeStateConfig;
import qgame.rule.placement.IPlacementRule;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.QPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.map.QTile;
import qgame.state.map.Tile;
import qgame.util.RuleUtil;

public class JsonToObject {

    /**
     * Reads in a JMap and converts the JMap to a QGameMap.
     * 
     * @param element the JMap that is a 2D Jason array
     * @return a QGameMap
     */
    public static IMap qGameMapFromJMap(JsonElement element) {
        Map<Posn, Tile> map = new HashMap<>();
        JsonElement[] arr = JsonConverterUtil.getAsElementArray(element);
        Stream.of(arr)
                .forEach(ele -> JsonConverterUtil.updateRowFromJRow(map, ele));
        return new QMap(map);
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
        JsonElement[] tiles = JsonConverterUtil.getAsElementArray(element);
        return Arrays.stream(tiles).map(JsonToObject::tileFromJTile).toList();
    }

    private static Posn posnFromJCoord(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        int y = object.get("row").getAsInt();
        int x = object.get("column").getAsInt();
        return new Posn(y, x);
    }

    private static Placement placementFromJPlacement(JsonElement jPlacement) {
        JsonObject object = jPlacement.getAsJsonObject();
        Tile tile = tileFromJTile(object.get("1tile"));
        Posn posn = posnFromJCoord(object.get("coordinate"));
        return new Placement(posn, tile);
    }

    public static List<Placement> placementsFromJPlacements(JsonElement jPlacements) throws IllegalArgumentException {
        JsonElement[] arr = JsonConverterUtil.getAsElementArray(jPlacements);
        return Stream.of(arr)
                .map(JsonToObject::placementFromJPlacement)
                .toList();
    }

    private static List<Tile> tilesFromJPlayer(JsonElement player) {
        JsonObject playerObj = player.getAsJsonObject();
        Collection<Tile> tiles = tilesFromJTileArray(playerObj.get("tile*"));
        return new ArrayList<>(tiles);
    }

    private static int scoreFromJPlayer(JsonElement jPlayer) {
        JsonObject playerObj = jPlayer.getAsJsonObject();
        return JsonConverterUtil.getAsInt(playerObj.get("score"));
    }

    private static int playerInfoFromJsonElement(JsonElement element) {
        if (element.isJsonObject()) {
            return scoreFromJPlayer(element);
        }
        return JsonConverterUtil.getAsInt(element);
    }

    private static PlayerInfo playerFromJPlayer(JsonElement element) {
        JsonObject jPlayer = element.getAsJsonObject();
        int score = JsonConverterUtil.getAsInt(jPlayer.get("score"));
        Collection<Tile> tiles = tilesFromJTileArray(jPlayer.get("tile*"));
        String name = JsonConverterUtil.getAsString(jPlayer.get("name"));
        return new PlayerInfo(score, tiles, name);
    }

    private static PlayerInfo playerFromOldJPlayer(JsonElement element) {
        JsonObject jPlayer = element.getAsJsonObject();
        int score = JsonConverterUtil.getAsInt(jPlayer.get("score"));
        Collection<Tile> tiles = tilesFromJTileArray(jPlayer.get("tile*"));
        String name = "";
        return new PlayerInfo(score, tiles, name);
    }

    public static List<PlayerInfo> playerInfosFromJPlayers(JsonElement element) {
        JsonElement[] players = JsonConverterUtil.getAsElementArray(element);
        return new ArrayList<>(Stream.of(players).map(JsonToObject::playerFromJPlayer).toList());
    }

    public static List<PlayerInfo> playerInfosFromOldJPlayers(JsonElement element) {
        JsonElement[] players = JsonConverterUtil.getAsElementArray(element);
        return new ArrayList<>(Stream.of(players).map(JsonToObject::playerFromOldJPlayer).toList());
    }

    private static List<Integer> scoresFromJPlayers(JsonElement element) {
        JsonElement[] arr = JsonConverterUtil.getAsElementArray(element);
        return Stream.of(arr)
                .map(JsonToObject::playerInfoFromJsonElement)
                .toList();
    }

    public static IPlayerGameState playerGameStateFromJPub(JsonElement jPub)
            throws IllegalArgumentException {
        JsonObject jPubAsObj = jPub.getAsJsonObject();
        IMap board = qGameMapFromJMap(jPubAsObj.get("map"));
        int tiles = JsonConverterUtil.getAsInt(jPubAsObj.get("tile*"));
        List<Integer> scores = scoresFromJPlayers(jPubAsObj.get("players"));
        JsonElement[] playerArr = JsonConverterUtil.getAsElementArray(jPubAsObj.get("players"));
        List<Tile> currentPlayerTiles = tilesFromJPlayer(playerArr[0]);
        return new QPlayerGameState(scores, board, tiles, currentPlayerTiles, "");
    }

    public static TurnStrategy jStrategyToStrategy(JsonElement element, IPlacementRule rule) {
        String type = JsonConverterUtil.getAsString(element);
        return switch (type) {
            case "dag" -> new DagStrategy(rule);
            case "ldasg" -> new LdasgStrategy(rule);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public static IGameState jStateToQGameState(JsonElement jState) {
        JsonObject jStateObj = jState.getAsJsonObject();
        IMap gameMap = qGameMapFromJMap(jStateObj.get("map"));
        Bag<Tile> tiles = new Bag<>(tilesFromJTileArray(jStateObj.get("tile*")));
        List<PlayerInfo> players = playerInfosFromJPlayers(jStateObj.get("players"));
        return new QGameState(gameMap, tiles, players);
    }

    // parses an IGameState without requiring inclusion of player names
    public static IGameState jStateToOldQGameState(JsonElement jState) {
        JsonObject jStateObj = jState.getAsJsonObject();
        IMap gameMap = qGameMapFromJMap(jStateObj.get("map"));
        Bag<Tile> tiles = new Bag<>(tilesFromJTileArray(jStateObj.get("tile*")));
        List<PlayerInfo> players = playerInfosFromOldJPlayers(jStateObj.get("players"));
        return new QGameState(gameMap, tiles, players);
    }

    private static DummyAIPlayer.FailStep failStepFromExn(JsonElement element) {
        String exn = JsonConverterUtil.getAsString(element);
        return switch (exn) {
            case "setup" -> DummyAIPlayer.FailStep.SETUP;
            case "take-turn" -> DummyAIPlayer.FailStep.TAKE_TURN;
            case "new-tiles" -> DummyAIPlayer.FailStep.NEW_TILES;
            case "win" -> DummyAIPlayer.FailStep.WIN;
            default -> throw new IllegalStateException("Unexpected value for JExn: " + exn);
        };
    }

    private static CheatingAIPlayer.Cheat cheatFromJCheat(JsonElement element) {
        String cheat = JsonConverterUtil.getAsString(element);
        return switch (cheat) {
            case "non-adjacent-coordinate" -> CheatingAIPlayer.Cheat.NOT_ADJACENT;
            case "tile-not-owned" -> CheatingAIPlayer.Cheat.NOT_OWNED;
            case "not-a-line" -> CheatingAIPlayer.Cheat.NOT_INLINE;
            case "bad-ask-for-tiles" -> CheatingAIPlayer.Cheat.NOT_ENOUGH_TILES;
            case "no-fit" -> CheatingAIPlayer.Cheat.NOT_LEGAL_NEIGHBOR;
            default -> throw new IllegalStateException("Unexpected value: " + cheat);
        };
    }

    private static Player playerFromJActorSpec(JsonElement element, IPlacementRule rule) {
        JsonElement[] spec = JsonConverterUtil.getAsElementArray(element);
        validateArg(size -> size >= 2, spec.length, "Spec needs at least 2 elements");
        String name = JsonConverterUtil.getAsString(spec[0]);
        validateArg(size -> size <= 20, name.length(), "Name must be at most 20 characters");
        TurnStrategy strat = jStrategyToStrategy(spec[1], rule);
        DummyAIPlayer.FailStep step = DummyAIPlayer.FailStep.NONE;
        if (spec.length == 3) {
            step = failStepFromExn(spec[2]);
        }
        return new DummyAIPlayer(name, strat, step);
    }

    private static Player playerFromJActorSpecA(JsonElement element) {
        return playerFromJActorSpecB(element);
    }

    public static Player playerFromJActorSpecB(JsonElement element) {
        JsonElement[] spec = JsonConverterUtil.getAsElementArray(element);
        validateArg(size -> size >= 2, spec.length, "Spec needs at least 2 elements");
        String name = JsonConverterUtil.getAsString(spec[0]);
        validateArg(size -> size <= 20, name.length(), "Name must be at most 20 characters");
        IPlacementRule rules = RuleUtil.createPlaceRules();
        TurnStrategy strat = jStrategyToStrategy(spec[1], rules);

        DummyAIPlayer.FailStep step = DummyAIPlayer.FailStep.NONE;
        CheatingAIPlayer.Cheat cheat = CheatingAIPlayer.Cheat.NONE;

        if (spec.length == 2) {
            return new DummyAIPlayer(name, strat);
        }
        if (spec.length == 3) {
            step = failStepFromExn(spec[2]);
            return new DummyAIPlayer(name, strat, step);
        }
        if (spec.length == 4) {
            if (JsonConverterUtil.getAsString(spec[2]).equals("a cheat")) {
                cheat = cheatFromJCheat(spec[3]);
                return new CheatingAIPlayer(name, strat, cheat);
            } else {
                step = failStepFromExn(spec[2]);
                int count = JsonConverterUtil.getAsInt(spec[3]);
                return new LoopingAIPlayer(name, strat, step, count);
            }
        } else {
            throw new IllegalArgumentException("Invalid spec length");
        }
    }

    public static List<Player> playersFromJActors(JsonElement element, IPlacementRule rule) {
        JsonElement[] players = JsonConverterUtil.getAsElementArray(element);
        return new ArrayList<>(stream(players).map(spec -> playerFromJActorSpec(spec, rule)).toList());
    }

    public static List<Player> playersFromJActorSpecA(JsonElement element) {
        JsonElement[] players = JsonConverterUtil.getAsElementArray(element);
        return new ArrayList<>(stream(players).map(spec -> playerFromJActorSpecA(spec)).toList());
    }

    public static List<Player> playersFromJActorSpecB(JsonElement element) {
        JsonElement[] players = JsonConverterUtil.getAsElementArray(element);
        return new ArrayList<>(stream(players).map(spec -> playerFromJActorSpecB(spec)).toList());
    }

    public static IGameState initializeNewStateWithNewPlayerList(IGameState state, List<Player> players,
            boolean verifyNames) {
        List<PlayerInfo> newInfos = new ArrayList<>();
        List<PlayerInfo> oldInfos = state.getAllPlayerInformation();

        validateArg(p -> p.size() == oldInfos.size(), players,
                "List of players must be the same size as the list of player information in the state");

        for (int i = 0; i < oldInfos.size(); i++) {
            PlayerInfo info = oldInfos.get(i);
            Player p = players.get(i);
            if (verifyNames && !info.getName().equals(p.name())) {
                throw new IllegalArgumentException("List of players must be given in original order.");
            }
            // System.out.println("Adding player with name: " + p.name());
            newInfos.add(new PlayerInfo(info.getScore(), info.getTiles().getItems(), players.get(i).name()));
        }

        return new QGameState(state.getBoard(), state.getRefereeTiles(), newInfos);
    }

    // TODO: put in GameState?
    public static IGameState initializeNewStateWithNewPlayerList(IGameState state, List<Player> players) {
        return initializeNewStateWithNewPlayerList(state, players, true);
    }

    public static TurnAction jChoiceToTurnAction(JsonElement element) {
        String actionType;
        if (element.isJsonPrimitive()) {
            actionType = JsonConverterUtil.getAsString(element);
        } else {
            actionType = "place";
        }

        return switch (actionType) {
            case "pass" -> new PassAction();
            case "replace" -> new ExchangeAction();
            case "place" -> new PlaceAction(placementsFromJPlacements(element));
            default -> throw new IllegalArgumentException("Illegal TurnAction type");
        };
    }

    public static RefereeStateConfig parseRefereeStateConfig(JsonElement element) {
        // TODO validate args
        JsonObject obj = element.getAsJsonObject();

        int qbo = obj.get("qbo").getAsInt();
        int fbo = obj.get("fbo").getAsInt();

        return new RefereeStateConfig(qbo, fbo);
    }

}
