package qgame.server;

import java.io.IOException;

import org.junit.Test;

import qgame.harnesses.XServerClient;

public class XServerClientTest {
    

    @Test
    public void testXServerClient() {

        String [] serverArgs = {"xserver", "1234"};

        String [] clientArgs = {"xclient", "1234"};


        Thread server = new Thread(() -> {
            try {
                XServerClient.main(serverArgs);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });


        Thread client = new Thread(() -> {
            try {
                XServerClient.main(clientArgs);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

}
