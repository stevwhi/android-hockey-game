package uk.ac.reading.sis05kol.MyGame;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * class for activity game is run in
 */
public class MainActivity extends Activity {
    private String selectedDifficulty;
    private int selPlayer, selOpp, selOppSpeed, selBall, selBallSpeed, selBackground;
    private Long highScoreForThisDif;
    private TextView mHighScoreView;
    private FirebaseDatabase db;
    private DatabaseReference myRef;
// ...


    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;

    private GameThread mGameThread;
    private GameView mGameView;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //get parameters
        selectedDifficulty = getIntent().getStringExtra("difficulty");
        selPlayer = getIntent().getIntExtra("playerOption", 0);
        selOpp = getIntent().getIntExtra("opponentOption", 0);
        selOppSpeed = getIntent().getIntExtra("opponentSpeed", 0);
        selBall = getIntent().getIntExtra("ballOption", 1);
        selBallSpeed = getIntent().getIntExtra("ballSpeed", 0);
        selBackground = getIntent().getIntExtra("backgroundOption", 0);

        setContentView(R.layout.activity_main);

        mGameView = (GameView)findViewById(R.id.gamearea);
        mGameView.setStatusView((TextView)findViewById(R.id.text));
        mGameView.setScoreView((TextView)findViewById(R.id.score));

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("root");
        mHighScoreView = findViewById(R.id.highScore);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                System.out.println("name: " + selectedDifficulty
                + " value: " + dataSnapshot.child(selectedDifficulty).getValue(Long.class));

                highScoreForThisDif = dataSnapshot.child(selectedDifficulty).getValue(Long.class);
                mHighScoreView.setText(getResources().getString(R.string.high_score)  + highScoreForThisDif);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });



        //set parameters
        mGameView.setSelectedDifficulty(selectedDifficulty);
        mGameView.setSelPlayer(selPlayer);
        mGameView.setSelOpp(selOpp);
        mGameView.setSelOppSpeed(selOppSpeed);
        mGameView.setSelBall(selBall);
        mGameView.setSelBallSpeed(selBallSpeed);
        mGameView.setSelBackground(selBackground);

        this.startGame(mGameView, null, savedInstanceState);
    }

    private void startGame(GameView gView, GameThread gThread, Bundle savedInstanceState) {

        //Set up a new game, we don't care about previous states
        mGameThread = new Game(mGameView);
        mGameView.setThread(mGameThread);
        mGameThread.setState(GameThread.STATE_READY);
        mGameView.startSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
    }

    /*
     * Activity state functions
     */

    @Override
    protected void onPause() {
        super.onPause();

        if(mGameThread.getMode() == GameThread.STATE_RUNNING) {
            mGameThread.setState(GameThread.STATE_PAUSE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        setNewHighScore(mGameView.getCurrentScore());

        mGameView.cleanup();
        mGameView.removeSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        mGameThread = null;
        mGameView = null;
    }

    private void setNewHighScore(Long score){
        if(score > highScoreForThisDif){
            myRef.child(selectedDifficulty).setValue(score);
        }
    }

    /*
     * UI Functions
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                mGameThread.doStart();
                return true;
            case MENU_STOP:
                mGameThread.setState(GameThread.STATE_LOSE,  getText(R.string.message_stopped));
                return true;
            case MENU_RESUME:
                mGameThread.unpause();
                return true;
        }

        return false;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing if nothing is selected
    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.