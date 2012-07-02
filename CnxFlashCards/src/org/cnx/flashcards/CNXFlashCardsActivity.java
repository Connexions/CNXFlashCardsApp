/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TEST_ID;
import static org.cnx.flashcards.Constants.TITLE;

import java.util.ArrayList;

import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class CNXFlashCardsActivity extends SherlockActivity {

    private Button searchButton;
    private Button parseTestButton;
    private Button showCardsButton;

    private EditText searchInput;

    private String id = TEST_ID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main);

        setProgressBarIndeterminateVisibility(false);

        // Hide the keyboard at launch (as EditText will be focused
        // automatically)
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get UI elements
        searchButton = (Button) findViewById(R.id.searchButton);
        parseTestButton = (Button) findViewById(R.id.parseTestButton);
        showCardsButton = (Button) findViewById(R.id.showCardsButton);
        searchInput = (EditText) findViewById(R.id.searchInput);
        
        
        searchInput.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return true;
            }
        });
        

        // Parses the target CNXML file (currently just the offline test file)
        parseTestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                id = searchInput.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

                new DownloadDeckTask().execute(id);
                Toast downloadToast = Toast.makeText(
                        CNXFlashCardsActivity.this, "Downloading module " + id,
                        Toast.LENGTH_SHORT);
                downloadToast.show();
                setProgressBarIndeterminateVisibility(true);
            }
        });

        showCardsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projection = { DECK_ID, TITLE };
                Cursor titlesCursor = getContentResolver().query(
                        DeckProvider.CONTENT_URI, projection, null, null, null);
                titlesCursor.moveToFirst();

                final ArrayList<String> idList = new ArrayList<String>();
                ArrayList<String> titlesList = new ArrayList<String>();

                if (!titlesCursor.isAfterLast()) {
                    do {
                        idList.add(new String(
                                titlesCursor.getString(titlesCursor
                                        .getColumnIndex(DECK_ID))));
                        titlesList.add(new String(titlesCursor
                                .getString(titlesCursor.getColumnIndex(TITLE))));
                    } while (titlesCursor.moveToNext());
                }

                titlesCursor.close();

                final String[] titles = titlesList
                        .toArray(new String[titlesList.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CNXFlashCardsActivity.this);
                builder.setTitle("Pick a deck");
                builder.setItems(titles, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        id = idList.get(item);
                        Intent cardIntent = new Intent(getApplicationContext(),
                                ModeSelectActivity.class);
                        cardIntent.putExtra(DECK_ID, id);
                        startActivity(cardIntent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Launch search
        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
    }
    
    
    public void search() {
        String searchTerm = searchInput.getText().toString();                
        Intent searchIntent = new Intent(CNXFlashCardsActivity.this, SearchActivity.class);
        searchIntent.putExtra(SEARCH_TERM, searchTerm);
        startActivity(searchIntent);
    }
    

    /** Called when Activity created, loads the ActionBar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    
    public class DownloadDeckTask extends AsyncTask<String, Void, ParseResult> {

        String id = "Module";

        @Override
        protected ParseResult doInBackground(String... idParam) {
            this.id = idParam[0];
            ParseResult result = new ModuleToDatabaseParser(
                    CNXFlashCardsActivity.this).parse(id);
            return result;
        }

        @Override
        protected void onPostExecute(ParseResult result) {
            super.onPostExecute(result);

            setProgressBarIndeterminateVisibility(false);

            String resultText = "";

            switch (result) {
            case SUCCESS:
                resultText = "Parsing succeeded, terms in database";
                break;

            case DUPLICATE:
                resultText = "Parsing failed. Duplicate.";
                break;

            case NO_NODES:
                resultText = "Parsing failed. No definitions in module.";
            }

            Toast resultsToast = Toast.makeText(CNXFlashCardsActivity.this,
                    resultText, Toast.LENGTH_SHORT);
            resultsToast.show();
        }
    }
}