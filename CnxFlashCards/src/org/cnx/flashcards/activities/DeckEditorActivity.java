package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;
import org.cnx.flashcards.database.DeckProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckEditorActivity extends SherlockActivity {
	
    Button newCardButton;
    String id;
    EditText titleEditText;
    EditText summaryEditText;
    
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
    	getContentResolver().update(DeckProvider.CONTENT_URI, values, DECK_ID + " = '" + id + "'", null);
    	super.finish();
    }
}
