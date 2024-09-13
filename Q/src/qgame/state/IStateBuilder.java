package qgame.state;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.player.PlayerInfo;

/**
 * A utilization of the builder pattern on gameState
 * to facilitate the creation of game states.
 */
public interface IStateBuilder {

  /**
   * Creates a statebuilder with the desired placements
   * put on the board's map.
   * @param placement list of positions to place tiles.
   * @return an updated state builder
   */
  IStateBuilder placeTiles(Placement... placement);

  /**
   * Places the given tile at the desired position on
   * the game's map.
   * @param posn position to be placed at
   * @param tile tile to be placed
   * @return an updated StateBuilder with the tile at that
   * location.
   */
  IStateBuilder placeTile(Posn posn, Tile tile);

  /**
   * Creates a statebuilder with the given array of tiles representing
   * the referee's tiles
   * @param tiles tiles to be added to the ref.
   * @return a StateBuilder whose ref's tiles are updated by.
   * this method.
   */
  IStateBuilder addTileBag(Tile... tileBag);

  /**
   * Creates a StateBuilder whose playerInfo has
   * been updated by the provided args.
   * @param infos playerinfo to be updated to
   * @return a new StateBuilder whose PlayerInfo list
   * is updated to these parameters.
   */
  IStateBuilder addPlayerInfo(PlayerInfo... infos);

  /**
   * Finally constructs a game state from the statebuilder.
   * @return QGameMap state from built conditions.
   */
  IGameState build();
}
