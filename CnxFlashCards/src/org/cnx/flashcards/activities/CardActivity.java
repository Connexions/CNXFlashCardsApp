package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.TITLE;

import java.util.ArrayList;

import org.cnx.flashcards.R;
import org.cnx.flashcards.R.id;
import org.cnx.flashcards.database.CardProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public abstract class CardActivity extends SherlockActivity implements
        OnTouchListener {

    protected ArrayList<String[]> definitions;
    protected int currentCard = 0;
    protected String id;

    protected Button nextCardButton;
    private Button prevCardButton;

    private TextView termText;
    protected TextView deckPositionText;
    
    protected SeekBar positionBar;
    

    SimpleOnGestureListener simpleGestureListener = new SimpleOnGestureListener() {
        public boolean onSingleTapUp(MotionEvent e) {
            setMeaningText();
            return true;
        };

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            if (e1.getX() > e2.getX())
                nextCard();
            else
                prevCard();

            return true;
        };
    };

    GestureDetector gestureDetector;

    
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Unique id of this deck of cards
        id = getIntent().getStringExtra(DECK_ID);

        // Get UI elements
        termText = (TextView) findViewById(R.id.termText);
        nextCardButton = (Button) findViewById(R.id.nextCardButton);
        prevCardButton = (Button) findViewById(R.id.prevCardButton);
        deckPositionText = (TextView) findViewById(R.id.deckPositionText);
        positionBar = (SeekBar)findViewById(R.id.deckPositionBar);
        
        

        loadCards(id);
        boolean valid = checkIfValidDeck();
        
        if(!valid) {
            setResult(RESULT_INVALID_DECK);
            finish();
            return;
        }
        
        if(definitions.size()==1) {
            positionBar.setMax(1);
            positionBar.setProgress(1);
        }
        else {
            positionBar.setMax(definitions.size()-1);
            positionBar.setProgress(currentCard);
        }
        
        termText.setText(definitions.get(currentCard)[0]);
        deckPositionText.setText(currentCard + 1 + "/" + definitions.size());

        gestureDetector = new GestureDetector(this, simpleGestureListener);

        setMeaningText();

        nextCardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextCard();
            }
        });

        // Previous button is null in quiz mode (TODO: Make this a bit more
        // elegant)
        if (prevCardButton != null) {
            prevCardButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    prevCard();
                }
            });
        }
        
        positionBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            boolean tracking = true;
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tracking = false;
                
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tracking = true;
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(tracking) {
                    currentCard = progress;
                    displayCard(currentCard);
                }
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
    

    private void loadCards(String id) {
        String[] columns = { TERM, MEANING };
        String selection = DECK_ID + " = '" + id + "'";

        Cursor cardsCursor = getContentResolver().query(
                CardProvider.CONTENT_URI, columns, selection, null, null);
        cardsCursor.moveToFirst();

        definitions = new ArrayList<String[]>();

        if (!cardsCursor.isAfterLast()) {
            do {
                definitions
                        .add(new String[] {
                                cardsCursor.getString(cardsCursor
                                        .getColumnIndex(TERM)),
                                cardsCursor.getString(cardsCursor
                                        .getColumnIndex(MEANING)) });
            } while (cardsCursor.moveToNext());
        }

        cardsCursor.close();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean consumed = gestureDetector.onTouchEvent(event);
        Log.d(TAG, "onTouch triggered. Consumed = " + consumed);
        return consumed;
    }

    // Move to the next card
    protected void nextCard() {
        if (definitions != null && definitions.size() != 0) {
            currentCard++;
            if (currentCard >= definitions.size())
                currentCard = 0;
            displayCard(currentCard);
        }
    }

    // Move to the previous card
    protected void prevCard() {
        if (definitions != null && definitions.size() != 0) {
            currentCard--;
            if (currentCard < 0)
                currentCard = definitions.size() - 1;
            displayCard(currentCard);
        }
    }
    
    
    protected void displayCard(int card) {
        if(definitions.size()==1) return;
        termText.setText(definitions.get(currentCard)[0]);
        setMeaningText();
        deckPositionText.setText(currentCard + 1 + "/" + definitions.size());
        positionBar.setProgress(currentCard);
    }
    

    // Each of the modes has its own unique way of displaying the meanings
    abstract void setMeaningText();
    abstract boolean checkIfValidDeck();
}
