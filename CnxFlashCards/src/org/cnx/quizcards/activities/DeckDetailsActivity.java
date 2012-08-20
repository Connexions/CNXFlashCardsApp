/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards.activities;

import static org.cnx.quizcards.Constants.ABSTRACT;
import static org.cnx.quizcards.Constants.AUTHOR;
import static org.cnx.quizcards.Constants.DECK_ID;
import static org.cnx.quizcards.Constants.EDIT_LAUNCH;
import static org.cnx.quizcards.Constants.MODULE_ID;
import static org.cnx.quizcards.Constants.NEW_DECK;
import static org.cnx.quizcards.Constants.QUIZ_LAUNCH;
import static org.cnx.quizcards.Constants.RESULT_DECK_DELETED;
import static org.cnx.quizcards.Constants.RESULT_INVALID_DECK;
import static org.cnx.quizcards.Constants.SELF_TEST_LAUNCH;
import static org.cnx.quizcards.Constants.STUDY_LAUNCH;
import static org.cnx.quizcards.Constants.TAG;
import static org.cnx.quizcards.Constants.TITLE;

import org.cnx.quizcards.R;
import org.cnx.quizcards.database.CardProvider;
import org.cnx.quizcards.database.DeckProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DeckDetailsActivity extends SherlockActivity {

    Button quizModeButton;
    Button studyModeButton;
    Button selfTestModeButton;
    
    TextView titleText;
    TextView summaryText;
    TextView authorsText;
    
    String id;
    String title;
    String summary;
    String authors;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_details);
        
        // Allow going back with ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra(DECK_ID);
        if(id == null)
        	id = getRowIdFromModuleId(getIntent().getStringExtra(MODULE_ID));

        quizModeButton = (Button) findViewById(R.id.quizModeButton);
        studyModeButton = (Button) findViewById(R.id.studyModeButton);
        selfTestModeButton = (Button) findViewById(R.id.selfTestModeButton);
        
        titleText = (TextView)findViewById(R.id.deckNameText);
        summaryText = (TextView)findViewById(R.id.deckSummaryText);
        authorsText = (TextView)findViewById(R.id.deckAuthorsText);
        
        setDetails();

        studyModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardIntent = new Intent(getApplicationContext(),
                        StudyCardActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivity(cardIntent);
            }
        });

        selfTestModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardIntent = new Intent(getApplicationContext(),
                        SelfTestCardActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivity(cardIntent);
            }
        });

        quizModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardIntent = new Intent(getApplicationContext(),
                        QuizCardActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivityForResult(cardIntent, QUIZ_LAUNCH);
            }
        });
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.deck_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
            
        case R.id.editDeckActionItem:
            Intent editIntent = new Intent(DeckDetailsActivity.this, DeckEditorActivity.class);
            editIntent.putExtra(DECK_ID, id);
            editIntent.putExtra(TITLE, title);
            editIntent.putExtra(ABSTRACT, summary);
            editIntent.putExtra(AUTHOR, authors);
            editIntent.putExtra(NEW_DECK, false);
            startActivityForResult(editIntent, EDIT_LAUNCH);
            break;
            
        case R.id.deleteDeckActionItem:
            deleteThisDeck();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private String getRowIdFromModuleId(String moduleID) {
		String[] projection = {BaseColumns._ID};
		String selection = MODULE_ID + " = '" + moduleID + "'";
		Cursor idCursor = getContentResolver().query(DeckProvider.CONTENT_URI, projection, selection, null, null);
		idCursor.moveToFirst();
		
		String _id = idCursor.getString(idCursor.getColumnIndex(BaseColumns._ID));
		idCursor.close();
		
		return _id;
	}


	private void setDetails() {
        String[] projection = { TITLE, ABSTRACT, AUTHOR };
        String selection = BaseColumns._ID + " = '" + id + "'";
        Cursor deckInfoCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, selection, null, null);
        deckInfoCursor.moveToFirst();
        
        title = deckInfoCursor.getString(deckInfoCursor.getColumnIndex(TITLE));
        if(title == null || title.equals(""))
        	titleText.setText("This deck has no title.");
        else
        	titleText.setText(title);
        	
        
        summary = deckInfoCursor.getString(deckInfoCursor.getColumnIndex(ABSTRACT));
        if(summary == null || summary.equals(""))
        	summaryText.setText("This deck has no abstract.");
        else
        	summaryText.setText(summary);
        
        authors = deckInfoCursor.getString(deckInfoCursor.getColumnIndex(AUTHOR));
        if(authors == null || authors.equals(""))
        	authors = "No authors";
    	authorsText.setText(authors);
        
        deckInfoCursor.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_INVALID_DECK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            
            switch(requestCode) {
            case QUIZ_LAUNCH:
                builder.setMessage("This deck has too few cards. You need at least 3 for a quiz.");
                break;
                
            case SELF_TEST_LAUNCH:
                builder.setMessage("This deck doesn't have any cards!");
                break;
                
            case STUDY_LAUNCH:
                builder.setMessage("This deck doesn't have any cards!");
                break;
            }
            
            AlertDialog quizAlert = builder.create();
            quizAlert.show();
        }
        else if (requestCode == EDIT_LAUNCH) {
        	if(resultCode == RESULT_DECK_DELETED) {
				setResult(RESULT_DECK_DELETED);
				finish();
			}
        	else {
        		Log.d(TAG, "Should be updating details.");
        		setDetails();
        	}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void deleteThisDeck() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this deck?");
        
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = BaseColumns._ID + " = '" + id + "'";
                getContentResolver().delete(DeckProvider.CONTENT_URI, selection, null);
                
                selection = DECK_ID + " = '" + id + "'";
                getContentResolver().delete(CardProvider.CONTENT_URI, selection, null);
                
                setResult(RESULT_DECK_DELETED);
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
        
    }
}
