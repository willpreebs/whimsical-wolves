package qgame.player;

import java.util.ArrayList;
import java.util.List;

import qgame.action.ExchangeAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.state.IPlayerGameState;

/**
 * A player's whose only action is to always tell the ref
 * to exchange.
 */
public class AlwaysExchangePlayer implements MockPlayer{
  Bag<Tile> tiles;

  public AlwaysExchangePlayer(Bag<Tile> hand){
    tiles = hand;
  }

  @Override
  public String name(){
    return "Exchanger";
  };

  @Override
  public TurnAction takeTurn(IPlayerGameState ref){
    return new ExchangeAction();
  }

  @Override
  public void setup(IPlayerGameState state){

  }

  @Override
  public void newTiles(Bag<Tile> tiles){
    this.tiles = tiles;
  }

  @Override
  public void win(boolean w){

  }
  
  public List<Tile> returnHand(){
    return new ArrayList<>(this.tiles.getItems());
  }
}
