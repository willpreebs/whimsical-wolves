package qgame.json;

import static qgame.util.ValidationUtil.validateArg;

import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import qgame.state.map.Posn;
import qgame.state.map.Tile;

public class JsonConverterUtil {
    
    public static int getAsInt(JsonElement element) {
    validateArg(JsonElement::isJsonPrimitive, element, "Must be number");
    JsonPrimitive prim = element.getAsJsonPrimitive();
    validateArg(JsonPrimitive::isNumber, prim, "Must be number");
    return prim.getAsInt();
  }

  public static String getAsString(JsonElement element) {
    validateArg(JsonElement::isJsonPrimitive, element, "Must be string");
    JsonPrimitive prim = element.getAsJsonPrimitive();
    validateArg(JsonPrimitive::isString, prim, "Must be string");
    return prim.getAsString();
  }

  public static JsonArray getAsArray(JsonElement element) {
    validateArg(JsonElement::isJsonArray, element, "Must be json array");
    return element.getAsJsonArray();
  }

  public static JsonElement[] getAsElementArray(JsonElement element) {
    JsonArray arr = getAsArray(element);
    JsonElement[] res = new JsonElement[arr.size()];
    for (int i = 0; i < arr.size(); i++) {
      res[i] = arr.get(i);
    }
    return res;
  }

  private static void updateMapFromJCellAndRow(Map<Posn, Tile> map, JsonElement element, int yVal) {
    JsonArray jCell = element.getAsJsonArray();
    int xVal = getAsInt(jCell.get(0));
    map.put(new Posn(yVal, xVal), JsonToObject.tileFromJTile(jCell.get(1)));
  }

  public static void updateRowFromJRow(Map<Posn, Tile> map, JsonElement element) {
    JsonElement[] arr = getAsElementArray(element);
    int yVal = getAsInt(arr[0]);
    Stream.of(arr)
      .skip(1)
      .forEach(ele -> updateMapFromJCellAndRow(map, ele, yVal));
  }


}
