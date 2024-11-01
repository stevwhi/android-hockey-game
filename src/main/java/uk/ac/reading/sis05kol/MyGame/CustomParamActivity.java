package uk.ac.reading.sis05kol.MyGame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steven Whitby
 * class for choosing custom game parameters
 */
public class CustomParamActivity extends AppCompatActivity {
    private static final String TAG = "SW";
    private final int NUM_PARAMS = 6;

    // Declare variables for the UI elements
    private RadioButton playerOption1, playerOption2, playerOption3;
    private RadioButton opponentOption1, opponentOption2, opponentOption3;
    private SeekBar opponentSpeedSeekBar, ballSpeedSeekBar;
    private RadioButton ballOption1, ballOption2, ballOption3;
    private TextView opponentSpeedValue, ballSpeedValue;
    private RadioButton backgroundOption1, backgroundOption2, backgroundOption3;
    private Button saveGameButton, loadGameButton, startGameButton;
    private RadioGroup playerOptionsGroup, opponentOptionsGroup, ballOptionsGroup, backgroundOptionsGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_param);

        // Initialize UI elements
        //player
        playerOptionsGroup = findViewById(R.id.player_options);
        playerOption1 = findViewById(R.id.player_option_1);
        playerOption2 = findViewById(R.id.player_option_2);
        playerOption3 = findViewById(R.id.player_option_3);

        //opponent
        opponentOptionsGroup = findViewById(R.id.opponent_options);
        opponentOption1 = findViewById(R.id.opponent_option_1);
        opponentOption2 = findViewById(R.id.opponent_option_2);
        opponentOption3 = findViewById(R.id.opponent_option_3);
        opponentSpeedSeekBar = findViewById(R.id.opponent_speed_seekbar);
        opponentSpeedValue = findViewById(R.id.opponent_speed_value);

        //ball
        ballOptionsGroup = findViewById(R.id.ball_options);
        ballOption1 = findViewById(R.id.ball_option_1);
        ballOption2 = findViewById(R.id.ball_option_2);
        ballOption3 = findViewById(R.id.ball_option_3);
        ballSpeedSeekBar = findViewById(R.id.ball_speed_seekbar);
        ballSpeedValue = findViewById(R.id.ball_speed_value);

        //background
        backgroundOptionsGroup = findViewById(R.id.background_options);
        backgroundOption1 = findViewById(R.id.background_option_1);
        backgroundOption2 = findViewById(R.id.background_option_2);
        backgroundOption3 = findViewById(R.id.background_option_3);

        //buttons
        saveGameButton = findViewById(R.id.save_game_button);
        loadGameButton = findViewById(R.id.load_game_button);
        startGameButton = findViewById(R.id.start_game_button);



        //shared pref
        SharedPreferences sharedPref = getSharedPreferences("cust_param_prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        // Set the initial values for opponent speed and ball speed

        int savedPlayerOption = sharedPref.getInt("playerOption", 0);
        Log.d("MyApp", "Saved player option: " + savedPlayerOption);

        setPlayerOption(sharedPref.getInt("playerOption", 0));
        setOpponentOption(sharedPref.getInt("opponentOption", 0));
        opponentSpeedSeekBar.setProgress(sharedPref.getInt("opponentSpeedOption", 5));
        opponentSpeedValue.setText(getString(R.string.opponent_speed_value, sharedPref.getInt("opponentSpeedOption", 5)));
        setBallOption(sharedPref.getInt("ballOption", 0));
        ballSpeedSeekBar.setProgress(sharedPref.getInt("ballSpeedOption", 5));
        ballSpeedValue.setText(getString(R.string.ball_speed_value, sharedPref.getInt("ballSpeedOption", 5)));
        setBackgroundOption(sharedPref.getInt("backgroundOption", 0));

        //player selector listener
        playerOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Deselect all the other radio buttons
                playerOption1.setChecked(false);
                playerOption2.setChecked(false);
                playerOption3.setChecked(false);
                // Select the currently checked radio button
                RadioButton checkedRadioButton = findViewById(checkedId);
                checkedRadioButton.setChecked(true);

                editor.putInt("playerOption", getPlayerOption());
                editor.commit();
            }
        });

        //opponent selector listener
        opponentOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Deselect all the other radio buttons
                opponentOption1.setChecked(false);
                opponentOption2.setChecked(false);
                opponentOption3.setChecked(false);
                // Select the currently checked radio button
                RadioButton checkedRadioButton = findViewById(checkedId);
                checkedRadioButton.setChecked(true);

                editor.putInt("opponentOption", getOpponentOption());
                editor.commit();
            }
        });

        // Set the listeners for the seekbars
        opponentSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the opponent speed value based on the seekbar progress
                opponentSpeedValue.setText(getString(R.string.opponent_speed_value, progress));

                editor.putInt("opponentSpeedOption", progress);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //ball selector listener
        ballOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Deselect all the other radio buttons
                ballOption1.setChecked(false);
                ballOption2.setChecked(false);
                ballOption3.setChecked(false);
                // Select the currently checked radio button
                RadioButton checkedRadioButton = findViewById(checkedId);
                checkedRadioButton.setChecked(true);

                editor.putInt("ballOption", getBallOption());
                editor.commit();
            }
        });

        ballSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the ball speed value based on the seekbar progress
                ballSpeedValue.setText(getString(R.string.ball_speed_value, progress));

                editor.putInt("ballSpeedOption", progress);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //background selector listener
        backgroundOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Deselect all the other radio buttons
                backgroundOption1.setChecked(false);
                backgroundOption2.setChecked(false);
                backgroundOption3.setChecked(false);
                // Select the currently checked radio button
                RadioButton checkedRadioButton = findViewById(checkedId);
                checkedRadioButton.setChecked(true);

                editor.putInt("backgroundOption", getBackgroundOption());
                editor.commit();
            }
        });

        // Set the listener for the save game button
        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameParams gp = new GameParams(getPlayerOption(),
                                                getOpponentOption(),
                                                opponentSpeedSeekBar.getProgress(),
                                                getBallOption(),
                                                ballSpeedSeekBar.getProgress(),
                                                getBackgroundOption());

                saveSettingsToFile(gp);
            }
        });

        // Set the listener for the save game button
        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettingsFromFile();
            }
        });

        // Set the listener for the start game button
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the MainActivity
                Intent intent = new Intent(CustomParamActivity.this, MainActivity.class);

                // Add the chosen parameters as extras to the intent
                intent.putExtra("playerOption", getPlayerOption());
                intent.putExtra("opponentOption", getOpponentOption());
                intent.putExtra("opponentSpeed", opponentSpeedSeekBar.getProgress());
                intent.putExtra("ballOption", getBallOption());
                intent.putExtra("ballSpeed", ballSpeedSeekBar.getProgress());
                intent.putExtra("backgroundOption", getBackgroundOption());
                intent.putExtra("difficulty", "custom");

                // Start the MainActivity
                startActivity(intent);
            }
        });
    }

    //file management functions

    private void saveSettingsToFile(final GameParams params) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save game settings");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename = input.getText().toString();
                //check if file already exists
                File file = new File(getFilesDir(), filename);
                if (file.exists() || filename.equals("")) {
                    // File already exists, prompt user to choose a different name
                    Toast.makeText(CustomParamActivity.this, "File already exists, please choose a different name", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(params);
                    oos.close();
                    Toast.makeText(CustomParamActivity.this, "Settings saved to " + filename, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(TAG, "Error saving game settings", e);
                    Toast.makeText(CustomParamActivity.this, "Error saving game settings", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadSettingsFromFile() {
        File[] files = getFilesDir().listFiles();
        if (files != null) {
            for (File file : files) {
                Log.d(TAG, file.getAbsolutePath());
            }
        }
        if (files == null || files.length == 0) {
            Toast.makeText(this, "No saved games found", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Load game settings");

        final List<String> filenames = new ArrayList<>();
        for (File file : files) {
            filenames.add(file.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filenames);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename = filenames.get(which);
                try {
                    FileInputStream fis = openFileInput(filename);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    GameParams params = (GameParams) ois.readObject();
                    ois.close();
                    Toast.makeText(CustomParamActivity.this, "Settings loaded from " + filename, Toast.LENGTH_SHORT).show();
                    // create the game using settings

                    setPlayerOption(params.getSelPlayer());
                    setOpponentOption(params.getSelOpp());
                    opponentSpeedSeekBar.setProgress(params.getSelOppSpeed());
                    setBallOption(params.getSelBall());
                    ballSpeedSeekBar.setProgress(params.getSelBallSpeed());
                    setBackgroundOption(params.getSelBackground());
                } catch (IOException | ClassNotFoundException e) {
                    Log.e(TAG, "Error loading game settings", e);
                    Toast.makeText(CustomParamActivity.this, "Error loading game settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //switch functions

    /**
     * Returns the selected player option as a int.
     */
    private int getPlayerOption() {
        if (playerOption1.isChecked()) {
            return 0;
        } else if (playerOption2.isChecked()) {
            return 1;
        } else if (playerOption3.isChecked()) {
            return 2;
        } else {
            //Default to option 1 if no option is selected
            return 0;
        }
    }

    /**
     * Returns the selected opponent option as a int.
     */
    private int getOpponentOption() {
        if (opponentOption1.isChecked()) {
            return 0;
        } else if (opponentOption2.isChecked()) {
            return 1;
        } else if (opponentOption3.isChecked()) {
            return 2;
        } else {
            //Default to option 1 if no option is selected
            return 0;
        }
    }

    /**
     * Returns the selected ball option as a int.
     */
    private int getBallOption() {
        if (ballOption1.isChecked()) {
            return 0;
        } else if (ballOption2.isChecked()) {
            return 1;
        } else if (ballOption3.isChecked()) {
            return 2;
        } else {
            //Default to option 1 if no option is selected
            return 0;
        }
    }

    /**
     * Returns the selected background option as a int.
     */
    private int getBackgroundOption() {
        if (backgroundOption1.isChecked()) {
            return 0;
        } else if (backgroundOption2.isChecked()) {
            return 1;
        } else if (backgroundOption3.isChecked()) {
            return 2;
        } else {
            // Default to option 1 if no option is selected
            return 0;
        }
    }

    /**
     * set the selected player option as a int.
     */
    private void setPlayerOption(int n) {
        playerOption1.setChecked(false);
        playerOption2.setChecked(false);
        playerOption3.setChecked(false);
        switch(n){
            case 0:
                playerOption1.setChecked(true);
                break;
            case 1:
                playerOption2.setChecked(true);
                break;
            case 2:
                System.out.println("hi");
                playerOption3.setChecked(true);
                break;
        }
    }

    /**
     * set the selected opponent option as a int.
     */
    private void setOpponentOption(int n) {
        opponentOption1.setChecked(false);
        opponentOption2.setChecked(false);
        opponentOption3.setChecked(false);
        switch(n){
            case 0:
                opponentOption1.setChecked(true);
                break;
            case 1:
                opponentOption2.setChecked(true);
                break;
            case 2:
                opponentOption3.setChecked(true);
                break;
        }
    }

    /**
     * set the selected ball option as a int.
     */
    private void setBallOption(int n) {
        ballOption1.setChecked(false);
        ballOption2.setChecked(false);
        ballOption3.setChecked(false);
        switch(n){
            case 0:
                ballOption1.setChecked(true);
                break;
            case 1:
                ballOption2.setChecked(true);
                break;
            case 2:
                ballOption3.setChecked(true);
                break;
        }
    }

    /**
     * set the selected background option as a int.
     */
    private void setBackgroundOption(int n) {
        backgroundOption1.setChecked(false);
        backgroundOption2.setChecked(false);
        backgroundOption3.setChecked(false);
        switch(n){
            case 0:
                backgroundOption1.setChecked(true);
                break;
            case 1:
                backgroundOption2.setChecked(true);
                break;
            case 2:
                backgroundOption3.setChecked(true);
                break;
        }
    }
}