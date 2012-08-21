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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardListActivity extends SherlockActivity {
	ListView cardListView;
    Cursor cardsCursor;
    SimpleCursorAdapter cursorAdapter;
    String id;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        cardListView = (ListView)findViewById(R.id.cardListView);
        
        id = getIntent().getStringExtra(DECK_ID);
        
        getCards();        
        
        cardListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                cardsCursor.moveToPosition(position);
                int row_id = cardsCursor.getInt(cardsCursor.getColumnIndex(BaseColumns._ID));
                String term = cardsCursor.getString(cardsCursor.getColumnIndex(TERM));
                String meaning = cardsCursor.getString(cardsCursor.getColumnIndex(MEANING));
                
                Intent cardEditIntent = new Intent(CardListActivity.this, CardEditorActivity.class);
                cardEditIntent.putExtra(BaseColumns._ID, row_id);
                cardEditIntent.putExtra(TERM, term);
                cardEditIntent.putExtra(MEANING, meaning);
                cardEditIntent.putExtra(DECK_ID, id);
                startActivityForResult(cardEditIntent, CARD_EDIT_REQUEST);
            }
        });
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.card_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
            
        case R.id.newCardActionItem:
            Intent newCardIntent = new Intent(CardListActivity.this, CardEditorActivity.class);
            newCardIntent.putExtra(DECK_ID, id);
            startActivityForResult(newCardIntent, CARD_EDIT_REQUEST);
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private void getCards() {
        String[] projection = {BaseColumns._ID, DECK_ID, TERM, MEANING };
        String selection = DECK_ID + " = '" + id + "'";
        String order = "LOWER(" + TERM + ")"; //SQLite normally orders any upper case before all lower case
        cardsCursor = getContentResolver().query(CardProvider.CONTENT_URI, projection, selection, null, order);
        cardsCursor.moveToFirst();
        
        int[] to = {R.id.term, R.id.meaning};
        String[] from = {TERM, MEANING };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.card_list_row, cardsCursor, from, to, CursorAdapter.NO_SELECTION);

        cardListView.setAdapter(cursorAdapter);      
    }
    
    
    @Override
    public void finish() {
    	/*ContentValues values = new ContentValues();
    	values.put(TITLE, titleEditText.getText().toString());
    	getContentResolver().update(DeckProvider.CONTENT_URI, values, DECK_ID + " = '" + id + "'", null);*/
    	super.finish();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   
        if(requestCode != CARD_EDIT_REQUEST)
            return;
        
        switch (resultCode) {
        case RESULT_OK:
            // TODO: Cursor.requery is deprecated, change.
            cardsCursor.requery();
            cursorAdapter.notifyDataSetChanged();
            break;
            
        case RESULT_CANCELED:
            break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}
