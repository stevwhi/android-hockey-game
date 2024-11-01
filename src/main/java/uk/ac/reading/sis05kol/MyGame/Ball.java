package uk.ac.reading.sis05kol.MyGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * @author Steven Whitby
 * class for ball
 */
public class Ball extends Sprite{
    /**
     * constructor for ball in game "game"
     * @param game game the ball is in
     */
    public Ball(Game game) {
        super(game);
    }

    /**
     * initialize ball values
     * @param sX speed in X direction
     * @param sY speed in Y direction
     * @param x x position
     * @param y y position
     */
    @Override
    protected void init(float sX, float sY, float x, float y) {
        super.init(sX, sY, x, y);
        randomDirection();
    }

    /**
     * update ball position
     * @param elapsed time since last update call
     */
    @Override
    public void update(float elapsed) {
        //Loop through all Sprites
        for(int i = 0; i < game.getAllSprites().size(); i++){
            Sprite s = game.getAllSprites().get(i);

            if(!(s instanceof Teammate && s.getTeam() == Team.PLAYER && speedY < 0
                    || s instanceof Teammate && s.getTeam() == Team.OPPONENT && speedY > 0 )){
                updateBallCollision(s.getX(), s.getY(), s.getID());
            }
        }

        for(int i = 0; i < game.getAllPowerups().size(); i++){
            Powerup p = game.getAllPowerups().get(i);

            updateBallCollision(p.getX(), p.getY(), p.getID(), p.getPowerupType());
        }




        //Move the ball's X and Y using the speed (pixel/sec)
        x = x + elapsed * speedX * speed;
        y = y + elapsed * speedY * speed;


        //Check if the ball hits either the left side or the right side of the screen
        //But only do something if the ball is moving towards that side of the screen
        //If it does that => change the direction of the ball in the X direction
        if((x <= getWidth() / 2 && speedX < 0) || (x >= game.getCanvasWidth() - getWidth() / 2 && speedX > 0) ) {
            speedX = -speedX;
        }


        //If the ball goes out of the top of the screen and moves towards the top of the screen =>
        //change the direction of the ball in the Y direction
        if(y <= getWidth() / 2 && speedY < 0) {
            if(x >= game.getGoalpost(0) && x <=game.getGoalpost(1)){
                game.updateScore(1);
            }
            speedY = -speedY;
        }

        //If the ball goes out of the bottom of the screen => lose the game
        if(y >= game.getCanvasHeight()) {
            if(x >= game.getGoalpost(0) && x <=game.getGoalpost(1)){
                game.setState(GameThread.STATE_LOSE);
            }
            speedY = -speedY;
        }

    }


    //private methods-------------------------------------------------------------

    //Collision control between mBall and another big ball
    private boolean updateBallCollision(float x, float y, int id) {
        if(id != this.spriteID){
            //Get actual distance (without square root - remember?) between the mBall and the ball being checked
            float distanceBetweenBallAndPaddle = (x - this.x) * (x - this.x) + (y - this.y) *(y - this.y);

            if(speedY < 0){
                //Check if the actual distance is lower than the allowed => collision
                if(game.getMinDistanceBetweenBallAndOpponentPaddle() >= distanceBetweenBallAndPaddle) {
                    //Get the present speed (this should also be the speed going away after the collision)
                    float speedOfBall = (float) Math.sqrt(speedX*speedX + speedY*speedY);

                    //Change the direction of the ball
                    speedX = this.x - x;
                    speedY = this.y - y;

                    //Get the speed after the collision
                    float newSpeedOfBall = (float) Math.sqrt(speedX*speedX + speedY*speedY);

                    //using the fraction between the original speed and present speed to calculate the needed
                    //velocities in X and Y to get the original speed but with the new angle.
                    speedX = speedX * speedOfBall / newSpeedOfBall;
                    speedY = speedY * speedOfBall / newSpeedOfBall;

                    return true;
                }
            } else{
                //Check if the actual distance is lower than the allowed => collision
                if(game.getMinDistanceBetweenBallAndPlayerPaddle() >= distanceBetweenBallAndPaddle) {
                    //Get the present speed (this should also be the speed going away after the collision)
                    float speedOfBall = (float) Math.sqrt(speedX*speedX + speedY*speedY);

                    //Change the direction of the ball
                    speedX = this.x - x;
                    speedY = this.y - y;

                    //Get the speed after the collision
                    float newSpeedOfBall = (float) Math.sqrt(speedX*speedX + speedY*speedY);

                    //using the fraction between the original speed and present speed to calculate the needed
                    //velocities in X and Y to get the original speed but with the new angle.
                    speedX = speedX * speedOfBall / newSpeedOfBall;
                    speedY = speedY * speedOfBall / newSpeedOfBall;

                    return true;
                }
            }


        }

        return false;
    }

    private boolean updateBallCollision(float x, float y, int id, Powerup.PowerupType pt){
            //Get actual distance (without square root - remember?) between the mBall and the ball being checked
            float distanceBetweenBallAndPowerup = (x - this.x) * (x - this.x) + (y - this.y) *(y - this.y);
            //Check if the actual distance is lower than the allowed => collision
            if(game.getmMinDistanceBetweenBallAndPowerup() >= distanceBetweenBallAndPowerup) {
                game.respondToPowerup(id, pt);
                return true;
        }
        return false;
    }

    //getters and setters -------------------------------------------------------

    public float getSpeedY() { return speedY; }



}