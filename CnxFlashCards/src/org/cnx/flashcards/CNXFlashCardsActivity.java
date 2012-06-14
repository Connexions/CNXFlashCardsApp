/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.*;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;


public class CNXFlashCardsActivity extends SherlockActivity {
	
	private Button searchButton;
	private Button parseTestButton;
	private Button showCardsButton;
	private Button nextCardButton;
	private Button prevCardButton;
	
	private TextView parseResultsText;
	private TextView termText;
	private TextView meaningText;
	private TextView deckPositionText;
	
	private EditText searchInput;
	
	private ArrayList<String[]> definitions;
	private int currentCard = 0;
	
	private String id = null;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        searchButton = (Button)findViewById(R.id.searchButton);
        parseTestButton = (Button)findViewById(R.id.parseTestButton);
        showCardsButton = (Button)findViewById(R.id.showCardsButton);        
        searchInput = (EditText)findViewById(R.id.searchInput);
        parseResultsText = (TextView)findViewById(R.id.parsingResultText);
        meaningText = (TextView)findViewById(R.id.meaningText);
        termText = (TextView)findViewById(R.id.termText);
        nextCardButton = (Button)findViewById(R.id.nextCardButton);
        prevCardButton = (Button)findViewById(R.id.prevCardButton);
        deckPositionText = (TextView)findViewById(R.id.deckPositionText);
        
        nextCardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(definitions != null && definitions.size() != 0) {
					currentCard++;
					if(currentCard >= definitions.size()) currentCard = 0;
					termText.setText(definitions.get(currentCard)[0]);
					meaningText.setText(definitions.get(currentCard)[1]);
					deckPositionText.setText(currentCard+1 + "/" + definitions.size());
				}
			}
		});
        
        
        prevCardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(definitions != null && definitions.size() != 0) {
					currentCard--;
					if(currentCard < 0) currentCard = definitions.size()-1;
					termText.setText(definitions.get(currentCard)[0]);
					meaningText.setText(definitions.get(currentCard)[1]);
					deckPositionText.setText(currentCard+1 + "/" + definitions.size());
				}
			}
		});
        
        
        // Parses the target CNXML file (currently just the offline test file)
        parseTestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(id == null) {
					parseResultsText.setText("Haven't set an id.");
					return;
				}
				
				ModuleToDatabaseParser parser = new ModuleToDatabaseParser(getApplicationContext());
				boolean success = parser.parse(id);
				if(success)
					parseResultsText.setText("Parsing succeeded, terms in database");
				else
					parseResultsText.setText("Parsing failed. No nodes.");
			}
		});
        
        
        showCardsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadCards(id);
				
				if(definitions.size() == 0) {
					parseResultsText.setTextColor(Color.RED);
					parseResultsText.setText("No cards found.");
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

					AlertDialog.Builder builder = new AlertDialog.Builder(CNXFlashCardsActivity.this);
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
		});
        
        
        // Launch search (currently disabled)
        searchButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				//resultsView.loadUrl("http://m.cnx.org/content/search?words=" + searchInput.getText().toString());
				id = searchInput.getText().toString();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
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
				definitions.add(new String[]{cardsCursor.getString(0), cardsCursor.getString(1)});
			} while (cardsCursor.moveToNext());
		}
		
		cardsCursor.close();
	}
    
    
    /** Called when Activity created, loads the ActionBar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//MenuInflater inflater = getSupportMenuInflater();
    	//inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
    }
}