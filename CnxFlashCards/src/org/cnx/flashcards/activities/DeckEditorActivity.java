package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.AUTHOR;
import static org.cnx.flashcards.Constants.MODULE_ID;
import static org.cnx.flashcards.Constants.NEW_DECK;
import static org.cnx.flashcards.Constants.RESULT_DECK_DELETED;
import static org.cnx.flashcards.Constants.TITLE;

import java.net.URI;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;
import org.cnx.flashcards.database.DeckProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckEditorActivity extends SherlockActivity {
	
    Button editCardsButton;
    Button deleteDeckButton;
    String id;
    EditText titleEditText;
    EditText summaryEditText;
    EditText authorEditText;
    boolean newDeck = false;
    boolean inDatabase = false;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deckeditor);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        editCardsButton = (Button)findViewById(R.id.editCardsButton);
        titleEditText = (EditText)findViewById(R.id.editDeckName);
        summaryEditText = (EditText)findViewById(R.id.editDeckSummary);
        authorEditText = (EditText)findViewById(R.id.editDeckAuthors);
        deleteDeckButton = (Button)findViewById(R.id.deleteDeckButton);
        
        newDeck = getIntent().getBooleanExtra(NEW_DECK, false);
        
        if(!newDeck) {
	        String title = getIntent().getStringExtra(TITLE);
	        titleEditText.setText(title);
	        
	        String summary = getIntent().getStringExtra(ABSTRACT);
	        if(!summary.equals("This module doesn't have an abstract."))
	        	summaryEditText.setText(summary);
	        
	        String authors = getIntent().getStringExtra(AUTHOR);
	        authorEditText.setText(authors);
	        
	        inDatabase = true;
	        id = getIntent().getStringExtra(DECK_ID);
        }
        
        
        editCardsButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	addToDatabase();
                Intent newCardIntent = new Intent(DeckEditorActivity.this, CardListActivity.class);
                newCardIntent.putExtra(DECK_ID, id);
                startActivity(newCardIntent);
            }
        });
        
        deleteDeckButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteThisDeck();
			}
		});
    }
    
    
    @Override
    public void finish() {
    	addToDatabase();
    	
    	super.finish();
    }


	private void addToDatabase() {
		ContentValues values = new ContentValues();
    	values.put(TITLE, titleEditText.getText().toString());
    	values.put(ABSTRACT, summaryEditText.getText().toString());
    	values.put(AUTHOR, authorEditText.getText().toString());
    	if(inDatabase)
    		getContentResolver().update(DeckProvider.CONTENT_URI, values, BaseColumns._ID + " = '" + id + "'", null);
    	else {
    		Uri idUri = getContentResolver().insert(DeckProvider.CONTENT_URI, values);
    		id = Long.toString(ContentUris.parseId(idUri));
    		inDatabase = true;
    	}
	}
    
    
    private void deleteThisDeck() {
    	String selection = BaseColumns._ID + " = '" + id + "'";
		getContentResolver().delete(DeckProvider.CONTENT_URI, selection, null);
		
		selection = DECK_ID + " = '" + id + "'";
		getContentResolver().delete(CardProvider.CONTENT_URI, selection, null);
		
		setResult(RESULT_DECK_DELETED);
		finish();
	}
}
