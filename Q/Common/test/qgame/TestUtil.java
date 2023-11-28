package qgame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.state.map.Tile;
import qgame.state.map.QTile;

public class TestUtil {

  public static Map<Tile.Color, Map<Tile.Shape, Tile>> allTiles() {
    Map<Tile.Color, Map<Tile.Shape, Tile>> allTiles = new HashMap<>();
    for (Tile.Color color : Tile.Color.values()) {
      allTiles.put(color, new HashMap<>());
      for (Tile.Shape shape : Tile.Shape.values()) {
        allTiles.get(color).put(shape, new QTile(color, shape));
      }
    }
    return allTiles;
  }

  public static List<Tile> generateOneEachTile(){
    List<Tile> list = new ArrayList<Tile>();
    for(Tile.Color c: Tile.Color.values()){
      for(Tile.Shape s: Tile.Shape.values()){
        list.add(new QTile(c,s));
      }
    }
    return list;
  }

  private static List<JsonElement> getJsonElementsFromFile(File f) throws FileNotFoundException {
      InputStream in = new FileInputStream(f);
      JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));

      List<JsonElement> elements = new ArrayList<>();

      while (parser.hasNext()) {
          elements.add(parser.next());
      }

      return elements;
  }

  public static List<JsonElement> getJsonTestElements(String directory, int testNum) throws FileNotFoundException {
      File f = new File(directory + testNum + "-in.json");
      return getJsonElementsFromFile(f);
  }

  public static List<JsonElement> getJsonServerConfig(String directory, int testNum) throws FileNotFoundException {
      File f = new File(directory + testNum + "-server-config.json");
      return getJsonElementsFromFile(f);
  }

  public static List<JsonElement> getJsonClientConfig(String directory, int testNum) throws FileNotFoundException {
      File f = new File(directory + testNum + "-client-config.json");
      return getJsonElementsFromFile(f);
  }


  public static JsonElement getJsonTestResult(String directory, int testNum) throws FileNotFoundException {
      File f = new File(directory + testNum + "-out.json");
      return getJsonElementsFromFile(f).get(0);
  }
}
