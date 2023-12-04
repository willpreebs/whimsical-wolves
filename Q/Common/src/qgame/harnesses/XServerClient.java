package qgame.harnesses;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.server.Client;
import qgame.server.ClientConfig;
import qgame.server.Server;
import qgame.server.ServerConfig;

public class XServerClient {

    public static void main (String[] args) throws IOException {

        if (args.length < 2) {
            throw new IllegalArgumentException("Must specify port");
        }

        int port = getPort(args[1]);
        
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonObject config = parser.next().getAsJsonObject();

        switch (args[0]) {
            case "xserver":
                handleXServer(port, config);
                break;
            case "xclients":
                handleXClient(port, config);
                break;
        }
    }

    private static void handleXServer(int port, JsonObject configObj) throws IOException {
        Server s = new Server(port, new ServerConfig(configObj));
        Thread thread = new Thread(s);
        runServer(thread);
    }

    private static void handleXClient(int port, JsonObject configObj) throws IOException {
        List<Player> players = JsonConverter.playersFromJActorSpecB(configObj.get("players"));
        ClientConfig config = new ClientConfig(configObj);
        List<Thread> threads = getClientThreads(port, config, players);
        runClients(threads, config);
    }

    private static int getPort(String arg) {
        try {
            int port = Integer.parseInt(arg);
            assertTrue(port >= 0 && port <= 65535);
            return port;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Port must be a number");
        } catch (AssertionError e) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
    }

    private static List<Thread> getClientThreads(int port, ClientConfig config, List<Player> players) throws IOException { 

        
        List<Client> clients = getClients(players, port, config);

        List<Thread> clientThreads = clients
        .stream()
        .map(client -> new Thread(client))
        .toList();

        return clientThreads;
    }

    private static List<Client> getClients(List<Player> players, int port, ClientConfig config) throws IOException {

        List<Client> clients = new ArrayList<>();

        for (Player p : players) {
            clients.add(new Client(port, config, p));
        }

        return clients;
    }

    private static void runClients(List<Thread> threads, ClientConfig config) {

        int millisBetweenThreadStarts = config.getWait() * 1000;

        for (Thread t : threads) {
            //System.out.println("starting client thread");
            t.start();
            try {
                Thread.sleep(millisBetweenThreadStarts);
            } catch (InterruptedException e) {
                System.err.println("Sleep period between Client threads interrupted");
            }
        } 
    }

    private static void runServer(Thread serverThread) {
        serverThread.start();
    }
}
