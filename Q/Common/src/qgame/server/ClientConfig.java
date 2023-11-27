package qgame.server;

import java.util.List;

import com.google.gson.JsonObject;

import qgame.player.Player;

public class ClientConfig {
    
    private int port;
    private String host;
    private int wait;
    private boolean quiet;
    
    // private List<Player> players;

    public ClientConfig(JsonObject obj) throws IllegalArgumentException {
        try {
            port = obj.get("port").getAsInt();
            host = obj.get("host").getAsString();
            wait = obj.get("wait").getAsInt();
            quiet = obj.get("quiet").getAsBoolean();
        } 
        catch (NullPointerException | IllegalStateException | NumberFormatException | UnsupportedOperationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Problem with configuration object");
        }
    }


    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getWait() {
        return wait;
    }

    public boolean isQuiet() {
        return quiet;
    }

    // public List<Player> getPlayers() {
    //     return players;
    // }
}
