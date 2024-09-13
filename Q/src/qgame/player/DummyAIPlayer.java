package qgame.player;

import qgame.action.TurnAction;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

public class DummyAIPlayer implements Player{

  public enum FailStep {NONE, SETUP, TAKE_TURN, NEW_TILES, WIN}

  private final SimpleAIPlayer player;
  private final FailStep failStep;

  public DummyAIPlayer(String name, TurnStrategy strat) {
    this(name, strat, FailStep.NONE);
  }

  public DummyAIPlayer(String name, TurnStrategy strat, FailStep step) {
    player = new SimpleAIPlayer(name, strat);
    failStep = step;
  }


  private void failIfStep(FailStep step) {
    if (this.failStep == step) {
      throw new IllegalStateException("FAILURE");
    }
  }
  @Override
  public String name() {
    return player.name();
  }

  @Override
  public TurnAction takeTurn(IPlayerGameState playerState) throws IllegalStateException {
    failIfStep(FailStep.TAKE_TURN);
    return player.takeTurn(playerState);
  }

  @Override
  public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
    failIfStep(FailStep.SETUP);
    player.setup(state, tiles);
  }

  @Override
  public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
    failIfStep(FailStep.NEW_TILES);
    player.newTiles(tiles);
  }

  @Override
  public void win(boolean w) throws IllegalStateException {
    failIfStep(FailStep.WIN);
    player.win(w);
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

  public FailStep failStep() {
    return this.failStep;
  }
  public TurnStrategy strategy() {
    return this.player.strategy();
  }
}
