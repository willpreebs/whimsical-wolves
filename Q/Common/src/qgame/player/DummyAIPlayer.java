package qgame.player;

import qgame.action.TurnAction;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class DummyAIPlayer implements Player{

  public enum FailStep {NONE, SETUP, TAKE_TURN, NEW_TILES, WIN}
  public enum Cheat {NONE, NOT_ADJACENT, NOT_OWNED, NOT_INLINE, NOT_ENOUGH_TILES, NOT_LEGAL_PLACEMENT}

  private final Player player;
  private final TurnStrategy strategy;
  private final FailStep failStep;
  private final Cheat cheat;

  public DummyAIPlayer(String name, TurnStrategy strat) {
    this(name, strat, FailStep.NONE, Cheat.NONE);
  }

  public DummyAIPlayer(String name, TurnStrategy strat, FailStep step) {
    this(name, strat, step, Cheat.NONE);
  }

  public DummyAIPlayer(String name, TurnStrategy strat, FailStep step, Cheat cheat) {
    player = new SimpleAIPlayer(name, strat);
    this.strategy = strat;
    failStep = step;
    this.cheat = cheat;
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
  public TurnAction takeTurn(IPlayerGameState ref) throws IllegalStateException {
    failIfStep(FailStep.TAKE_TURN);
    // TODO: implement cheats
    return player.takeTurn(ref);
  }

  @Override
  public void setup(IMap map, Bag<Tile> tiles) throws IllegalStateException {
    failIfStep(FailStep.SETUP);
    player.setup(map, tiles);
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

  public FailStep failStep() {
    return this.failStep;
  }
  public TurnStrategy strategy() {
    return this.strategy;
  }
}
