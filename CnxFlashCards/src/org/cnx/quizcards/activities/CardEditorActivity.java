/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards.activities;

import static org.cnx.quizcards.Constants.DECK_ID;
import static org.cnx.quizcards.Constants.MEANING;
import static org.cnx.quizcards.Constants.TERM;

import org.cnx.quizcards.R;
import org.cnx.quizcards.database.CardProvider;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardEditorActivity extends SherlockActivity {
    
    EditText termEditText;
    EditText meaningEditText;
    MenuItem saveActionBarItem;
    
    int _id;
    String deck;
    boolean newCard = true;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_editor);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        termEditText = (EditText)findViewById(R.id.term);
        meaningEditText = (EditText)findViewById(R.id.meaning);
        
        _id = getIntent().getIntExtra(BaseColumns._ID, 0);
        String term = getIntent().getStringExtra(TERM);
        String meaning = getIntent().getStringExtra(MEANING);
        deck = getIntent().getStringExtra(DECK_ID);
        
        if(term != null) {
            termEditText.setText(term);
            meaningEditText.setText(meaning);
            newCard = false;
        }
        
        termEditText.addTextChangedListener(new TextWatcher() {
            
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
        
        meaningEditText.addTextChangedListener(new TextWatcher() {
            
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
    
    
    public void saveCard() {
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
        
        saveActionBarItem.setEnabled(false);
        
        setResult(RESULT_OK);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.card_editor_menu, menu);
        saveActionBarItem = menu.findItem(R.id.saveCardActionItem);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
            
        case R.id.deleteCardActionItem:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this card?");
            
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selection = BaseColumns._ID + " = '" + _id + "'";
                    getContentResolver().delete(CardProvider.CONTENT_URI, selection, null);
                    setResult(RESULT_OK);
                    finish();
                }
            });
            
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            break;
            
        case R.id.saveCardActionItem:
            saveCard();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
