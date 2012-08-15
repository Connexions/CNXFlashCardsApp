/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

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

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DeckEditorActivity extends SherlockActivity {

    String id;
    EditText titleEditText;
    EditText summaryEditText;
    EditText authorEditText;
    MenuItem saveActionBarItem;
    Button editCardsButton;
    
    boolean newDeck = false;
    boolean inDatabase = false;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_editor);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        titleEditText = (EditText)findViewById(R.id.editDeckName);
        summaryEditText = (EditText)findViewById(R.id.editDeckSummary);
        authorEditText = (EditText)findViewById(R.id.editDeckAuthors);
        editCardsButton = (Button)findViewById(R.id.editCardsButton);
        
        newDeck = getIntent().getBooleanExtra(NEW_DECK, false);
        
        if(!newDeck) {
	        String title = getIntent().getStringExtra(TITLE);
	        titleEditText.setText(title);
	        
	        String summary = getIntent().getStringExtra(ABSTRACT);
	        if(summary != null && !summary.equals("This module doesn't have an abstract."))
	        	summaryEditText.setText(summary);
	        
	        String authors = getIntent().getStringExtra(AUTHOR);
	        authorEditText.setText(authors);
	        
	        inDatabase = true;
	        id = getIntent().getStringExtra(DECK_ID);
        }
        
        editCardsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDeck();
                Intent newCardIntent = new Intent(DeckEditorActivity.this, CardListActivity.class);
                newCardIntent.putExtra(DECK_ID, id);
                startActivity(newCardIntent);
            }
        });
        
        titleEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if(saveActionBarItem != null)
                    saveActionBarItem.setEnabled(true);
            }
        });
        
        summaryEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if(saveActionBarItem != null)
                    saveActionBarItem.setEnabled(true);
            }
        });
        
        authorEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if(saveActionBarItem != null)
                    saveActionBarItem.setEnabled(true);
            }
        });
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.deck_editor_menu, menu);
        saveActionBarItem = menu.findItem(R.id.saveDeckActionItem);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
            
        case R.id.saveDeckActionItem:
            saveDeck();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }


	private void saveDeck() {
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
    	
    	saveActionBarItem.setEnabled(false);
	}
}
