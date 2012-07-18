package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.MODULE_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.TITLE;

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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;

public class CardListActivity extends SherlockActivity {
	ListView cardListView;
    Cursor cardsCursor;
    SimpleCursorAdapter cursorAdapter;
    Button newCardButton;
    String id;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardlisteditor);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        cardListView = (ListView)findViewById(R.id.cardListView);
        newCardButton = (Button)findViewById(R.id.newCardButton);
        
        id = getIntent().getStringExtra(MODULE_ID);
        
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
        
        newCardButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent newCardIntent = new Intent(CardListActivity.this, CardEditorActivity.class);
                newCardIntent.putExtra(DECK_ID, id);
                startActivityForResult(newCardIntent, CARD_EDIT_REQUEST);
            }
        });
    }

    
    private void getCards() {
        String[] projection = {BaseColumns._ID, DECK_ID, TERM, MEANING };
        String selection = DECK_ID + " = '" + id + "'";
        cardsCursor = getContentResolver().query(CardProvider.CONTENT_URI, projection, selection, null, TERM);
        cardsCursor.moveToFirst();
        
        int[] to = {R.id.term, R.id.meaning};
        String[] from = {TERM, MEANING };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.card_row, cardsCursor, from, to, CursorAdapter.NO_SELECTION);

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
    	Log.d(TAG, "Got a result - " + resultCode + " " + requestCode);
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
