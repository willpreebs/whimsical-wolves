package qgame.server;

import java.io.IOException;
import java.net.ServerSocket;

import qgame.player.SimpleAIPlayer;
import qgame.player.strategy.DagStrategy;
import qgame.player.strategy.LdasgStrategy;
import qgame.util.RuleUtil;

public class SampleServerClient {
    
    public static void main(String[] args) throws IOException {
        Server s = new Server(1234);
        ServerSocket server = s.getServer();
        SimpleAIPlayer player1 = new SimpleAIPlayer("player1", new DagStrategy(RuleUtil.createPlaceRules()));
        SimpleAIPlayer player2 = new SimpleAIPlayer("player2", new LdasgStrategy(RuleUtil.createPlaceRules()));
        SimpleAIPlayer player3 = new SimpleAIPlayer("player3", new LdasgStrategy(RuleUtil.createPlaceRules()));
        SimpleAIPlayer player4 = new SimpleAIPlayer("player4", new LdasgStrategy(RuleUtil.createPlaceRules()));
        
        Client c1 = new Client(server, player1);
        Client c2 = new Client(server, player2);
        Client c3 = new Client(server, player3);
        Client c4 = new Client(server, player4);
        c1.startConnection();
        c2.startConnection();
        c3.startConnection();
        c4.startConnection();

        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        Thread t3 = new Thread(c3);
        Thread t4 = new Thread(c4);
        // Thread serverThread = new Thread(s);
        // serverThread.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        s.run();
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();
        c1.startListeningToMessages();
        c2.startListeningToMessages();
        c3.startListeningToMessages();
        c4.startListeningToMessages();
        // System.out.println("Client1 received: " + c1.sendMessage("hi1"));
        // System.out.println("Client2 received: " + c2.sendMessage("hi2"));
        // System.out.println("Client3 received: " + c3.sendMessage("hi3"));
        // System.out.println("Client4 received: " + c4.sendMessage("hi4"));

        
    }
}
