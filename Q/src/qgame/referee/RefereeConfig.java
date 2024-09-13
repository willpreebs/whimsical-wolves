package qgame.referee;

import com.google.gson.JsonObject;
import qgame.json.JsonToObject;
import qgame.state.IGameState;

/**
 * A RefereeConfig contains the following information
 * about how the Referee should operate the game:
 * 
 * state: The state from which the game should be run
 * quiet: Determines if the referee should be quiet or output to the logger 
 * configS: Determines the amount of points that the Q bonus 
 * and the placed all tiles bonus is worth
 * perTurn: Determines how long the QReferee should give wait for a Player's response
 * observe: Determines if the Referee should be run with an Observer attached.
 * 
 */
public class RefereeConfig {

    private IGameState state;
    private boolean quiet;
    private RefereeStateConfig configS;
    private int perTurn;
    private boolean observe;

    public RefereeConfig(JsonObject config) throws IllegalArgumentException {
        try {
            state = JsonToObject.jStateToQGameState(config.get("state0"));
            quiet = config.get("quiet").getAsBoolean();
            configS = JsonToObject.parseRefereeStateConfig(config.get("config-s"));
            perTurn = config.get("per-turn").getAsInt();
            observe = config.get("observe").getAsBoolean();
        } catch (NullPointerException | IllegalStateException | NumberFormatException
                | UnsupportedOperationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Problem with configuration object");
        }
    }

    public RefereeConfig(IGameState state, boolean quiet, RefereeStateConfig configS, int perTurn, boolean observe) {
        this.state = state;
        this.quiet = quiet;
        this.configS = configS;
        this.perTurn = perTurn;
        this.observe = observe;
    }

    public IGameState getState() {
        return state;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public RefereeStateConfig getConfigS() {
        return configS;
    }

    public int getPerTurn() {
        return perTurn;
    }

    public boolean isObserve() {
        return observe;
    }
}
