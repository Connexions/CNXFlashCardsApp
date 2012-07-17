package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.ABSTRACT;
import static org.cnx.flashcards.Constants.AUTHOR;
import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.TITLE;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.DeckProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter.AuthorityEntry;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckEditorActivity extends SherlockActivity {
	
    Button newCardButton;
    Button saveButton;
    Button cancelButton;
    String id;
    EditText titleEditText;
    EditText summaryEditText;
    EditText authorEditText;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deckeditor);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        newCardButton = (Button)findViewById(R.id.editCardsButton);
        titleEditText = (EditText)findViewById(R.id.editDeckName);
        summaryEditText = (EditText)findViewById(R.id.editDeckSummary);
        authorEditText = (EditText)findViewById(R.id.editDeckAuthors);
        
        id = getIntent().getStringExtra(DECK_ID);
        
        String title = getIntent().getStringExtra(TITLE);
        titleEditText.setText(title);
        
        String summary = getIntent().getStringExtra(ABSTRACT);
        if(!summary.equals("This module doesn't have an abstract."))
        	summaryEditText.setText(summary);
        
        
        newCardButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent newCardIntent = new Intent(DeckEditorActivity.this, CardListActivity.class);
                newCardIntent.putExtra(DECK_ID, id);
                startActivity(newCardIntent);
            }
        });
    }
    
    
    @Override
    public void finish() {
    	ContentValues values = new ContentValues();
    	values.put(TITLE, titleEditText.getText().toString());
    	values.put(ABSTRACT, summaryEditText.getText().toString());
    	values.put(AUTHOR, authorEditText.getText().toString());
    	getContentResolver().update(DeckProvider.CONTENT_URI, values, DECK_ID + " = '" + id + "'", null);
    	super.finish();
    }
}
