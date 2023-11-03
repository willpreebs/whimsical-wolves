package qgame.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.List;


import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;
import static qgame.json.JsonConverter.tileFromJTile;
import static qgame.json.JsonConverter.jCoordsFromPosns;

import static org.junit.Assert.*;

public class JsonConverterTest {

  private JsonElement jsonTile1;
  private JsonElement jsonTile2;
  private JsonElement jsonTile3;
  private JsonElement jsonTile4;
  private JsonElement jsonTile5;
  private JsonElement jsonTile6;
  private Tile tile1;
  private Tile tile2;
  private Tile tile3;
  private Tile tile4;
  private Tile tile5;
  private Tile tile6;

  @Before
  public void init() {
    Gson gson = new Gson();
    jsonTile1 = gson.fromJson("{\"shape\": \"circle\", \"color\": \"orange\"}", JsonElement.class);
    jsonTile2 = gson.fromJson("{\"color\": \"blue\", \"shape\": \"square\"}", JsonElement.class);
    jsonTile3 = gson.fromJson("{\"shape\": \"clover\", \"color\": \"purple\"}", JsonElement.class);
    jsonTile4 = gson.fromJson("{\"shape\": \"diamond\", \"color\": \"green\"}", JsonElement.class);
    jsonTile5 = gson.fromJson("{\"color\": \"yellow\", \"shape\": \"star\"}", JsonElement.class);
    jsonTile6 = gson.fromJson("{\"shape\": \"8star\", \"color\": \"red\"}", JsonElement.class);
    tile1 = new TileImpl(Tile.Color.ORANGE, Tile.Shape.CIRCLE);
    tile2 = new TileImpl(Tile.Color.BLUE, Tile.Shape.SQUARE);
    tile3 = new TileImpl(Tile.Color.PURPLE, Tile.Shape.CLOVER);
    tile4 = new TileImpl(Tile.Color.GREEN, Tile.Shape.DIAMOND);
    tile5 = new TileImpl(Tile.Color.YELLOW, Tile.Shape.STAR);
    tile6 = new TileImpl(Tile.Color.RED, Tile.Shape.EIGHT_STAR);
  }

  private Posn posn(int y, int x) {
    return new Posn(y, x);
  }
  @Test
  public void qGameMapFromJMap() {
  }

  @Test
  public void tileFromJTileTest1() {
    assertEquals(tile1, tileFromJTile(jsonTile1));
    assertEquals(tile2, tileFromJTile(jsonTile2));
    assertEquals(tile3, tileFromJTile(jsonTile3));
    assertEquals(tile4, tileFromJTile(jsonTile4));
    assertEquals(tile5, tileFromJTile(jsonTile5));
    assertEquals(tile6, tileFromJTile(jsonTile6));
  }


  // This jcoordsFromPosns sorts the jCoords, so expected checks that the coordinates are in a
  // given order.
  public void testJcoordsOnList(List<Posn> posns, List<Posn> expected) {
    JsonArray array = jCoordsFromPosns(posns);
    assertEquals(expected.size(), array.size());
    for (int i = 0; i < expected.size(); i++) {
      JsonElement element = array.get(i);
      assertTrue(element.isJsonObject());
      JsonObject obj = element.getAsJsonObject();
      assertEquals(2, obj.size());
      assertTrue(obj.has("row"));
      assertTrue(obj.has("column"));
      assertEquals(expected.get(i).y(), obj.get("row").getAsInt());
      assertEquals(expected.get(i).x(), obj.get("column").getAsInt());

    }
  }

  @Test
  public void testJCoordsFromPosns1() {
    List<Posn> posns = List.of(posn(0, 1), posn(5, 3), posn(7, 9));
    testJcoordsOnList(posns, posns);
  }

  @Test
  public void testJCoordsFromPosns2() {
    List<Posn> posns = List.of(posn(0, 1), posn(5, 3), posn(7, 9), posn(1, 1),
                              posn(1, 2), posn(5, 5), posn(3, 4));
    List<Posn> expected = List.of(posn(0, 1), posn(1, 1), posn(1, 2),
      posn(3, 4), posn(5, 3), posn(5, 5), posn(7, 9));
    testJcoordsOnList(posns, expected);
  }
}