package uk.ac.reading.sis05kol.MyGame;

/**
 * @author Steven Whitby
 * class for opponent paddle
 */
public class OpponentPaddle extends Sprite{

    /**
     * create opponent paddle
     * @param game game of opponent paddle
     */
    public OpponentPaddle(Game game) {
        super(game);

    }

    /**
     * update position of opponent paddle
     * @param elapsed time since lst update
     */
    @Override
    public void update(float elapsed) {
        float speedy = speed;
        //humanised movement
        int decision = random.nextInt(40);
        if(decision == 0){// 2.5% chance not moving
            speedy = 0;
        } else if(decision == 1){ //2.5% chance random direction
            randomDirection();
        } else if(decision <= 4){ //2.5% chance move to puck
            pointPaddleTowardsBall(game.getBall());
            speedy = speed;
        }

        //horizontal boundary
        //if(x >= game.getCanvasWidth() - bitmap.getWidth()/2 || x <= 0 + bitmap.getWidth()/2) speedX *= -1;
        //move

        x = x + elapsed * speedX * speedy;
        //y = y + elapsed * speedY;
    }

    //private methods --------------------------------------------------------
    private void pointPaddleTowardsBall(Ball ball) {
        // Calculate the angle between the paddle and the ball
        float angle = (float) Math.atan2(ball.getY() - this.y, ball.getX() - this.x);

        // Set the speedX and speedY of the paddle to move towards the ball at the given paddle speed

        float paddleSpeed = 400f; // Adjust this value to control the speed of the paddle
        speedX = (float) (paddleSpeed * Math.cos(angle));
        speedY = (float) (paddleSpeed * Math.sin(angle));
    }




}
