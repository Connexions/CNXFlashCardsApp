package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.*;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class CardEditorActivity extends SherlockActivity {
    
    EditText termEditText;
    EditText meaningEditText;
    
    Button saveButton;
    Button cancelButton;
    Button deleteButton;
    
    int _id;
    String deck;
    boolean newCard = true;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeditor);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        termEditText = (EditText)findViewById(R.id.term);
        meaningEditText = (EditText)findViewById(R.id.meaning);
        saveButton = (Button)findViewById(R.id.saveEditButton);
        cancelButton = (Button)findViewById(R.id.cancelEditButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        
        _id = getIntent().getIntExtra(BaseColumns._ID, 0);
        String term = getIntent().getStringExtra(TERM);
        String meaning = getIntent().getStringExtra(MEANING);
        deck = getIntent().getStringExtra(DECK_ID);
        
        if(term != null) {
            termEditText.setText(term);
            meaningEditText.setText(meaning);
            newCard = false;
        }
        
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        saveButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ContentValues editedValues = new ContentValues();
                
                editedValues.put(TERM, termEditText.getText().toString());
                editedValues.put(MEANING, meaningEditText.getText().toString());
                
                if(!newCard) {
                    String selection = BaseColumns._ID + " = '" + _id + "'";                    
                    getContentResolver().update(CardProvider.CONTENT_URI, editedValues, selection, null);
                }
                else {
                    editedValues.put(DECK_ID, deck);
                    getContentResolver().insert(CardProvider.CONTENT_URI, editedValues);
                }
                
                setResult(RESULT_OK);
                
                finish();
            }
        });
        
        deleteButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String selection = BaseColumns._ID + " = '" + _id + "'";
                getContentResolver().delete(CardProvider.CONTENT_URI, selection, null);
                
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
