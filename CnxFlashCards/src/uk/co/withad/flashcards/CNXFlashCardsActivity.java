package uk.co.withad.flashcards;

import static uk.co.withad.flashcards.Constants.*;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;


public class CNXFlashCardsActivity extends SherlockActivity {
	
	private Button searchButton;
	private Button parseTestButton;
	private Button showCardsButton;
	private Button nextCardButton;
	private Button prevCardButton;
	
	private TextView parseResultsText;
	private TextView termText;
	private TextView meaningText;
	private TextView deckPositionText;
	
	private EditText searchInput;
	
	private ArrayList<String[]> definitions;
	private int currentCard = 0;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get UI elements
        searchButton = (Button)findViewById(R.id.searchButton);
        parseTestButton = (Button)findViewById(R.id.parseTestButton);
        showCardsButton = (Button)findViewById(R.id.showCardsButton);        
        searchInput = (EditText)findViewById(R.id.searchInput);
        parseResultsText = (TextView)findViewById(R.id.parsingResultText);
        meaningText = (TextView)findViewById(R.id.meaningText);
        termText = (TextView)findViewById(R.id.termText);
        nextCardButton = (Button)findViewById(R.id.nextCardButton);
        prevCardButton = (Button)findViewById(R.id.prevCardButton);
        deckPositionText = (TextView)findViewById(R.id.deckPositionText);
        
        nextCardButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(definitions != null && definitions.size() != 0) {
					currentCard++;
					if(currentCard >= definitions.size()) currentCard = 0;
					termText.setText(definitions.get(currentCard)[0]);
					meaningText.setText(definitions.get(currentCard)[1]);
					deckPositionText.setText(currentCard+1 + "/" + definitions.size());
				}
			}
		});
        
        
        prevCardButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(definitions != null && definitions.size() != 0) {
					currentCard--;
					if(currentCard < 0) currentCard = definitions.size()-1;
					termText.setText(definitions.get(currentCard)[0]);
					meaningText.setText(definitions.get(currentCard)[1]);
					deckPositionText.setText(currentCard+1 + "/" + definitions.size());
				}
			}
		});
        
        
        // Parses the target CNXML file (currently just the offline test file)
        parseTestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ModuleToDatabaseParser parser = new ModuleToDatabaseParser(getApplicationContext());
				boolean success = parser.parse(TEST_ID);
				if(success)
					parseResultsText.setText("Parsing succeeded, terms in database");
				else
					parseResultsText.setText("Parsing failed. No nodes.");
			}
		});
        
        
        showCardsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				definitions = loadCards(TEST_ID);
				currentCard = 0;
				termText.setText(definitions.get(currentCard)[0]);
				meaningText.setText(definitions.get(currentCard)[1]);
				deckPositionText.setText(currentCard+1 + "/" + definitions.size());
			}
		});
        
        
        // Launch search (currently disabled)
        searchButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				//resultsView.loadUrl("http://m.cnx.org/content/search?words=" + searchInput.getText().toString());
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
				
			}
		});
    }
    
    
    private ArrayList<String[]> loadCards(String id) {		
		CardDatabaseOpenHelper cards = new CardDatabaseOpenHelper(getApplicationContext());
		SQLiteDatabase cardsdb = cards.getReadableDatabase();
		
		String[] columns = {TERM, MEANING};
		String selection = DECK_ID + " = '" + id + "'";
		
		Cursor cardsCursor = cardsdb.query(CARDS_TABLE, columns, selection, null, null, null, null);
		cardsCursor.moveToFirst();
		
		
		ArrayList<String[]> definitions = new ArrayList<String[]>();
		
		if(!cardsCursor.isAfterLast()) {
			do {
				definitions.add(new String[]{cardsCursor.getString(0), cardsCursor.getString(1)});
			} while (cardsCursor.moveToNext());
		}
		
		cardsCursor.close();
		cardsdb.close();
		cards.close();
		
		return definitions;
	}
    
    
    /** Called when Activity created, loads the ActionBar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//MenuInflater inflater = getSupportMenuInflater();
    	//inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
    }
}