package qgame.referee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.player.DummyAIPlayer;
import qgame.player.Player;
import qgame.state.IGameState;

class XGamesInputCreator {

  private static String failStepToString(DummyAIPlayer.FailStep step) {
    return switch (step) {
      case NONE -> throw new IllegalStateException();
      case SETUP -> "setup";
      case TAKE_TURN -> "take-turn";
      case NEW_TILES -> "new-tiles";
      case WIN -> "win";
    };
  }
  private static JsonElement dummyPlayerToJActorSpec(DummyAIPlayer player) {
    JsonArray jActorSpec = new JsonArray();
    jActorSpec.add(player.name());
    jActorSpec.add(JsonConverter.strategyToJson(player.strategy()));
    if (player.failStep() != DummyAIPlayer.FailStep.NONE) {
      jActorSpec.add(failStepToString(player.failStep()));
    }
    return jActorSpec;
  }
  private static JsonElement dummyPlayersToJActors(List<DummyAIPlayer> players) {
    JsonArray jActors = new JsonArray();
    players
      .stream()
      .map(XGamesInputCreator::dummyPlayerToJActorSpec)
      .forEach(jActors::add);
    return jActors;
  }

  public static boolean createTest(IGameState state, List<Player> players, GameResults results,
                                String path, int num) throws IOException {
    JsonElement JState = JsonConverter.jStateFromQGameState(state);

    List<DummyAIPlayer> dummies = players
      .stream()
      .map(player -> (DummyAIPlayer)player)
      .toList();
    JsonElement actors = dummyPlayersToJActors(dummies);

    File in = new File(String.format("%s/%d-in.json", path, num));
    File out = new File(String.format("%s/%d-out.json", path, num));
    if (!in.exists() && !in.createNewFile()) {
      return false;
    }
    if (!out.exists() && !out.createNewFile()) {
      return false;
    }
    Writer inWrite = new FileWriter(in);
    inWrite.append(JState.toString()).append("\n");
    inWrite.append(actors.toString()).append("\n");
    inWrite.flush();
    inWrite.close();

    Writer outWrite = new FileWriter(out);
    outWrite.append(JsonConverter.jResultsFromGameResults(results).toString());
    outWrite.flush();
    outWrite.close();
    return true;
  }
}
