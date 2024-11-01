package uk.ac.reading.sis05kol.MyGame;

/**
 * @author Steven Whitby
 * class for player paddle
 */
public class PlayerPaddle extends Sprite{

    /**
     * create player paddle
     * @param game game of player paddle
     */
    public PlayerPaddle(Game game) {
        super(game);
    }

    /**
     * update position of player paddle
     * @param elapsed time since last update
     */
    @Override
    protected void update(float elapsed) {
        //Move the paddle's X and Y using the speed (pixel/sec)
        x = x + elapsed * speedX * speed;
    }

    /**
     * change movement when screen moved
     * @param xDirection x direction
     * @param yDirection y direction
     * @param zDirection z direction
     */
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection){
        //Change the paddle speed
        speedX = speedX + 70f * xDirection;

        //If paddle is outside the screen and moving further away
        //Move it into the screen and set its speed to 0
        if(x <= 0 && speedX < 0) {
            speedX = 0;
            x = 0;
        }
        if(x >= game.getCanvasWidth() && speedX > 0) {
            speedX = 0;
            x = game.getCanvasWidth();
        }

        //Change the paddle speed
        speedY = speedY + 70f * yDirection;

        //If paddle is outside the screen and moving further away
        //Move it into the screen and set its speed to 0
        if(y <= 0 && speedY < 0) {
            speedY = 0;
            y = 0;
        }
        if(y >= game.getCanvasHeight() && speedY > 0) {
            speedY = 0;
            y = game.getCanvasHeight();
        }
    }
}
