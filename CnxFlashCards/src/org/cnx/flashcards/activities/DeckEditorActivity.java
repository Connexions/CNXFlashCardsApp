package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.*;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckEditorActivity extends SherlockActivity {
    
    ListView cardListView;
    Cursor cardsCursor;
    SimpleCursorAdapter cursorAdapter;
    
    static int CARD_EDIT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deckeditor);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        cardListView = (ListView)findViewById(R.id.cardListView);
        
        String id = getIntent().getStringExtra(DECK_ID);
        
        getCards(id);
        
        cardListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                cardsCursor.moveToPosition(position);
                int row_id = cardsCursor.getInt(cardsCursor.getColumnIndex(BaseColumns._ID));
                String term = cardsCursor.getString(cardsCursor.getColumnIndex(TERM));
                String meaning = cardsCursor.getString(cardsCursor.getColumnIndex(MEANING));
                
                Intent cardEditIntent = new Intent(DeckEditorActivity.this, CardEditorActivity.class);
                cardEditIntent.putExtra(BaseColumns._ID, row_id);
                cardEditIntent.putExtra(TERM, term);
                cardEditIntent.putExtra(MEANING, meaning);
                startActivityForResult(cardEditIntent, CARD_EDIT_REQUEST);
            }
        });
    }

    
    private void getCards(String id) {
        String[] projection = {BaseColumns._ID, DECK_ID, TERM, MEANING };
        String selection = DECK_ID + " = '" + id + "'";
        cardsCursor = getContentResolver().query(CardProvider.CONTENT_URI, projection, selection, null, null);
        cardsCursor.moveToFirst();
        
        int[] to = {R.id.term, R.id.meaning};
        String[] from = {TERM, MEANING };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.card_row, cardsCursor, from, to, CursorAdapter.NO_SELECTION);

        cardListView.setAdapter(cursorAdapter);      
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Got a result.");
        
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
