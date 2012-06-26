package org.cnx.flashcards;
import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.HIGH_SCORE;
import static org.cnx.flashcards.Constants.SCORE;
import static org.cnx.flashcards.Constants.TAG;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;


public class QuizEndActivity extends SherlockActivity {
    
    TextView scoreTextView;
    Button finishButton;
    
    int score = 0;
    String id;
    
    int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizend);
        
        scoreTextView = (TextView)findViewById(R.id.finalScoreText);
        finishButton = (Button)findViewById(R.id.exitButton);
        
        id = getIntent().getStringExtra(DECK_ID);
        Log.d(TAG, "id is " +id);
        
        score = getIntent().getIntExtra(SCORE, 0);
        scoreTextView.setText("Score: " + score);
        
        checkHighScore();
        
        finishButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                finish();
                
            }
        });
    }
    
    
    private void checkHighScore() {
        String[] projection = { HIGH_SCORE };
        String selection = DECK_ID + " = " + "'" + id + "'";
        Cursor highScoreCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, selection, null, null);
        highScoreCursor.moveToFirst();
        
        if(highScoreCursor.getCount() != 0) {
            Log.d(TAG, "Something on the cursor.");
            highScore = highScoreCursor.getInt(highScoreCursor.getColumnIndex(HIGH_SCORE));
        }
        
        highScoreCursor.close();
        Log.d(TAG, "Score: " + score + " High score: " + highScore);
        
        if(score > highScore) {
            ContentValues values = new ContentValues();
            values.put(HIGH_SCORE, score);
            getContentResolver().update(DeckProvider.CONTENT_URI, values, selection, null);
        }
        
        
        
    }
    
}
