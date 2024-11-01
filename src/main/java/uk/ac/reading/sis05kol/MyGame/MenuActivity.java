package uk.ac.reading.sis05kol.MyGame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * @author Steven Whitby
 * class for main game menu
 */
public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        Button playButton = findViewById(R.id.easy_mode);
        Button practiceButton = findViewById(R.id.medium_mode);
        Button highScoreButton = findViewById(R.id.hard_mode);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the game activity
                Intent intent = new Intent(MenuActivity.this, DifficultyActivity.class);
                startActivity(intent);
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the custom param activity
                Intent intent = new Intent(MenuActivity.this, CustomParamActivity.class);
                startActivity(intent);
            }
        });

        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the high score activity
                Intent intent = new Intent(MenuActivity.this, HighScoreActivity.class);
                startActivity(intent);
            }
        });

    }
}
