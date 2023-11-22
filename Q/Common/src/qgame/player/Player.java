package qgame.player;


import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.Tile;
import qgame.state.IPlayerGameState;

/**
 * An interface that represents all the behavior that any player
 * of Q-Game is expected to have.
 */
public interface Player {

  /**
   * Returns the name of the player. Player names are unique within games.
   * @return The name of the player.
   */
  String name();

  /**
   * The player determines what move to make [PASS, EXCHANGE, PLACEMENT] given
   * the current state of the game.
   * @param state the current state of the game that is publicly available to this
   *            player.
   * @return a TurnAction that corresponds to the move the player wants to make.
   * @throws IllegalStateException if unable to make a move.
   */
  TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException;

  /**
   * Tells a player the starting state of a game and the tiles they have.
   * @param map The map of a game.
   * @param tiles The tiles in the players hands
   * @throws IllegalStateException If player is unable to receive the message when.
   */
  //void setup(IMap map, Bag<Tile> tiles) throws IllegalStateException;

  void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException;
  /**
   * Gives the player a new list of tiles.
   * @param tiles The tiles the player now has.
   * @return True if the player successfully receives the tiles
   * @throws IllegalStateException if fails to receive valid data.
   */
  void newTiles(Bag<Tile> tiles) throws IllegalStateException;

  /**
   * Informs the player if they won or not.
   * @param w true if this player won, false if this
   *          player lost.
   * @return true if the player successfully received their win-state.
   * @throws IllegalStateException if fails to receive valid data.
   */
  void win(boolean w) throws IllegalStateException;

  boolean equals(Object o);
}
