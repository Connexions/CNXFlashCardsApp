/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TEST_ID;
import static org.cnx.flashcards.Constants.TITLE;

import java.util.ArrayList;

import org.cnx.flashcards.ModuleToDatabaseParser;
import org.cnx.flashcards.R;
import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;
import org.cnx.flashcards.R.id;
import org.cnx.flashcards.R.layout;
import org.cnx.flashcards.R.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
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

public class MainActivity extends SherlockActivity {

    private Button searchButton;
    private Button showCardsButton;
    private Button viewHelpButton;

    private EditText searchInput;

    private String id = TEST_ID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        searchButton = (Button) findViewById(R.id.searchButton);
        showCardsButton = (Button) findViewById(R.id.showCardsButton);
        searchInput = (EditText) findViewById(R.id.searchInput);
        viewHelpButton = (Button)findViewById(R.id.viewHelpButton);
        
        // Show the user's existing decks
        showCardsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deckListIntent = new Intent(MainActivity.this, DeckListActivity.class);
                startActivity(deckListIntent);
            }
        });

        // Search when the user hits the search button
        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
        
        // Search if the user hits enter while typing a search term
        searchInput.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return true;
            }
        });
        
        // Launch the help screen
        viewHelpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpIntent);
            }
        });
    }
    
    
    /** Launch a search for the term in the search box **/
    public void search() {
     // Check the internet connection
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You must be connected to the internet to search Connexions.");
            builder.setTitle("Unable to search");
            builder.create().show();
        }
        else {
            String searchTerm = searchInput.getText().toString();                
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            searchIntent.putExtra(SEARCH_TERM, searchTerm);
            startActivity(searchIntent);
        }
    }
    

    /** Called when Activity created, loads the ActionBar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}