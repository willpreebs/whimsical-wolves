package qgame.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.player.Player;

public class Client implements Runnable {
// public class Client {

    private Socket socket;
    // private RefereeProxy refereeProxy;
    private Player player;
    // private String name;
    private ServerSocket server;

    BufferedReader in;
    PrintWriter out;

    RefereeProxy refProxy;


    
    public Client(ServerSocket server, Player player) throws IOException {
        this.server = server;
        this.player = player;  
    }

    public void startListeningToMessages() throws IOException {
        refProxy.listenForMessages();
    }
    
    public void startConnection() throws IOException {
        socket = new Socket(server.getInetAddress(), server.getLocalPort());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // public Socket getSocket() {
    //     return this.socket;
    // }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    @Override
    public void run() {
        System.out.println("Client running on thread: " + Thread.currentThread().threadId());
        System.out.println("Client thread name: " + Thread.currentThread().getName());
        // Socket socket = new Socket(server.getInetAddress(), server.getLocalPort());
        // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // out = new PrintWriter(socket.getOutputStream(), true);
        
        this.refProxy = new RefereeProxy(socket, player);
        System.out.println("Writing player name");
        out.println(new JsonPrimitive(this.player.name()));

        while (true) {
            try {
				System.out.println("Client: read line: " + in.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        // try {
        //     this.refProxy.listenForMessages();
        // } catch (IOException e) {
        //     throw new IllegalStateException(e);
        // }
    
        // try {
        //     OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        //     System.out.println("Client writing: " + this.player.name());
        //     writer.write("\"" + this.player.name() + "\"");
        //     refereeProxy.listenForMessages();
        // } catch (IOException e) {
        //     throw new IllegalStateException("Client IOException: " + e.getMessage());
        // }
    }

    // private boolean getConnectedMessage(Socket socket) {
    //     try {
    //         // JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
    //         // InputStreamReader r = new InputStreamReader(socket.getInputStream());
    //         BufferedInputStream b = new BufferedInputStream(socket.getInputStream());
    //         BufferedReader r = new BufferedReader(new InputStreamReader(b));
    //         System.out.println("Client: Trying to read connected message");
    //         String connectedMessage = r.readLine();
    //         if (connectedMessage.equals("connected")) {
    //             System.out.println("Client: read connected message");
    //             return true;
    //         }
    //         else {
    //             System.out.println("Client: Connected message: " + connectedMessage);
    //         }
    //         return false;
    //     } catch (IOException e) {
    //         throw new IllegalArgumentException("Client IOException while looking for connected message");
    //     }
    // }
}
