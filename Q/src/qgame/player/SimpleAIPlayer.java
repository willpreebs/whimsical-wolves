package qgame.player;

import qgame.action.TurnAction;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;


public class SimpleAIPlayer implements Player {

  private final String name;
  private final TurnStrategy strat;

  public SimpleAIPlayer(String name, TurnStrategy strat){
    this.name = name;
    this.strat = strat;
  }
  public String name() {
    return this.name;
  }

  @Override
  public TurnAction takeTurn(IPlayerGameState gameState) throws IllegalStateException {
    return strat.chooseAction(gameState);
  }

  @Override
  public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
  }

  @Override
  public void win(boolean w) throws IllegalStateException {
  }

  @Override
  public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
  }

  @Override
  public boolean equals(Object o) {
      if (o instanceof Player) {
          return this.name().equals(((Player) o).name());
      }
      else {
          return false;
      }
  }

  public TurnStrategy strategy(){
    return this.strat;
  }

}
