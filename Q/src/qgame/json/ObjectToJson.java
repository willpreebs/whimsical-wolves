package qgame.json;

import static qgame.util.ValidationUtil.nonNullObj;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import qgame.action.ExchangeAction;
import qgame.action.PassAction;
import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.player.PlayerInfo;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.player.strategy.TurnStrategy;
import qgame.referee.GameResults;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.QMap;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;

public class ObjectToJson {

    public static JsonElement jMapFromQGameMap(QMap map) {
        SortedMap<Integer, SortedMap<Integer, Tile>> rowMap = create2DMapFromState(map.getBoardState());
        return createJMapFrom2DMap(rowMap);
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

    private static JsonObject jCoordFromPosn(Posn posn) {
        JsonObject obj = new JsonObject();
        obj.add("column", new JsonPrimitive(posn.x()));
        obj.add("row", new JsonPrimitive(posn.y()));
        return obj;
    }

    /**
     * Takes in a list of positions and returns a corresponding list of
     * jCoordinates.
     * Sorted in row-column order because of XMap.
     * 
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

    private static JsonElement jPlayerFromPlayerState(IPlayerGameState state) {
        JsonObject player = new JsonObject();
        player.add("score", new JsonPrimitive(state.getPlayerScores().get(0)));
        player.add("tile*", jTilesFromTiles(state.getCurrentPlayerTiles().getItems()));
        return player;
    }

    public static JsonElement playerStateToJPub(IPlayerGameState state) {
        JsonObject jPub = new JsonObject();
        jPub.add("map", jMapFromQGameMap(state.getBoard()));
        jPub.add("tile*", new JsonPrimitive(state.getNumberRemainingTiles()));
        JsonArray arr = new JsonArray();
        arr.add(jPlayerFromPlayerState(state));
        state
                .getPlayerScores()
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

    public static JsonArray jPlacementsFromPlaceAction(PlaceAction action) {
        JsonArray a = new JsonArray();
        for (Placement p : action.placements()) {
            a.add(onePlacementFromPlacement(p));
        }
        return a;
    }

    public static JsonElement actionToJson(TurnAction action) {
        return switch (action) {
            case PassAction pass -> new JsonPrimitive("pass");
            case ExchangeAction exchangeAction -> new JsonPrimitive("replace");
            case PlaceAction place -> onePlacementFromPlacement(place.placements().get(0));
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }

    public static JsonElement actionToJChoice(TurnAction action) {
        return switch (action) {
            case PassAction pass -> new JsonPrimitive("pass");
            case ExchangeAction exchangeAction -> new JsonPrimitive("replace");
            case PlaceAction place -> jPlacementsFromPlaceAction(place);
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }

    public static JsonElement jResultsFromGameResults(GameResults results) {
        List<String> winners = results.getWinners();
        List<String> ruleBreakers = results.getRuleBreakers();
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
        object.add("score", new JsonPrimitive(info.getScore()));
        object.add("name", new JsonPrimitive(info.getName()));
        object.add("tile*", jTilesFromTiles(info.getTiles().getItems()));
        return object;
    }

    public static JsonElement jStateFromQGameState(IGameState state) {
        JsonElement map = jMapFromQGameMap(state.getBoard());
        JsonElement tiles = jTilesFromTiles(state.getRefereeTiles().getItems());
        JsonArray players = new JsonArray();
        state.getAllPlayerInformation()
                .stream()
                .map(ObjectToJson::jPlayerFromPlayerInfo)
                .forEach(players::add);
        JsonObject result = new JsonObject();
        result.add("map", map);
        result.add("tile*", tiles);
        result.add("players", players);
        return result;
    }

}
