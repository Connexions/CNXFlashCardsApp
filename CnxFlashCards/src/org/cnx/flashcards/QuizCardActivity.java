package org.cnx.flashcards;

import android.os.Bundle;

public class QuizCardActivity extends CardActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.cards);
		super.onCreate(savedInstanceState);
	}

	@Override
	void setMeaningText() {
		
	}

}
