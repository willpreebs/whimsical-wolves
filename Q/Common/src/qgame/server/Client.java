package qgame.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import qgame.player.Player;

public class Client {

    private Socket socket;
    
    public Client(ServerSocket server) throws IOException {
        this.socket = server.accept();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void sendPlayerName(String jName) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write("\"" + jName + "\"");
    }
}
