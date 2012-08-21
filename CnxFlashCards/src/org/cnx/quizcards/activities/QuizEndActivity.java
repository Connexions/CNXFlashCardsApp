/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards.activities;

import static org.cnx.quizcards.Constants.DECK_ID;
import static org.cnx.quizcards.Constants.HIGH_SCORE;
import static org.cnx.quizcards.Constants.SCORE;
import static org.cnx.quizcards.Constants.TITLE;

import org.cnx.quizcards.R;
import org.cnx.quizcards.database.DeckProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;


public class QuizEndActivity extends SherlockActivity {
    
    TextView scoreTextView;
    Button finishButton;
    TextView previousHighScoreText;
    TextView highScoreText;
    TextView titleText;
    
    int score = 0;
    String id;
    
    int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_end);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        scoreTextView = (TextView)findViewById(R.id.finalScoreText);
        finishButton = (Button)findViewById(R.id.exitButton);
        previousHighScoreText = (TextView)findViewById(R.id.previousHighScoreText);
        highScoreText = (TextView)findViewById(R.id.highScoreText);
        titleText = (TextView)findViewById(R.id.titleText);
        
        id = getIntent().getStringExtra(DECK_ID);
        
        score = getIntent().getIntExtra(SCORE, 0);
        scoreTextView.setText("Score: " + score);
        
        getDetails();
        
        finishButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                finish();
                
            }
        });
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private void getDetails() {
        String[] projection = { TITLE, HIGH_SCORE };
        String selection = BaseColumns._ID + " = " + "'" + id + "'";
        Cursor highScoreCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, selection, null, null);
        highScoreCursor.moveToFirst();
        
        titleText.setText(highScoreCursor.getString(highScoreCursor.getColumnIndex(TITLE)));
        highScore = highScoreCursor.getInt(highScoreCursor.getColumnIndex(HIGH_SCORE));
        
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
