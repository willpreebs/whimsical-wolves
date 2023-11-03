package qgame.player.strategy;

import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.state.PlayerGameState;


class XStrategyInputCreator {

  public static boolean createInput(PlayerGameState state, TurnStrategy strategy,
                                 TurnAction expected, String path, int num) throws IOException{
    JsonElement jPub = JsonConverter.playerStateToJPub(state);
    JsonElement strat = JsonConverter.strategyToJson(strategy);
    JsonElement result = JsonConverter.actionToJson(expected);
    File in = new File(String.format("%s/%d-in.json", path, num));
    File out = new File(String.format("%s/%d-out.json", path, num));
    if (!in.exists() && !in.createNewFile()) {
      return false;
    }
    if (!out.exists() && !out.createNewFile()) {
      return false;
    }
    Writer inWrite = new FileWriter(in);
    inWrite.append(strat.toString()).append("\n");
    inWrite.append(jPub.toString()).append("\n");
    inWrite.flush();
    inWrite.close();

    Writer outWrite = new FileWriter(out);
    outWrite.append(result.toString());
    outWrite.flush();
    outWrite.close();
    return true;
  }
}
