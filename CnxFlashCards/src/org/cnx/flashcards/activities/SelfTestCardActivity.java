package org.cnx.flashcards.activities;

import org.cnx.flashcards.R;
import org.cnx.flashcards.R.id;
import org.cnx.flashcards.R.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SelfTestCardActivity extends CardActivity {

    private TextView meaningText;
    boolean revealed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cards);

        meaningText = (TextView) findViewById(R.id.meaningText);
        meaningText.setTextColor(Color.WHITE);
        meaningText.setOnTouchListener(this);
        meaningText.setClickable(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    void setMeaningText() {
        if (!revealed) {
            meaningText.setText("Tap to see definition...");
            revealed = true;
        } else {
            meaningText.setText(definitions.get(currentCard)[1]);
        }
    }

    @Override
    protected void nextCard() {
        super.nextCard();

    }

    @Override
    protected void prevCard() {
        super.prevCard();

    }

    @Override
    boolean checkIfValidDeck() {
        return definitions.size() > 0;
    }
    
    @Override
    protected void displayCard(int card) {
        revealed = false;
        super.displayCard(card);
    }
}
