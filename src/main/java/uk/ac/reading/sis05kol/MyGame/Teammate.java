package uk.ac.reading.sis05kol.MyGame;


/**
 * @author Steven Whitby
 * class for teammate
 */
public class Teammate extends Sprite{


    /**
     * create new Teammate
     * @param game game Teammate is in
     */
    public Teammate(Game game) {
        super(game);
    }

    /**
     * initialize teammate's parameters
     * @param sX speed in x direction
     * @param sY speed in y direction
     * @param x x position
     * @param y y position
     */
    @Override
    protected void init(float sX, float sY, float x, float y) {
        super.init(sX, sY, x, y);
    }

    /**
     * update teammate's position
     * @param elapsed time since last update
     */
    @Override
    protected void update(float elapsed) {
        //if(x >= game.getCanvasWidth() - bitmap.getWidth()/2 || x <= 0 + bitmap.getWidth()/2) speedX *= -1;



        updateBallCollision(game.getCanvasWidth() - getWidth()/2, game.getCanvasHeight()/2, -1);
        updateBallCollision(getWidth()/2, game.getCanvasHeight()/2, -1);

        x = x + elapsed * speedX * 0.5f;
    }

    private boolean updateBallCollision(float x, float y, int id) {
        if(id != this.spriteID){
            //Get actual distance (without square root - remember?) between the mBall and the ball being checked
            float distanceBetweenBallAndPaddle = (x - this.x) * (x - this.x) + (y - this.y) *(y - this.y);

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
                    //speedY = speedY * speedOfBall / newSpeedOfBall;

                    return true;
                }



        }

        return false;
    }


    public void setTeam(Team team) { this.team = team;}

}
