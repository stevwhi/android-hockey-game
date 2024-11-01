package uk.ac.reading.sis05kol.MyGame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * class for custom game view
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
	private volatile GameThread thread;

	//private SensorEventListener sensorAccelerometer;

	//Handle communication from the GameThread to the View/Activity Thread
	private Handler mHandler;
	
	//Pointers to the views
	private TextView mScoreView;
	private TextView mHighScoreView;
	private TextView mStatusView;

    Sensor accelerometer;
    Sensor magnetometer;

	private String selectedDifficulty;
	private int selPlayer, selOpp, selOppSpeed, selBall, selBallSpeed, selBackground;
	private long currentScore;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		//Set up a handler for messages from GameThread
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message m) {
				if(m.getData().getBoolean("score")) {
					mScoreView.setText("score: " + m.getData().getString("text"));
					currentScore = m.getData().getLong("scoreLong");
				}
				else {
					//So it is a status
                    int i = m.getData().getInt("viz");
                    switch(i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }

                    mStatusView.setText(m.getData().getString("text"));
				}
 			}
		};
	}
	
	//Used to release any resources.
	public void cleanup() {
		this.thread.setRunning(false);
		this.thread.cleanup();
		
		this.removeCallbacks(thread);
		thread = null;
		
		this.setOnTouchListener(null);
		
		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}
	
	/*
	 * Setters and Getters
	 */

	public void setThread(GameThread newThread) {

		thread = newThread;

		setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
		});

        setClickable(true);
		setFocusable(true);
	}
	
	public GameThread getThread() {
		return thread;
	}

	public TextView getStatusView() {
		return mStatusView;
	}

	public void setStatusView(TextView mStatusView) {
		this.mStatusView = mStatusView;
	}
	
	public TextView getScoreView() {
		return mScoreView;
	}

	public void setScoreView(TextView mScoreView) {
		this.mScoreView = mScoreView;
	}

	public void setmHighScoreView(TextView mHighScoreView) { this.mHighScoreView = mScoreView; }
	

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	
	/*
	 * Screen functions
	 */
	
	//ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(thread!=null) {
			if (!hasWindowFocus)
				thread.pause();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if(thread!=null) {
			thread.setRunning(true);
			
			if(thread.getState() == Thread.State.NEW){
				//Just start the new thread
				thread.start();
				thread.startTimers();
			}
			else {
				if(thread.getState() == Thread.State.TERMINATED){
					//Start a new thread
					//Should be this to update screen with old game: new GameThread(this, thread);
					//The method should set all fields in new thread to the value of old thread's fields 
					thread = new Game(this);
					thread.setRunning(true);
					thread.start();
					thread.startTimers();
				}
			}
		}
	}
	
	//Always called once after surfaceCreated. Tell the GameThread the actual size
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(thread!=null) {
			thread.setSurfaceSize(width, height);			
		}
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused on even stopped.
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retryGameThreadTimer = true;

		if(thread!=null) {
			thread.setRunning(false);
		}

		thread.stopTimers();

		//join the thread with this thread
		while (retryGameThreadTimer) {
			try {
				if(thread!=null) {
					thread.join();
				}
				retryGameThreadTimer = false;
			} 
			catch (InterruptedException e) {
				//naugthy, ought to do something...
			}
		}
	}
	
	/*
	 * Accelerometer
	 */

	public void startSensor(SensorManager sm) {

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }
	
	public void removeSensor(SensorManager sm) {
        sm.unregisterListener(this);

        accelerometer = null;
        magnetometer = null;
	}

    //A sensor has changed, let the thread take care of it
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(thread!=null) {
            thread.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

	//getters and setters ----------------------------------------
	public long getCurrentScore() { return currentScore; }
	public void setSelectedDifficulty(String selectedDifficulty) { this.selectedDifficulty = selectedDifficulty; }
	public String getSelectedDifficulty() {
		return this.selectedDifficulty;
	}
	public int getSelPlayer() { return selPlayer; }
	public void setSelPlayer(int selPlayer) { this.selPlayer = selPlayer; }
	public int getSelOpp() { return selOpp; }
	public void setSelOpp(int selOpp) { this.selOpp = selOpp; }
	public int getSelOppSpeed() { return selOppSpeed; }
	public void setSelOppSpeed(int selOppSpeed) { this.selOppSpeed = selOppSpeed; }
	public int getSelBall() { return selBall; }
	public void setSelBall(int selBall) { this.selBall = selBall; }
	public int getSelBallSpeed() { return selBallSpeed; }
	public void setSelBallSpeed(int selBallSpeed) { this.selBallSpeed = selBallSpeed; }
	public int getSelBackground() { return selBackground; }
	public void setSelBackground(int selBackground) { this.selBackground = selBackground; }
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
