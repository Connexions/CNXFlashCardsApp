package org.cnx.flashcards;

import android.os.Bundle;

public class QuizCardActivity extends CardActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.quizmode);
		super.onCreate(savedInstanceState);
	}

	@Override
	void setMeaningText() {
		
	}

}
