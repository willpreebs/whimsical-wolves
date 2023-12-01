package qgame.integration;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import qgame.TestUtil;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.QReferee;
import qgame.server.Client;
import qgame.server.ClientConfig;
import qgame.server.Server;
import qgame.server.ServerConfig;
import qgame.state.IGameState;

public class TestXServerClient {
    

    @Test
    public void runIndividualTest() throws IOException {
        performTest("Tests", 0);
    }
    
    public JsonElement getGameResults(String directory, int testNum) throws IOException {

        // doesnt matter, as long as clients and server both connect to the same
        int port = 1234;


        JsonObject serverConfig = TestUtil.getJsonServerConfig(directory, testNum).get(0).getAsJsonObject();
        JsonObject clientConfig = TestUtil.getJsonClientConfig(directory, testNum).get(0).getAsJsonObject();
        
        ServerConfig sConfig = new ServerConfig(serverConfig);
        ClientConfig cConfig = new ClientConfig(clientConfig);

        List<Player> players = JsonConverter.playersFromJActorSpecB(clientConfig.get("players"));

        
        startXClients(port, cConfig, players);
        JsonElement testResults = startXServer(port, sConfig);

        return testResults;
    }

    private static JsonElement startXServer(int port, ServerConfig config) throws IOException {
        Server s = new Server(port, config);

        File f = new File("Testresults");

        PrintStream stream = new PrintStream(f);
        s.setResultStream(stream);

        s.run();

        JsonStreamParser fReader = new JsonStreamParser(new FileReader(f));
        JsonElement result = fReader.next();
       
        return result;
    }

    private static void startXClients(int port, ClientConfig config, List<Player> players) {
        List<Thread> threads = getClientThreads(port, config, players);
        runClients(threads);

        
    }

    private static void runClients(List<Thread> threads) {
        threads.forEach(t -> t.start());
    }

    private static List<Thread> getClientThreads(int port, ClientConfig config, List<Player> players) {
        List<Client> clients = getClients(players, port, config);

        List<Thread> clientThreads = clients
        .stream()
        .map(client -> new Thread(client))
        .toList();

        return clientThreads;
    }

    private static List<Client> getClients(List<Player> players, int port, ClientConfig config) {

        List<Client> clients = new ArrayList<>();

        for (Player p : players) {

            try {
                clients.add(new Client(port, config, p));
            } catch (IOException e) {
                // issue with creating socket for Client
                continue;
            }
        }

        return clients;
    }

    public List<String> getNamesFromJsonArray(JsonArray a) {
        return a.asList()
        .stream()
        .map(e -> e.getAsString())
        .toList();
    }

    public void performTest(String dir, int testNum) throws IOException {

        
        String directory = "10/" + dir + "/";

        JsonElement actual = getGameResults(directory, testNum);

        JsonElement expected = TestUtil.getJsonTestResult(directory, testNum);
        
        JsonArray expectedWinners = expected.getAsJsonArray().get(0).getAsJsonArray();
        JsonArray expectedCheaters = expected.getAsJsonArray().get(1).getAsJsonArray();

        JsonArray actualWinners = actual.getAsJsonArray().get(0).getAsJsonArray();
        JsonArray actualCheaters = actual.getAsJsonArray().get(1).getAsJsonArray();

        
        List<String> actualWinnerNames = getNamesFromJsonArray(actualWinners);
        List<String> actualCheaterNames = getNamesFromJsonArray(actualCheaters);

        List<String> expectedWinnerNames = getNamesFromJsonArray(expectedWinners);
        List<String> expectedCheaterNames = getNamesFromJsonArray(expectedCheaters);

        assertEquals(expectedWinnerNames, actualWinnerNames);
        assertEquals(expectedCheaterNames, actualCheaterNames);
    }

}
