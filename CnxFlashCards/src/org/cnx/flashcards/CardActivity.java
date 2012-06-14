package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.*;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;


public class CardActivity extends SherlockActivity {
	
	private ArrayList<String[]> definitions;
	private int currentCard = 0;
	private String id;
	
	private Button nextCardButton;
	private Button prevCardButton;
	
	private TextView termText;
	private TextView meaningText;
	private TextView deckPositionText;
	
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);
		
		id = getIntent().getStringExtra(DECK_ID);
		
		loadCards(id);
		
		if(definitions.size() == 0) {
			/*parseResultsText.setTextColor(Color.RED);
			parseResultsText.setText("No cards found.");*/
		}
		else {
			
			String[] projection = {DECK_ID, TITLE}; 
			Cursor titlesCursor = getContentResolver().query(DeckProvider.CONTENT_URI, projection, null, null, null);
			titlesCursor.moveToFirst();
			
			final ArrayList<String> idList = new ArrayList<String>();
			ArrayList<String> titlesList = new ArrayList<String>();
			
			if(!titlesCursor.isAfterLast()) {
				do {
					idList.add(new String(titlesCursor.getString(0)));
					titlesList.add(new String(titlesCursor.getString(1)));
				} while (titlesCursor.moveToNext());
			}
			
			final String[] titles = titlesList.toArray(new String[titlesList.size()]);

			AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
			builder.setTitle("Pick a deck");
			builder.setItems(titles, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	loadCards(idList.get(item));
			    	currentCard = 0;
					termText.setText(definitions.get(currentCard)[0]);
					meaningText.setText(definitions.get(currentCard)[1]);
					deckPositionText.setText(currentCard+1 + "/" + definitions.size());
			    }
			});
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	
	private void loadCards(String id) {
		String[] columns = {TERM, MEANING};
		String selection = DECK_ID + " = '" + id + "'";
		
		Cursor cardsCursor = getContentResolver().query(CardProvider.CONTENT_URI, columns, selection, null, null);
		cardsCursor.moveToFirst();
		
		definitions = new ArrayList<String[]>();
		
		if(!cardsCursor.isAfterLast()) {
			do {
				definitions.add(new String[]{cardsCursor.getString(0), cardsCursor.getString(1)});
			} while (cardsCursor.moveToNext());
		}
		
		cardsCursor.close();
	}
}
