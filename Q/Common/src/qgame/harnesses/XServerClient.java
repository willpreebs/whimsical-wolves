package qgame.harnesses;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.server.Client;
import qgame.server.Server;

public class XServerClient {

    public static void main (String[] args) throws IOException {

        if (args.length < 2) {
            throw new IllegalArgumentException("Must specify port");
        }

        int port = getPort(args[1]);
        
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonElement config = parser.next();

        

        switch (args[0]) {
            case "xserver":
                Server s = JsonConverter.parseServerConfig(port, config);
                runServer(s);
                break;
            case "xclient":
                Client c = new Client(null, null)
        }

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

    private static void runServer(Server s) {

        s.run();

    }
}
