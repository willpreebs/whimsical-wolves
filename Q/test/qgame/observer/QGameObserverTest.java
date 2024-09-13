package qgame.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import qgame.player.PlayerInfo;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.state.QGameState;
import qgame.state.QStateBuilder;
import qgame.state.map.Posn;
import qgame.state.map.QTile;
import qgame.state.map.Tile;

public class QGameObserverTest {

    Tile.Color red = Tile.Color.RED;
    Tile.Color blue = Tile.Color.BLUE;
    Tile.Color green = Tile.Color.GREEN;
    Tile.Color yellow = Tile.Color.YELLOW;
    Tile.Color orange = Tile.Color.ORANGE;
    Tile.Color purple = Tile.Color.PURPLE;

    Tile.Shape square = Tile.Shape.SQUARE;
    Tile.Shape circle = Tile.Shape.CIRCLE;
    Tile.Shape diamond = Tile.Shape.DIAMOND;
    Tile.Shape clover = Tile.Shape.CLOVER;
    Tile.Shape star = Tile.Shape.STAR;
    Tile.Shape eightStar = Tile.Shape.EIGHT_STAR;
    
    @Test
    public void testGameObserver() throws InterruptedException {
        QGameObserver o = new QGameObserver();
    
        IGameState state =
              new QStateBuilder()
        .addTileBag(new QTile(blue, eightStar))
        .placeTile(new Posn(0, 0), new QTile(red, clover))
        .addPlayerInfo(new PlayerInfo(1, List.of(new QTile(green, square)), "P1"))
        .addPlayerInfo(new PlayerInfo(5, List.of(new QTile(yellow, star)), "P2"))
        .build();

        o.receiveState(state);   

        state.placeTile(new Placement(new Posn(0, 1), new QTile(blue, clover)));

        o.receiveState(state);


        Thread.sleep(10000);
    }

    @Test
    public void testReceiveState() {

        QGameObserver ob = new QGameObserver();

        IGameState state =
              new QStateBuilder()
        .addTileBag(new QTile(blue, eightStar))
        .placeTile(new Posn(0, 0), new QTile(red, clover))
        .addPlayerInfo(new PlayerInfo(1, List.of(new QTile(green, square)), "P1"))
        .addPlayerInfo(new PlayerInfo(5, List.of(new QTile(yellow, star)), "P2"))
        .build();

        ob.receiveState(state);
        assertEquals(1, ob.getStates().size());

        File f1 = new File(ob.getFilePath(1));
        assertTrue(f1.isFile());

        state.placeTile(new Placement(new Posn(0, 1), new QTile(blue, clover)));

        ob.receiveState(state);
        assertEquals(2, ob.getStates().size());

        File f2 = new File(ob.getFilePath(1));
        assertTrue(f2.isFile());
    }

    @Test
    public void testNext() {
        QGameObserver ob = new QGameObserver();

        IGameState state =
              new QStateBuilder()
        .addTileBag(new QTile(blue, eightStar))
        .placeTile(new Posn(0, 0), new QTile(red, clover))
        .addPlayerInfo(new PlayerInfo(1, List.of(new QTile(green, square)), "P1"))
        .addPlayerInfo(new PlayerInfo(5, List.of(new QTile(yellow, star)), "P2"))
        .build();

        IGameState firstStateCopy = new QGameState(state);

        ob.receiveState(state);
        assertEquals(1, ob.getStates().size());

        state.placeTile(new Placement(new Posn(0, 1), new QTile(blue, clover)));

        ob.receiveState(state);

        ob.next();

        assertEquals(firstStateCopy, ob.getCurrentState());
    }

}
