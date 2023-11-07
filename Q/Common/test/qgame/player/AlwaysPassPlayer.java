package qgame.player;
import java.util.List;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.state.IPlayerGameState;

/**
 * A player's whose only action told to the ref is to
 * pass.
 */
public class AlwaysPassPlayer implements MockPlayer{
  Bag<Tile> tiles;

  public AlwaysPassPlayer(List<Tile> hand){
    tiles = new Bag<>(hand);
  }

  public String name(){
    return "Passer";
  };

  public TurnAction takeTurn(IPlayerGameState ref){
    return new PassAction();
  }

  public void setup(IMap map, Bag<Tile> tiles){

  }

  public void newTiles(Bag<Tile> tiles){
    this.tiles = tiles;
  }

  public void win(boolean w){

  }

  public List<Tile> returnHand(){
//    return this.tiles;
    return null;
  }

}
