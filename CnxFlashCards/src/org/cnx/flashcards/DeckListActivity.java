package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class DeckListActivity extends SherlockActivity {
    
    ListView deckListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decklist);
        
        // Get UI elements
        deckListView = (ListView)findViewById(R.id.deckListView);
        
        String[] projection = {"_id", DECK_ID, TITLE };
        final Cursor titlesCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, null, null, null);
        titlesCursor.moveToFirst();
        
        int[] to = {R.id.url, R.id.title};
        String[] from = {DECK_ID, TITLE };
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.search_row, titlesCursor, from, to, CursorAdapter.NO_SELECTION);

        deckListView.setAdapter(cursorAdapter);
        
        deckListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                titlesCursor.moveToPosition(position);
                String id = titlesCursor.getString(titlesCursor.getColumnIndex(DECK_ID));
                Intent cardIntent = new Intent(getApplicationContext(),
                        ModeSelectActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivity(cardIntent);
                
            }
        });
    }
}
