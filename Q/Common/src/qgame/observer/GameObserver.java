package qgame.observer;

import java.util.List;

import qgame.state.map.QGameMap;
import qgame.referee.GameResults;
import qgame.state.QGameState;

public interface GameObserver {
  void receiveState(QGameState state);

  void endGame(GameResults results);

  List<Integer> viewScores();

  QGameMap viewBoard();

  List<String> turnOrder();

  List<QGameState> gameStates();

  boolean isGameOver();

  GameResults results();
}
