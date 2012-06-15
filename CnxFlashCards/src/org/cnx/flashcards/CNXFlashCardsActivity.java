/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TEST_ID;
import static org.cnx.flashcards.Constants.TITLE;

import java.util.ArrayList;

import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;


public class CNXFlashCardsActivity extends SherlockActivity {
	
	private Button searchButton;
	private Button parseTestButton;
	private Button showCardsButton;
	private TextView parseResultsText;
	
	private EditText searchInput;
	
	
	private String id = TEST_ID;
	
	
	
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
        
        
        // Parses the target CNXML file (currently just the offline test file)
        parseTestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				id = searchInput.getText().toString();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

				
				ModuleToDatabaseParser parser = new ModuleToDatabaseParser(getApplicationContext());
				ParseResult result = parser.parse(id);
				switch (result) {
				case SUCCESS:
					parseResultsText.setText("Parsing succeeded, terms in database");
					break;
					
				case DUPLICATE:
					parseResultsText.setText("Parsing failed. Duplicate.");
					break;
					
				case NO_NODES:
					parseResultsText.setText("Parsing failed. No definitions in module.");
				}
			}
		});
        
        
        showCardsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
				    	id = idList.get(item);
				    	Intent cardIntent = new Intent(getApplicationContext(), CardActivity.class);
						Log.d(TAG, id);
						cardIntent.putExtra(DECK_ID, id);
						startActivity(cardIntent);
				    }
				});
				
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
        
        // Launch search
        searchButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				id = searchInput.getText().toString();
				Intent searchIntent = new Intent(CNXFlashCardsActivity.this, SearchActivity.class);
				searchIntent.putExtra("SEARCH_TERM", id);
				startActivity(searchIntent);
			}
		});
    }
    
    
    /** Called when Activity created, loads the ActionBar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//MenuInflater inflater = getSupportMenuInflater();
    	//inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
    }
}