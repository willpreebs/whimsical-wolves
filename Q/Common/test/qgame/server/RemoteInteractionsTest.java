package qgame.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonStreamParser;

import qgame.player.Player;
import qgame.player.QPlayer;
import qgame.player.SimpleAIPlayer;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.util.RuleUtil;

public class RemoteInteractionsTest {

    Server s;

    @Before
    public void setupServer() throws IOException {
        s = new Server(1234);
    }

    public void startServer() throws IOException {
        Thread t = new Thread(s);
        t.start();
    }

    private MockRefereeProxy makeMockRefProxy(RefereeProxy rp) {
        return new MockRefereeProxy(rp.getOut(), rp.getParser(), rp.getPlayer());
    }

    private PlayerProxy makePlayerProxy(ServerSocket s, String playerName) {

        Socket socket;
        PrintWriter out = null;
        JsonStreamParser parser = null;

        try {
            socket = s.accept();
            out = new PrintWriter(socket.getOutputStream(), true);
            parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // Problem connecting to client, try again
            fail();
        }

       return new PlayerProxy(playerName, parser, out);
    }

    @Test
    public void testClientSupplyPlayerName() {

        String playerName = "testPlayer";

        Player p = new SimpleAIPlayer(playerName, new DagStrategy(RuleUtil.createPlaceRules()));

        Client c = null;
        try {
            c = new Client(s.getServer(), p);
        } catch (IOException e) {
            fail();
        }

        List<Player> proxies = new ArrayList<>();

        Thread t = new Thread(() -> s.getPlayerProxiesWithinTimeout(proxies));
    
        c.sendPlayerName();
        t.start();

        List<Player> expectedProxies = new ArrayList<>();
        expectedProxies.add(new QPlayer(playerName));

        assertEquals(expectedProxies, expectedProxies);
    }

    @Test
    public void testSendEmptyGameResult() throws IOException {

        String playerName = "testPlayer";

        Client c = new Client(s.getServer(), new QPlayer(playerName));

        ServerSocket server = s.getServer();

        PlayerProxy p = makePlayerProxy(server, playerName);
        RefereeProxy rp = c.getRefereeProxy();
        MockRefereeProxy r = makeMockRefProxy(rp);

        Thread t = new Thread(() -> r.listenForMessages());
        t.start();

        s.sendEmptyGameResult(List.of(p));
        // wait a second for message to transmit
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        t.interrupt();

        List<String> expected = List.of("[[],[]]");
        assertEquals(expected, r.getIncomingMessages());
    }

    // @Test
    // public void testGetOneProxy() throws IOException {
    //     Client c1 = new Client(s.getServer(), new SimpleAIPlayer("testplayer1", new DagStrategy(RuleUtil.createPlaceRules())));
    //     new Thread(c1).start();
    //     s.run();
    // }

    @Test
    public void testGetTwoProxies() throws IOException {

        Client c1 = new Client(s.getServer(), new SimpleAIPlayer("testplayer1", new DagStrategy(RuleUtil.createPlaceRules())));
        Client c2 = new Client(s.getServer(), new SimpleAIPlayer("testplayer2", new LdasgStrategy(RuleUtil.createPlaceRules())));

        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);

        t1.start();
        t2.start();

        List<Player> proxies = s.getProxies();
        
        List<Player> expected1 = List.of(new QPlayer("testplayer1"), new QPlayer("testplayer2"));
        List<Player> expected2 = List.of(new QPlayer("testplayer2"), new QPlayer("testplayer1"));

        try {
            assertEquals(expected1, proxies);
        } catch (AssertionError e) {
            e.printStackTrace();
            assertEquals(expected2, proxies);
        }
    }
}