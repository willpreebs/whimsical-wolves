package qgame.server;

import java.io.PrintStream;

public enum DebugStream {
    ERROR(System.err),
    DEBUG(System.out);

    public PrintStream s;

    private DebugStream(PrintStream s) {
        this.s = s;
    }
}
