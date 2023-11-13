package qgame.server;

import java.io.IOException;
import java.net.ServerSocket;

public class SampleServerClient {
    

    public static void main(String[] args) throws IOException {
        Server s = new Server(1234);
        ServerSocket socket = s.getServer();
        s.getClientsAndRunGame();
        Client c1 = new Client(socket);
        c1.sendPlayerName("player1");
        Client c2 = new Client(socket);
        c2.sendPlayerName("player2");
    }
    
}
