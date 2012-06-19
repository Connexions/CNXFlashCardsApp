package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.TITLE;

import java.util.ArrayList;

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
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;


public class CardActivity extends SherlockActivity implements OnTouchListener {
	
	private ArrayList<String[]> definitions;
	private int currentCard = 0;
	private String id;
	
	private Button nextCardButton;
	private Button prevCardButton;
	
	private TextView termText;
	private TextView meaningText;
	private TextView deckPositionText;
	
	SimpleOnGestureListener simpleGestureListener = new SimpleOnGestureListener() {
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {	
			
			if(e1.getX() > e2.getX())
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
		setContentView(R.layout.cards);
		
		// Unique id of this deck of cards
		id = getIntent().getStringExtra(DECK_ID);
		
		// Get UI elements
		meaningText = (TextView)findViewById(R.id.meaningText);
        termText = (TextView)findViewById(R.id.termText);
        nextCardButton = (Button)findViewById(R.id.nextCardButton);
        prevCardButton = (Button)findViewById(R.id.prevCardButton);
        deckPositionText = (TextView)findViewById(R.id.deckPositionText);
        
		loadCards(id);
		termText.setText(definitions.get(currentCard)[0]);
		meaningText.setText("Tap to see definition...");
		deckPositionText.setText(currentCard+1 + "/" + definitions.size());
		
		gestureDetector = new GestureDetector(this, simpleGestureListener);
		
		// Need to set the text colour or else it changes on swiping on phone used for testing
		meaningText.setTextColor(Color.WHITE); 
		meaningText.setOnTouchListener(this);
		meaningText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDefinition();
			}
		});
		
		nextCardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextCard();
			}
		});
        
        prevCardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prevCard();
			}
		});
	}
	
	
	private void loadCards(String id) {
		String[] columns = {TERM, MEANING};
		String selection = DECK_ID + " = '" + id + "'";
		
		Cursor cardsCursor = getContentResolver().query(CardProvider.CONTENT_URI, columns, selection, null, null);
		cardsCursor.moveToFirst();
		
		definitions = new ArrayList<String[]>();
		
		if(!cardsCursor.isAfterLast()) {
			do {
				definitions.add(new String[]{cardsCursor.getString(cardsCursor.getColumnIndex(TERM)), cardsCursor.getString(cardsCursor.getColumnIndex(MEANING))});
			} while (cardsCursor.moveToNext());
		}
		
		cardsCursor.close();
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean consumed = gestureDetector.onTouchEvent(event);
		return consumed;
	}
	
	
	private void nextCard() {
		if(definitions != null && definitions.size() != 0) {
			currentCard++;
			if(currentCard >= definitions.size()) currentCard = 0;
			termText.setText(definitions.get(currentCard)[0]);
			meaningText.setText("Tap to see definition...");
			deckPositionText.setText(currentCard+1 + "/" + definitions.size());
		}
	}
	
	
	private void prevCard() {
		if(definitions != null && definitions.size() != 0) {
			currentCard--;
			if(currentCard < 0) currentCard = definitions.size()-1;
			termText.setText(definitions.get(currentCard)[0]);
			meaningText.setText("Tap to see definition...");
			deckPositionText.setText(currentCard+1 + "/" + definitions.size());
		}
	}
	
	
	private void showDefinition() {
		meaningText.setText(definitions.get(currentCard)[1]);
	}
}
