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
    TextView previousHighScoreText;
    TextView highScoreText;
    
    int score = 0;
    String id;
    
    int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizend);
        
        scoreTextView = (TextView)findViewById(R.id.finalScoreText);
        finishButton = (Button)findViewById(R.id.exitButton);
        previousHighScoreText = (TextView)findViewById(R.id.previousHighScoreText);
        highScoreText = (TextView)findViewById(R.id.highScoreText);
        
        id = getIntent().getStringExtra(DECK_ID);
        
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
            highScore = highScoreCursor.getInt(highScoreCursor.getColumnIndex(HIGH_SCORE));
        }
        
        highScoreCursor.close();
        
        previousHighScoreText.setText("High score: " + highScore);
        
        if(score > highScore) {
            highScoreText.setText("You got a new high score!");
            
            ContentValues values = new ContentValues();
            values.put(HIGH_SCORE, score);
            getContentResolver().update(DeckProvider.CONTENT_URI, values, selection, null);
        }
        else {
            highScoreText.setText("You didn't get a new high score.");
        }
        
        
        
    }
    
}
