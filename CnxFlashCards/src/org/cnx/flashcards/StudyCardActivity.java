package org.cnx.flashcards;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StudyCardActivity extends CardActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.cards);
		
		super.onCreate(savedInstanceState);
		
		meaningText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCurrentDefinition();
			}
		});
	}

	@Override
	void setMeaningText() {
		showCurrentDefinition();
	}
	
	
	
}
