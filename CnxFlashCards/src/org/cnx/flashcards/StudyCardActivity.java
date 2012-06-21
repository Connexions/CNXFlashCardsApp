package org.cnx.flashcards;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import static org.cnx.flashcards.Constants.*;

public class StudyCardActivity extends CardActivity {
	
	private TextView meaningText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.cards);
		
		meaningText = (TextView)findViewById(R.id.meaningText);
		meaningText.setTextColor(Color.WHITE); 
		meaningText.setOnTouchListener(this);
		
		super.onCreate(savedInstanceState);
	}

	
	@Override
	void setMeaningText() {
		Log.d(TAG, "Setting meaning text.");
		meaningText.setText(definitions.get(currentCard)[1]);
	}
	
	
	
}
