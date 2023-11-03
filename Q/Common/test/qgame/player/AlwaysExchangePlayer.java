package qgame.player;

import java.util.ArrayList;
import java.util.List;

import qgame.action.ExchangeAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.state.PlayerGameState;

/**
 * A player's whose only action is to always tell the ref
 * to exchange.
 */
public class AlwaysExchangePlayer implements MockPlayer{
  Bag<Tile> tiles;

  public AlwaysExchangePlayer(Bag<Tile> hand){
    tiles = hand;
  }

  public String name(){
    return "Exchanger";
  };

  public TurnAction takeTurn(PlayerGameState ref){
    return new ExchangeAction();
  }

  public void setup(QGameMap map, Bag<Tile> tiles){

  }

  public void newTiles(Bag<Tile> tiles){
    this.tiles = tiles;
  }

  public void win(boolean w){

  }

  public List<Tile> returnHand(){
    return new ArrayList<>(this.tiles.viewItems());
  }
}
