package uk.ac.reading.sis05kol.MyGame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * @author Steven Whitby
 * class for powerup
 */
public class Powerup extends Sprite{

    public enum PowerupType{
        COIN,
        PADDLE_SIZE,
        BALL_SPEED,
        TWO_BALLS,
        TEAMMATE;

        private static final Random rand = new Random();

        /**
         * choose random powerup
         */
        public static PowerupType randomPowerType()  {
            PowerupType[] powerups = values();
            return powerups[rand.nextInt(powerups.length)];
        }
    }

    Bitmap bitmapArr[];
    private int currentFrame;
    private long currentTime, lastFrameTime;
    PowerupType pt;


    /**
     * create new powerup
     * @param game game powerup will be in
     * @param bitmapArr frames of animation
     * @param pt Type of powerup
     */
    public Powerup(Game game, Bitmap[] bitmapArr, PowerupType pt) {
        super(game);
        this.bitmapArr = bitmapArr;
        this.pt = pt;

        setPosition();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
    }



    /**
     * update powerup frame
     * @param elapsed last time since update
     */
    @Override
    protected void update(float elapsed) {
        currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastFrameTime;

        if (elapsedTime > 100) { // change the animation speed by changing this value
            currentFrame++;
            if (currentFrame >= bitmapArr.length) {
                currentFrame = 0;
            }
            lastFrameTime = currentTime;
        }
    }

    /**
     * draw powerup animation
     * @param canvas canvas to be drawn on
     */
    @Override
    protected void draw(Canvas canvas) {
        bitmap = bitmapArr[currentFrame];
        canvas.drawBitmap(bitmap, x - getWidth() / 2, y - getHeight() / 2, null);
    }


    /**
     * set position of powerup
     */
    protected void setPosition() {
        int minX = bitmapArr[0].getWidth()/2;
        int maxX = game.getCanvasWidth() - bitmapArr[0].getWidth()/2;
        int minY = game.getCanvasHeight()/3 + bitmapArr[0].getHeight()/2;
        int maxY = game.getCanvasHeight()/3 * 2 - bitmapArr[0].getHeight()/2;

        this.x = random.nextInt(maxX - minX) + minX;
        this.y = random.nextInt(maxY - minY) + minY;
    }

    public PowerupType getPowerupType() { return pt;}
}
