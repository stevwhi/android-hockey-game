package uk.ac.reading.sis05kol.MyGame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.Random;

/**
 * @author Steven Whitby
 * class for game sprite
 */
public abstract class Sprite {
    public enum Team{
        PLAYER, OPPONENT;
    }

    protected Team team;

    protected int spriteID;
    static int spriteCount = 0;
    protected Bitmap bitmap, originalBitmap;
    protected float x, y, speedX, speedY, speed;
    protected Random random;
    protected Game game;


    protected boolean heightGrow, widthGrow, heightShrink, widthShrink;
    private int currentWidth, currentHeight, maxWidth, maxHeight, originalHeight, originalWidth;
    private float growthFactor;

    /**
     * create new game sprite
     * @param game game sprite is in
     */
    public Sprite(Game game) {
        this.game = game;
        this.random = new Random();
        spriteID = spriteCount++;

        heightGrow = false;
        widthGrow = false;
        heightShrink = false;
        widthShrink = false;
    }

    /**
     * initialize sprite's parameters
     * @param sX speed in x direction
     * @param sY speed in y direction
     * @param x x position
     * @param y y position
     */
    protected void init(float sX, float sY, float x, float y){
        this.speedX = sX;
        this.speedY = sY;
        setPosition(x, y);
    }


    /**
     * initialize sprite's difficulty level dependent values
     * @param speed speed multiplier
     * @param bitmap bitmap image of sprite
     */
    protected void difInit(float speed, Bitmap bitmap) {
        this.speed = speed;
        this.bitmap = bitmap;
        this.originalBitmap = bitmap;

        growthFactor = 1.8f;

        originalHeight = bitmap.getHeight();
        originalWidth = bitmap.getWidth();

        currentHeight = originalHeight;
        currentWidth = originalWidth;

        maxHeight = Math.round(originalHeight + (originalHeight * growthFactor));
        maxWidth = Math.round(originalWidth + (currentWidth * growthFactor));


    }

    /**
     * update sprite's position
     * @param elapsed time since last update
     */
    protected abstract void update(float elapsed);


    /**
     * draw sprite to canvas
     * @param canvas canvas to be drawn on
     */
    protected void draw(Canvas canvas) {
        if (heightGrow) {
            currentHeight += 5;
            if (currentHeight >= maxHeight) {
                // Powerup effect is over
                heightGrow = false;
                currentHeight = maxHeight;
            }
        } else if (heightShrink) {
            currentHeight -= 5;
            if (currentHeight <= originalHeight) {
                // Powerup effect is over
                heightShrink = false;
                currentHeight = originalHeight;
            }
        }
        if (widthGrow){
            currentWidth += 5;
            if (currentWidth >= maxWidth) {
                // Powerup effect is over
                widthGrow = false;
                currentWidth = maxWidth;
            }
        } else if (widthShrink){
            currentWidth -= 5;
            if (currentWidth <= originalWidth) {
                // Powerup effect is over
                widthShrink = false;
                currentWidth = originalWidth;
            }
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, currentWidth, currentHeight, false);
        canvas.drawBitmap(bitmap, x - getWidth() / 2, y - getHeight() / 2, null);
    }

    /**
     * shrink effect on sprite
     * @param x x position
     * @param y y position
     */
    protected void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * set random direction of sprite
     */
    protected void randomDirection(){
        speedX *= random.nextInt(2)*2-1;
        speedY *= random.nextInt(2)*2-1;
    }

    protected void boundaryBounce(){
        if (x <= getWidth()/2 || x >= game.getCanvasWidth() - getWidth()/2) speedX *= -1;
        // if Drone hit (tried to go through) left or right walls, set mirror angle, being 180-angle
        if (y <= getWidth()/2 || y >= game.getCanvasHeight() - getHeight()/2) speedY *= -1;
        // if try to go off top or out of top half, set mirror angle
    }

    /**
     * is sprite at xPos,yPos size or hitting this sprite
     * @param xPos x Position
     * @param yPos y Position
     * @param rad radius of sprite
     * @return true if hitting
     */
    public boolean hitting(double xPos, double yPos, double rad) {
        double radius;

        if(this.getHeight() > this.getWidth()){
            radius = this.getHeight()/2;
        } else{
            radius = this.getWidth()/2;
        }

        return (xPos-this.x)*(xPos-this.x) + (yPos-this.y)*(yPos-this.y) < (rad+radius)*(rad+radius);
        // hitting if a^2 = b^2 < rad^2
    }



    //getters and setters -----------------------------------------

    public int getWidth() { return bitmap.getWidth();}

    public int getHeight() { return bitmap.getHeight();}

    public float getX() { return x;}

    public float getY() { return y;}

    public int getID() { return spriteID; }

    public float getSpeed() { return this.speed; }

    /**
     * grow effect on sprite
     */
    public void grow() {
        heightGrow = true;
        widthGrow = true;
    }

    /**
     * shrink effect on sprite
     */
    public void shrink() {
        heightShrink = true;
        widthShrink = true;
    }

    public void setBitmap(Bitmap b) {
        this.bitmap = b;
    }

    public Bitmap getBitmap() { return this.originalBitmap; }

    //for difficulty
    public void setSpeed(float speed) { this.speed = speed; }

    public Teammate.Team getTeam() { return team; }





}