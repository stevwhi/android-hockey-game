package uk.ac.reading.sis05kol.MyGame;

//Other parts of the android libraries that we use
import static uk.ac.reading.sis05kol.MyGame.Powerup.PowerupType.TWO_BALLS;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.material.circularreveal.CircularRevealFrameLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Steven Whitby
 * class game
 */
public class Game extends GameThread{
    private int powerupMaxY, powerupMinY, getPowerupMaxX, powerUpMinX;
    //animation frames
    private final int paddleSizePowerup[] = {R.drawable.green_orb_1, R.drawable.green_orb_2,R.drawable.green_orb_3,R.drawable.green_orb_4,R.drawable.green_orb_5,R.drawable.green_orb_6};
    private final int ballSpeedPowerup[] = {R.drawable.yellow_orb_1, R.drawable.yellow_orb_2,R.drawable.yellow_orb_3,R.drawable.yellow_orb_4,R.drawable.yellow_orb_5,R.drawable.yellow_orb_6};
    private final int twoBallPowerup[] = {R.drawable.red_orb_1, R.drawable.red_orb_2,R.drawable.red_orb_3,R.drawable.red_orb_4,R.drawable.red_orb_5,R.drawable.red_orb_6};
    private final int teammatePowerup[] = {R.drawable.blue_orb_1, R.drawable.blue_orb_2,R.drawable.blue_orb_3,R.drawable.blue_orb_4,R.drawable.blue_orb_5,R.drawable.blue_orb_6,};
    private final int coinPowerup[] = {R.drawable.gold_coin_round_diamond_1, R.drawable.gold_coin_round_diamond_2, R.drawable.gold_coin_round_diamond_3, R.drawable.gold_coin_round_diamond_4, R.drawable.gold_coin_round_diamond_5, R.drawable.gold_coin_round_diamond_6};

    //dificulty parameters
    private final int  levelOpponentImages[] = {R.drawable.play_paddle_blue,R.drawable.play_paddle_green,R.drawable.play_paddle_red};
    private final int levelPlayerImages[] = {R.drawable.play_paddle_blue,R.drawable.play_paddle_green,R.drawable.play_paddle_red};
    private final int levelBackgroundColors[] = {Color.parseColor("#39FF14"), Color.parseColor("#FFFF33"), Color.parseColor("#FF3131")};
    private final int levelBallImages[] = {R.drawable.play_puck_white,R.drawable.play_puck_yellow,R.drawable.play_puck_purple};
    private final float levelOpponentSpeeds[] = {1, 2.6f, 4.5f};
    private final float levelBallSpeeds[] = {1f, 1.4f, 1.9f};

    private GameView gameView;
    private Ball ball;
    private PlayerPaddle pPaddle;
    private OpponentPaddle oPaddle;
    private ArrayList<Sprite> allSprites;
    private ArrayList <Powerup> allPowerups;
    private ArrayList<Integer> teammateIDs;
    private ArrayList<Integer> newBallIDs;
    private Bitmap ballBM, pPaddleBM, oPaddleBM;

    //This will store the min distance allowed between a big ball and the small ball
    //This is used to check collisions
    private float mMinDistanceBetweenBallAndPlayerPaddle = 0;
    private float mMinDistanceBetweenBallAndOpponentPaddle = 0;
    private float mMinDistanceBetweenBallAndPowerup = 0;
    private int margin = 20;
    private int themeColor = Color.WHITE;
    private int[] goalposts = new int[2];

    float tmpPaddle;
    float tmpOpp;

    private Random rand;
    private Timer powerupTimer, removeTimer, powerupEffectTimer;

    //This is run before anything else, so we can prepare things here
    /**
     * creates a new game
     * @param gameView game view of game
     */
    public Game(GameView gameView) {
        super(gameView);
        this.gameView = gameView;
        this.ball = new Ball(this);
        this.pPaddle = new PlayerPaddle(this);
        this.oPaddle = new OpponentPaddle(this);

        teammateIDs = new ArrayList<>();
        newBallIDs = new ArrayList<>();
        allPowerups = new ArrayList<>();
        powerupTimer = new Timer();
        removeTimer = new Timer();
        powerupEffectTimer = new Timer();
        this.rand = new Random();
    }

    //This is run before a new game (also after an old game)
    /**
     * initialize game
     */
    @Override
    public void init() {
        difficultyModifier(gameView.getSelectedDifficulty());
        allSprites = new ArrayList<>();
        allSprites.add(ball);
        allSprites.add(pPaddle);
        allSprites.add(oPaddle);

        ball.init(mCanvasWidth / 3, mCanvasHeight / 3, mCanvasWidth / 2, mCanvasHeight / 2);
        pPaddle.init(0,0, mCanvasWidth / 2, mCanvasHeight - margin - pPaddleBM.getWidth()/2);
        oPaddle.init(mCanvasWidth / 3, mCanvasHeight / 3, mCanvasWidth / 2, margin + oPaddleBM.getWidth()/2);

        mMinDistanceBetweenBallAndPlayerPaddle = (pPaddle.getWidth() / 2 + ball.getWidth() / 2) * (pPaddle.getWidth() / 2 + ball.getWidth() / 2);
        mMinDistanceBetweenBallAndOpponentPaddle = (oPaddle.getWidth() / 2 + ball.getWidth() / 2) * (oPaddle.getWidth() / 2 + ball.getWidth() / 2);
        mMinDistanceBetweenBallAndPowerup = (createBitmap(paddleSizePowerup[0]).getWidth() / 2 + ball.getWidth() / 2) * (createBitmap(paddleSizePowerup[0]).getWidth() / 2 + ball.getWidth() / 2);

        tmpPaddle = mMinDistanceBetweenBallAndPlayerPaddle;
        tmpOpp = mMinDistanceBetweenBallAndOpponentPaddle;
    }

    //This is run whenever the phone is touched by the user
    /**
     * updates player position to position touched on screen
     * @param x x position touched on screen
     * @param y y position touched on screen
     */
    @Override
    protected void actionOnTouch(float x, float y) {
        //Move the ball to the x position of the touch
        pPaddle.setPosition(x, y);
    }

    //This is run whenever the phone moves around its axises
    /**
     * completes action when phone moved
     * @param xDirection x direction
     * @param yDirection y direction
     * @param zDirection z Direction
     */
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        pPaddle.actionWhenPhoneMoved(xDirection, yDirection, zDirection);
    }

    //This is run just before the game "scenario" is printed on the screen
    /**
     * updates all elements in game
     * @param secondsElapsed time since last update
     */
    @Override
    protected void updateGame(float secondsElapsed) {

        for(int i = 0; i < allSprites.size(); i++){
            allSprites.get(i).update(secondsElapsed);
        }

        for(int i = 0; i < allPowerups.size(); i++){
            allPowerups.get(i).update(secondsElapsed);
        }

    }

    /**
     * draws everything on canvas
     * @param canvas canvas to be drawn on
     */
    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to do nothing
        //It is ok not understanding what is happening here
        if(canvas == null) return;
        //House keeping
        super.doDraw(canvas);
        drawBackground(canvas);

        for(int i = 0; i < allSprites.size(); i++){
            allSprites.get(i).draw(canvas);
        }
        for(int i = 0; i < allPowerups.size(); i++){
            allPowerups.get(i).draw(canvas);
        }
    }

    /**
     * takes appropriate powerup action on game parameters
     * @param id id of powerup
     * @param pt type of powerup
     */
    public void respondToPowerup(int id, Powerup.PowerupType pt){
        removePowerupById(id);

        switch (pt) {
            case PADDLE_SIZE:
                // Code for increasing paddle size

                if(ball.getSpeedY() < 0){
                    pPaddle.grow();
                    mMinDistanceBetweenBallAndPlayerPaddle *= 4;
                } else{
                    oPaddle.grow();
                    mMinDistanceBetweenBallAndOpponentPaddle *= 4;
                }
                TimerTask paddleSizeEffect = new TimerTask() {
                    @Override
                    public void run() {
                        // Code to revert the effect
                        pPaddle.shrink();
                        oPaddle.shrink();
                        mMinDistanceBetweenBallAndPlayerPaddle = tmpPaddle;
                        mMinDistanceBetweenBallAndOpponentPaddle = tmpOpp;
                    }
                };
                powerupEffectTimer.schedule(paddleSizeEffect, 10000); // Effect lasts for 10 seconds
                break;

            case BALL_SPEED:
                // Code for ball speed effect
                ball.setSpeed(ball.getSpeed()*2);
                TimerTask ballSpeedEffect = new TimerTask() {
                    @Override
                    public void run() {
                        // Code to revert the effect
                        ball.setSpeed(ball.getSpeed()/2);
                    }
                };
                powerupEffectTimer.schedule(ballSpeedEffect, 10000); // Effect lasts for 10 seconds
                break;

            case TWO_BALLS:
                // Code for two ball effect
                addNewBall();
                TimerTask twoBallEffect = new TimerTask() {
                    @Override
                    public void run() {
                        // Code to revert the effect;
                        removeSpriteById(newBallIDs.get(0));
                    }
                };
                powerupEffectTimer.schedule(twoBallEffect, 10000); // Effect lasts for 10 seconds
                break;

            case TEAMMATE:
                // Code for ball teammate effect
                addTeammate();

                TimerTask teammateEffect = new TimerTask() {
                    @Override
                    public void run() {
                        // Code to revert the effect;
                        removeSpriteById(teammateIDs.get(0));
                        teammateIDs.remove(0);
                    }
                };
                powerupEffectTimer.schedule(teammateEffect, 10000); // Effect lasts for 10 seconds
                break;

            case COIN:
                if(ball.getSpeedY() < 0){
                    updateScore(5);
                } else{ updateScore(-1); }
            }

    }

    private void removePowerupById(int id) {
        for (int i = 0; i < allPowerups.size(); i++) {
            Powerup powerup = allPowerups.get(i);
            if (powerup.getID() == id) {
                allPowerups.remove(i);
                break; // Stop the loop after removing the powerup
            }
        }
    }

    private void removeSpriteById(int id) {
        for (int i = 0; i < allSprites.size(); i++) {
            Sprite sprite = allSprites.get(i);
            if (sprite.getID() == id) {
                allSprites.remove(i);
                break; // Stop the loop after removing the powerup
            }
        }
    }

    private void addNewBall(){
        Ball newball = new Ball(this);
        newball.init(mCanvasWidth / 3, mCanvasHeight / 3, mCanvasWidth / 2, mCanvasHeight / 2);
        newball.difInit(1, ball.getBitmap());

        double xPos = mCanvasWidth / 2;
        double yPos = mCanvasHeight / 2;
        double rad;

        if(newball.getWidth() > newball.getHeight()){
            rad = newball.getWidth()/2;
        } else{ rad = newball.getHeight()/2;}

        while(!canGoHere(xPos, yPos, rad, newball.getID())) {
            xPos = rand.nextInt((getCanvasWidth() - newball.getWidth()/2) - newball.getWidth()/2) + newball.getWidth()/2;

        }

        newball.setPosition((float) xPos, (float) yPos);
        allSprites.add(newball);
        newBallIDs.add(newball.getID());
    }
    private void addTeammate(){
        Teammate tm = new Teammate(this);
        tm.init(mCanvasWidth / 3, mCanvasHeight / 3, mCanvasWidth / 2, mCanvasHeight / 2);

        if(ball.speedY < 0){
            tm.setTeam(Teammate.Team.PLAYER);
            tm.difInit(1, pPaddle.getBitmap());
        } else {
            tm.setTeam(Teammate.Team.OPPONENT);
            tm.difInit(1, oPaddle.getBitmap());
        }

        double xPos = mCanvasWidth / 2;
        double yPos = mCanvasHeight / 2;
        double rad;

        if(tm.getWidth() > tm.getHeight()){
            rad = tm.getWidth()/2;
        } else{ rad = tm.getHeight()/2;}

        while(!canGoHere(xPos, yPos, rad, tm.getID())) {
            xPos = rand.nextInt((getCanvasWidth() - tm.getWidth()/2) - tm.getWidth()/2) + tm.getWidth()/2;

        }

        tm.setPosition((float) xPos, (float) yPos);
        allSprites.add(tm);
        teammateIDs.add(tm.getID());
    }

    /**
     * Check if sprite is placed in plausible spot
     * @param xPos				drone x position
     * @param yPos				drone y position
     * @param rad			radius of drone
     * @param notID			identify of sprite not to be checked
     * @return				true if plausible
     */
    private boolean canGoHere(double xPos, double yPos, double rad, int notID) {
        if (xPos <= rad || xPos >= getCanvasWidth() - rad) return false;		//left and right collision
        if (yPos <= getCanvasHeight()/3 + rad || yPos >= getCanvasHeight()/3 * 2 - rad) return false;		//top and bottom collsion

        for (Sprite hitSprite : allSprites) 							//hitting another drone
            if (hitSprite.getID() != notID && hitSprite.hitting(xPos, yPos, rad)) {
                return false;
            }

        return true;
    }

    private Bitmap createBitmap(int imageID){
        return BitmapFactory.decodeResource(gameView.getContext().getResources(), imageID);
    }

    private void difficultyModifier(String dif){
        switch (dif){
            case "easy":
                //easy paramfnff
                difInit(0);
                break;
            case "medium":
                //med param
                difInit(1);
                break;
            case "hard":
                //hard param
                difInit(2);
                break;
            case "custom":
                custDifInit();
                break;
        }
    }

    private void custDifInit(){
        int selPlayer = gameView.getSelPlayer();
        int selOpp = gameView.getSelOpp();
        int selOppSpeed = gameView.getSelOppSpeed();
        int selBall = gameView.getSelBall();
        int selBallSpeed = gameView.getSelBallSpeed();
        int selBackground = gameView.getSelBackground();

        ballBM = createBitmap(levelBallImages[selBall]);
        pPaddleBM = createBitmap(levelPlayerImages[selPlayer]);
        oPaddleBM = createBitmap(levelOpponentImages[selOpp]);
        setThemeColor(levelBackgroundColors[selBackground]);

        ball.difInit(translateBallSpeed(selBallSpeed), ballBM);
        pPaddle.difInit(0, pPaddleBM);
        oPaddle.difInit(translateOppSpeed(selOppSpeed), oPaddleBM);
    }

    private void difInit(int n){

        ballBM = createBitmap(levelBallImages[0]);
        pPaddleBM = createBitmap(levelPlayerImages[0]);
        oPaddleBM = createBitmap(levelOpponentImages[2]);


        setThemeColor(levelBackgroundColors[n]);
        ball.difInit(levelBallSpeeds[n], ballBM);
        pPaddle.difInit(0, pPaddleBM);
        oPaddle.difInit(levelOpponentSpeeds[n], oPaddleBM);
    }


    private void drawBackground(Canvas canvas){
        //if(mBackgroundImage != null) canvas.setBitmap(mBackgroundImage);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // Draw the background color
        canvas.drawColor(Color.BLACK);


        // Define the table dimensions
        int tableWidth = width - margin * 2;
        int tableHeight = height - margin * 2;
        int tableLeft = (width - tableWidth) / 2;
        int tableTop = (height - tableHeight) / 2;

        // Define the border dimensions
        int borderWidth = 10;
        int borderLeft = tableLeft - borderWidth;
        int borderTop = tableTop - borderWidth;
        int borderRight = tableLeft + tableWidth + borderWidth;
        int borderBottom = tableTop + tableHeight + borderWidth;

        // Draw the table border
        Paint borderPaint = new Paint();
        borderPaint.setColor(themeColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);

        // Add a glowing effect to the border
        borderPaint.setShadowLayer(10, 0, 0, themeColor);

        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, borderPaint);

        // Draw the center line
        Paint linePaint = new Paint();
        linePaint.setColor(themeColor);
        linePaint.setStrokeWidth(6);

        // Add a glowing effect to the line
        linePaint.setShadowLayer(10, 0, 0, themeColor);

        canvas.drawLine(tableLeft, height / 2, tableLeft + tableWidth, height / 2, linePaint);

        // Draw the center circle
        Paint circlePaint = new Paint();
        circlePaint.setColor(themeColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(6);

        // Add a glowing effect to the circle
        circlePaint.setShadowLayer(10, 0, 0, themeColor);

        canvas.drawCircle(width / 2, height / 2, tableWidth / 6, circlePaint);

        // Draw the goal areas
        Paint goalPaint = new Paint();
        goalPaint.setColor(themeColor);

        // Add a glowing effect to the goal area
        goalPaint.setShadowLayer(10, 0, 0, themeColor);

        goalposts[0] = tableLeft + tableWidth / 3;
        goalposts[1] = tableLeft + 2 * tableWidth / 3;
        canvas.drawRect(tableLeft + tableWidth / 3, tableTop, tableLeft + 2 * tableWidth / 3, tableTop + borderWidth, goalPaint);
        canvas.drawRect(tableLeft + tableWidth / 3, tableTop + tableHeight - borderWidth, tableLeft + 2 * tableWidth / 3, tableTop + tableHeight, goalPaint);

        // Draw the center spot
        Paint spotPaint = new Paint();
        spotPaint.setColor(themeColor);

        // Add a glowing effect to the center spot
        spotPaint.setShadowLayer(10, 0, 0, themeColor);

        canvas.drawCircle(width / 2, height / 2, 10, spotPaint);

    }

    private Bitmap[] createFrames(int[] frames){
        Bitmap bitmapArr[] = new Bitmap[frames.length];
        for(int i = 0; i < frames.length; i++){
            bitmapArr[i] = createBitmap(frames[i]);
        }
        return bitmapArr;
    }

    private Powerup createPowerup(Powerup.PowerupType pt){
        switch(pt){
            case PADDLE_SIZE:
                return  new Powerup(this, createFrames(paddleSizePowerup), Powerup.PowerupType.PADDLE_SIZE);
            case BALL_SPEED:
                return new Powerup(this, createFrames(ballSpeedPowerup), Powerup.PowerupType.BALL_SPEED);
            case TWO_BALLS:
                return  new Powerup(this, createFrames(twoBallPowerup), TWO_BALLS);
            case TEAMMATE:
                return  new Powerup(this, createFrames(teammatePowerup), Powerup.PowerupType.TEAMMATE);
            case COIN:
                return  new Powerup(this, createFrames(coinPowerup), Powerup.PowerupType.COIN);
        }
        return null;
    }

    /**
     * starts powerup timers
     */
    @Override
    public void startTimers() {
        powerupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // This code will be executed every 10 seconds
                // Add a new powerup to the powerups list
                allPowerups.add(createPowerup(Powerup.PowerupType.randomPowerType()));
            }
        }, 0, 10000); // Delay of 0 ms, and then repeat every 10000 ms (10 seconds)

        removeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // This code will be executed every 15 seconds
                // Remove the least recently added powerup from the powerups list
                if (!allPowerups.isEmpty()) {
                    allPowerups.remove(0);
                }
            }
        }, 0, 15000); // Delay of 0 ms, and then repeat every 15000 ms (15 seconds)
    }

    /**
     * stop the powerup timers
     */
    @Override
    public void stopTimers() {
        powerupTimer.cancel();
        removeTimer.cancel();
        powerupEffectTimer.cancel();

    }

    private float translateBallSpeed(int n){
        float ballSpeed = 1;
        switch(n){
            case 1:
                ballSpeed = 1f;
                break;
            case 2:
                ballSpeed = 1.1f;
                break;
            case 3:
                ballSpeed = 1.2f;
                break;
            case 4:
                ballSpeed = 1.3f;
                break;
            case 5:
                ballSpeed = 1.4f;
                break;
            case 6:
                ballSpeed = 1.5f;
                break;
            case 7:
                ballSpeed = 1.6f;
                break;
            case 8:
                ballSpeed = 1.7f;
                break;
            case 9:
                ballSpeed = 1.8f;
                break;
            case 10:
                ballSpeed = 1.9f;
                break;
        }

        return ballSpeed;
    }

    private float translateOppSpeed(int n){
        float oppSpeed = 1;
        switch(n){
            case 1:
                oppSpeed = 1;
                break;
            case 2:
                oppSpeed = 1.4f;
                break;
            case 3:
                oppSpeed = 1.8f;
                break;
            case 4:
                oppSpeed = 2.2f;
                break;
            case 5:
                oppSpeed = 2.6f;
                break;
            case 6:
                oppSpeed = 3f;
                break;
            case 7:
                oppSpeed = 3.4f;
                break;
            case 8:
                oppSpeed = 3.8f;
                break;
            case 9:
                oppSpeed = 4.2f;
                break;
            case 10:
                oppSpeed = 4.5f;
                break;
        }

        return oppSpeed;
    }

    //getters and setters ------------------------------------------------
    public int getGoalpost(int n) { return goalposts[n]; }
    public int getCanvasHeight() { return mCanvasHeight; }

    public int getCanvasWidth() { return mCanvasWidth; }

    public float getMinDistanceBetweenBallAndPlayerPaddle() { return mMinDistanceBetweenBallAndPlayerPaddle; }

    public float getMinDistanceBetweenBallAndOpponentPaddle() { return mMinDistanceBetweenBallAndOpponentPaddle; }

    public float getmMinDistanceBetweenBallAndPowerup() { return mMinDistanceBetweenBallAndPowerup; }

    public ArrayList<Sprite> getAllSprites() { return allSprites; }

    public ArrayList<Powerup> getAllPowerups() { return allPowerups; }

    public Ball getBall() { return ball; }

    public void setThemeColor(int color){ this.themeColor = color; }

}


