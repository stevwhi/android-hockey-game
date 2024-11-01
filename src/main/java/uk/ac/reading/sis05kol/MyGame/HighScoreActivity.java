package uk.ac.reading.sis05kol.MyGame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Steven Whitby
 * class for viewing high scores
 */
public class HighScoreActivity extends Activity {
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private Button easy, medium, hard, custom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_high_score);

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("root");

        easy = findViewById(R.id.easy_mode);
        medium = findViewById(R.id.medium_mode);
        hard = findViewById(R.id.hard_mode);
        custom = findViewById(R.id.custom_mode);


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long easyValue = dataSnapshot.child("easy").getValue(Long.class);
                Long mediumValue = dataSnapshot.child("medium").getValue(Long.class);
                Long hardValue = dataSnapshot.child("hard").getValue(Long.class);
                Long customValue = dataSnapshot.child("custom").getValue(Long.class);

                easy.setText(getResources().getString(R.string.easy_mode) + easyValue);
                medium.setText(getResources().getString(R.string.medium_mode) + mediumValue);
                hard.setText(getResources().getString(R.string.hard_mode) + hardValue);
                custom.setText(getResources().getString(R.string.custom_mode) + customValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });



    }
}