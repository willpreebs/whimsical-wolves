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
        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        Thread t3 = new Thread(c3);
        Thread t4 = new Thread(c4);
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        s.run();
    }
}
