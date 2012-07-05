package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TERM;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;

import android.content.ContentValues;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class CardEditorActivity extends SherlockActivity {
    
    EditText termEditText;
    EditText meaningEditText;
    
    Button saveButton;
    Button cancelButton;
    
    int row_id;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeditor);
        
        // Get UI elements
        termEditText = (EditText)findViewById(R.id.term);
        meaningEditText = (EditText)findViewById(R.id.meaning);
        saveButton = (Button)findViewById(R.id.saveEditButton);
        cancelButton = (Button)findViewById(R.id.cancelEditButton);
        
        row_id = getIntent().getIntExtra(BaseColumns._ID, 0);
        String term = getIntent().getStringExtra(TERM);
        String meaning = getIntent().getStringExtra(MEANING);
        
        termEditText.setText(term);
        meaningEditText.setText(meaning);
        
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
                
                String selection = BaseColumns._ID + " = '" + row_id + "'";
                
                getContentResolver().update(CardProvider.CONTENT_URI, editedValues, selection, null);
                
                setResult(RESULT_OK);
                
                finish();
            }
        });
    }
}
