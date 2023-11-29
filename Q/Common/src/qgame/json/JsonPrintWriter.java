package qgame.json;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;

public class JsonPrintWriter {
    
    PrintWriter out;

    public JsonPrintWriter(PrintWriter out) {
        this.out = out;
    }

    /**
     * Sends the given JsonElement
     * 
     * throws IOException if there is a problem with the stream
     */
    public void sendJson(JsonElement e) throws IOException {
        out.println(e);
    }

}
