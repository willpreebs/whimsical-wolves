package qgame.referee;

public class RefereeStateConfig {

    private int qBonus;
    private int fBonus;

    public RefereeStateConfig(int qBonus, int fBonus) {
        this.qBonus = qBonus;
        this.fBonus = fBonus;
    }

    public int getqBonus() {
        return qBonus;
    }

    public int getfBonus() {
        return fBonus;
    }
}