package qgame.player;

import java.util.List;

import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.player.strategy.TurnStrategy;
import qgame.state.PlayerGameState;


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
  public TurnAction takeTurn(PlayerGameState gameState) throws IllegalStateException {
    return strat.chooseAction(gameState);
  }

  @Override
  public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
  }

  @Override
  public void win(boolean w) throws IllegalStateException {
  }

  @Override
  public void setup(QGameMap map, Bag<Tile> tiles) throws IllegalStateException {
  }


}
