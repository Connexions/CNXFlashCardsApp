package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.CardProvider;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckEditorActivity extends SherlockActivity {
    
    ListView cardListView;
    Cursor cardsCursor;

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
    }

    
    private void getCards(String id) {
        String[] projection = {"_id", DECK_ID, TERM, MEANING };
        String selection = DECK_ID + " = '" + id + "'";
        cardsCursor = getContentResolver().query(
                CardProvider.CONTENT_URI, projection, selection, null, null);
        cardsCursor.moveToFirst();
        
        int[] to = {R.id.term, R.id.meaning};
        String[] from = {TERM, MEANING };
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.card_row, cardsCursor, from, to, CursorAdapter.NO_SELECTION);

        cardListView.setAdapter(cursorAdapter);      
        
        Log.d(TAG, cardsCursor.getCount() + " results.");
    }
    
}
