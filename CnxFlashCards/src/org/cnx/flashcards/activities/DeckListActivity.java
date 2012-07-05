package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.TITLE;

import org.cnx.flashcards.R;
import org.cnx.flashcards.database.DeckProvider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckListActivity extends SherlockActivity {
    
    ListView deckListView;
    Cursor titlesCursor;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decklist);
        
        // Get UI elements
        deckListView = (ListView)findViewById(R.id.deckListView);
        
        // Retrieve decks from the database, show in list
        getDecks();
        
        deckListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                // Get the id of the selected deck
                titlesCursor.moveToPosition(position);
                String id = titlesCursor.getString(titlesCursor.getColumnIndex(DECK_ID));
                
                // Launch the deck
                Intent cardIntent = new Intent(getApplicationContext(), DownloadedDeckInfoActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivity(cardIntent);
            }
        });
    }

    
    /**Extracts decks from the database, shows them in the ListView**/
    private void getDecks() {
        String[] projection = {BaseColumns._ID, DECK_ID, TITLE };
        titlesCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, null, null, null);
        titlesCursor.moveToFirst();
        
        int[] to = {R.id.url, R.id.title};
        String[] from = {DECK_ID, TITLE };
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.search_row, titlesCursor, from, to, CursorAdapter.NO_SELECTION);

        deckListView.setAdapter(cursorAdapter);
    }
}