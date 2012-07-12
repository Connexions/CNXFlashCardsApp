package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.QUIZ_LAUNCH;
import static org.cnx.flashcards.Constants.RESULT_INVALID_DECK;
import static org.cnx.flashcards.Constants.SELF_TEST_LAUNCH;
import static org.cnx.flashcards.Constants.STUDY_LAUNCH;
import static org.cnx.flashcards.Constants.TITLE;

import org.cnx.flashcards.R;
import org.cnx.flashcards.R.id;
import org.cnx.flashcards.R.layout;
import org.cnx.flashcards.database.CardProvider;
import org.cnx.flashcards.database.DeckProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class DownloadedDeckInfoActivity extends SherlockActivity {

    Button quizModeButton;
    Button studyModeButton;
    Button selfTestModeButton;
    Button editButton;
    
    TextView titleText;
    TextView summaryText;
    TextView authorsText;
    TextView noOfCardsText;
    
    String id;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadeddeck);

        id = getIntent().getStringExtra(DECK_ID);

        quizModeButton = (Button) findViewById(R.id.quizModeButton);
        studyModeButton = (Button) findViewById(R.id.studyModeButton);
        selfTestModeButton = (Button) findViewById(R.id.selfTestModeButton);
        editButton = (Button)findViewById(R.id.editButton);
        
        titleText = (TextView)findViewById(R.id.deckNameText);
        summaryText = (TextView)findViewById(R.id.deckSummaryText);
        noOfCardsText = (TextView)findViewById(R.id.numberOfCardsText);
        
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
        
        editButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(DownloadedDeckInfoActivity.this, DeckEditorActivity.class);
                editIntent.putExtra(DECK_ID, id);
                startActivityForResult(editIntent, EDIT_LAUNCH);
            }
        });
    }

    
    private void setDetails() {
        String[] projection = { TITLE, ABSTRACT };
        String selection = DECK_ID + " = '" + id + "'";
        Cursor deckInfoCursor = getContentResolver().query(
                DeckProvider.CONTENT_URI, projection, selection, null, null);
        deckInfoCursor.moveToFirst();
        
        String title = deckInfoCursor.getString(deckInfoCursor.getColumnIndex(TITLE));
        titleText.setText("Title: " + title);
        
        String summary = deckInfoCursor.getString(deckInfoCursor.getColumnIndex(ABSTRACT));
        summaryText.setText("Abstract: " + summary);
        
        projection = new String[]{TERM};
        Cursor cardCountCursor = getContentResolver().query(CardProvider.CONTENT_URI, projection, selection, null, null);
        noOfCardsText.setText("No. of cards: " + cardCountCursor.getCount());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Only dealing with invalid decks right now
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
            setDetails();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
