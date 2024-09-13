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

}
