package qgame.referee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import qgame.json.JsonConverter;
import qgame.json.ObjectToJson;
import qgame.player.CheatingAIPlayer;
import qgame.player.DummyAIPlayer;
import qgame.player.LoopingAIPlayer;
import qgame.player.Player;
import qgame.player.SimpleAIPlayer;
import qgame.state.IGameState;

/**
 * Class used to generate integration tests for XGames and XGames-with-observer
 */
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

  private static String cheatToString(CheatingAIPlayer.Cheat cheat){
    return switch(cheat){
      case NONE -> throw new IllegalStateException();
      case NOT_ADJACENT -> "non-adjacent-coordinate";
      case NOT_OWNED -> "tile-not-owned";
      case NOT_INLINE -> "not-a-line";
      case NOT_ENOUGH_TILES -> "bad-ask-for-tiles";
      case NOT_LEGAL_NEIGHBOR-> "no-fit";
    };
  }
  private static JsonElement dummyPlayerToJActorSpec(DummyAIPlayer player) {
    JsonArray jActorSpec = new JsonArray();
    jActorSpec.add(player.name());
    jActorSpec.add(ObjectToJson.strategyToJson(player.strategy()));
    if (player.failStep() != DummyAIPlayer.FailStep.NONE) {
      jActorSpec.add(failStepToString(player.failStep()));
    }
    return jActorSpec;
  }

  private static JsonElement cheatingPlayertoJActorSpecA(CheatingAIPlayer player){
    JsonArray jActorSpec = new JsonArray();
    jActorSpec.add(player.name());
    jActorSpec.add(ObjectToJson.strategyToJson(player.strategy()));
    if (player.getCheat() != CheatingAIPlayer.Cheat.NONE) {
      jActorSpec.add(new JsonPrimitive("a cheat"));
      jActorSpec.add(cheatToString(player.getCheat()));
    }
    return jActorSpec;
  }

  private static JsonElement loopingPlayerToJActorSpecB(LoopingAIPlayer player){
    JsonArray jActorSpec = new JsonArray();
    jActorSpec.add(player.name());
    jActorSpec.add(ObjectToJson.strategyToJson(player.strategy()));
    if (player.failStep() != DummyAIPlayer.FailStep.NONE) {
      jActorSpec.add(failStepToString(player.failStep()));
      jActorSpec.add(player.getCountLimit());
    }
    return jActorSpec;
  }

  private static JsonElement simpleAIPlayerToJActorSpec(SimpleAIPlayer player){
    JsonArray jActorSpec = new JsonArray();
    jActorSpec.add(player.name());
    jActorSpec.add(ObjectToJson.strategyToJson(player.strategy()));
    return jActorSpec;
  }
  private static JsonElement playerToJActorSpec(Player player){
    if(player instanceof DummyAIPlayer){
      return dummyPlayerToJActorSpec((DummyAIPlayer)player);
    }
    else if (player instanceof LoopingAIPlayer){
      return loopingPlayerToJActorSpecB((LoopingAIPlayer)player);
    }
    else if(player instanceof SimpleAIPlayer){
      return simpleAIPlayerToJActorSpec((SimpleAIPlayer)player);
    }
    else if (player instanceof CheatingAIPlayer){
      return cheatingPlayertoJActorSpecA((CheatingAIPlayer)player);
    }
    else{
      throw new IllegalArgumentException("invalid player type");
    }
  }

  private static JsonElement playersToJActorsSpecs(List<Player> players){
    JsonArray jActors = new JsonArray();
    players
            .stream()
            .map(XGamesInputCreator::playerToJActorSpec)
            .forEach(jActors::add);
    return jActors;
  }



  public static boolean createHarnessTest(IGameState state, List<Player> players,
                                          GameResults results, String path, int num)
          throws IOException {
    JsonElement JState = ObjectToJson.jStateFromQGameState(state);
    JsonElement actors = playersToJActorsSpecs(players);
    return writeOutToJSON(JState, actors, results, path, num);
  }


  /**
   * Constructs an n-in.json file containing JState and JActor array, and an
   * n-out file containing a JGameResults.
   * @param jState serialized QGameState
   * @param jActors list of JActorSpecs
   * @param results QGameResults of the game that was played with those actors
   * @param path location to store files
   * @param num what number test
   * @return true if successful in writing out to JSON, false if not.
   * @throws IOException for invalid writing output to file.
   */
  private static boolean writeOutToJSON(JsonElement jState, JsonElement jActors,
                                        GameResults results,
                                        String path, int num) throws IOException {
    File in = new File(String.format("%s/%d-in.json", path, num));
    File out = new File(String.format("%s/%d-out.json", path, num));
    if (!in.exists() && !in.createNewFile()) {
      return false;
    }
    if (!out.exists() && !out.createNewFile()) {
      return false;
    }
    Writer inWrite = new FileWriter(in);
    inWrite.append(jState.toString()).append("\n");
    inWrite.append(jActors.toString()).append("\n");
    inWrite.flush();
    inWrite.close();

    Writer outWrite = new FileWriter(out);
    outWrite.append(ObjectToJson.jResultsFromGameResults(results).toString());
    outWrite.flush();
    outWrite.close();
    return true;
  }

}
