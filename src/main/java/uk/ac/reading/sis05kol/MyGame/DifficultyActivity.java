package uk.ac.reading.sis05kol.MyGame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * @author Steven Whitby
 * class for choosing game difficulty
 */
public class DifficultyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_difficulty);

        Button easyBtn = findViewById(R.id.easy_button);
        Button mediumBtn = findViewById(R.id.medium_button);
        Button hardBtn = findViewById(R.id.hard_button);

        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity(getResources().getString(R.string.difEasy));
            }
        });

        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity(getResources().getString(R.string.difMedium));
            }
        });

        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity(getResources().getString(R.string.difHard));
            }
        });
    }

    private void startMainActivity(String difficulty) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

}
