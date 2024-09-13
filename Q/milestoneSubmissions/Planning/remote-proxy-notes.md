1. Define logical interactions
2. Create interfaces for actors


    Flipper                         Player
        |      ---guess->              |
        |      <--heads or tails--     |
        |     ---win/lose-->           |
        |                              |
        |                              |
        |                              |


IPlayer:
- HorT guess()
- void notify(Boolean win)

IFlipper:
- void playGame(IPlayer p)

Idea of remote-proxy:

- Client: impl of Player
- Server: impl of Flipper

Introduce proxies for both components

- ProxyPlayer impl IPlayer

Flipper     Proxy Player   Proxy Flipper    Player
    |  -guess-> |   ...comm..   |   -guess->   |
    |  <-hOrT-- |   ...comm..   |   <-hOrT--   |
    | -notify-> |               |   -notify->  |
    |           |               |              |
    |           |               |              |
    |           |               |              |
    |           |               |              |
    |           |               |              |
    |           |               |              |
    |           |               |              |

class ProxyPlayer impl IPlayer {
    Connection c
    HorT guess() {
        c.send("guess") // call guess method in a json format (somehow)
        response = c.receive()
        return deserializedHorT(response)
    }

    void notify(boolean win) {
        //...
    }
}

class ProxyFlipper {
    Connection c
    IPlayer p
    void listen() {
        // shutdown condition. How to shutdown things cleanly?
        while (true) {
            msg = c.receive()
            if (msg is guess) {
                response = p.guess()
                c.send(response.asJson())
            }
        }
    }
}


