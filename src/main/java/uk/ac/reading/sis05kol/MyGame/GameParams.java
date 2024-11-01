package uk.ac.reading.sis05kol.MyGame;

import java.io.Serializable;

/**
 * @author Steven Whitby
 * class for game parameters
 */
public class GameParams implements Serializable {
    private int selPlayer, selOpp, selOppSpeed, selBall, selBallSpeed, selBackground;

    /**
     * creates a set of game parameters
     * @param selPlayer selected player
     * @param selOpp selected opponent
     * @param selOppSpeed selected opponent speed
     * @param selBall selected ball
     * @param selBallSpeed selected ball speed
     * @param selBackground selected background
     */
    public GameParams(int selPlayer, int selOpp, int selOppSpeed, int selBall, int selBallSpeed, int selBackground) {
        this.selPlayer = selPlayer;
        this.selOpp = selOpp;
        this.selOppSpeed = selOppSpeed;
        this.selBall = selBall;
        this.selBallSpeed = selBallSpeed;
        this.selBackground = selBackground;
    }

    public int getSelPlayer() { return selPlayer; }

    public int getSelOpp() { return selOpp; }

    public int getSelOppSpeed() { return selOppSpeed; }

    public int getSelBall() { return selBall; }

    public int getSelBallSpeed() { return selBallSpeed; }

    public int getSelBackground() { return selBackground; }
}
