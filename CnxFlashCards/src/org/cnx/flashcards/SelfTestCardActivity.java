package org.cnx.flashcards;

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
		
		meaningText = (TextView)findViewById(R.id.meaningText);
		meaningText.setTextColor(Color.WHITE); 
		meaningText.setOnTouchListener(this);
		meaningText.setClickable(true);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	void setMeaningText() {
		if(!revealed) {
			meaningText.setText("Tap to see definition...");
			revealed = true;
		}
		else {
			meaningText.setText(definitions.get(currentCard)[1]);
		}
	}
	
	
	@Override
	protected void nextCard() {
		revealed = false;
		super.nextCard();
		
	}
	
	
	@Override
	protected void prevCard() {
		revealed = false;
		super.prevCard();
		
	}
}
