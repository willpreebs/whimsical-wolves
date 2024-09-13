package qgame.server;

import com.google.gson.JsonObject;

import qgame.referee.RefereeConfig;

public class ServerConfig {
    
    private int port;
    private int serverTries;
    private int serverWait;
    private int waitForSignup;
    
    private boolean quiet;

    private RefereeConfig refSpec;

    public ServerConfig(JsonObject configElement) throws IllegalArgumentException {
        
        try {
            serverTries = configElement.get("server-tries").getAsInt();
            serverWait = configElement.get("server-wait").getAsInt();
            waitForSignup = configElement.get("wait-for-signup").getAsInt();
            quiet = configElement.get("quiet").getAsBoolean();
            JsonObject obj = configElement.get("ref-spec").getAsJsonObject();
            this.refSpec = new RefereeConfig(obj);
        } 
        catch (NullPointerException | IllegalStateException | NumberFormatException | UnsupportedOperationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Problem with configuration object");
        }
    }

    public int getPort() {
        return port;
    }

    public int getServerTries() {
        return serverTries;
    }

    public int getServerWait() {
        return serverWait;
    }

    public int getWaitForSignup() {
        return waitForSignup;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public RefereeConfig getRefSpec() {
        return refSpec;
    }
}
