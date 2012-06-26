package org.cnx.flashcards;
import org.cnx.flashcards.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import static org.cnx.flashcards.Constants.*;


public class QuizEndActivity extends SherlockActivity {
    
    TextView scoreTextView;
    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizend);
        
        scoreTextView = (TextView)findViewById(R.id.finalScoreText);
        finishButton = (Button)findViewById(R.id.exitButton);
        
        finishButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
                
            }
        });
        
        int score = getIntent().getIntExtra(SCORE, 0);
        scoreTextView.setText("Score: " + score);
    }
    
}
