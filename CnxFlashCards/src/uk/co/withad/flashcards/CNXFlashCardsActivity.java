package uk.co.withad.flashcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class CNXFlashCardsActivity extends SherlockActivity {
	
	private Button searchButton;
	private Button parseTestButton;
	private WebView resultsView;
	private EditText searchInput;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        searchButton = (Button)findViewById(R.id.searchButton);
        parseTestButton = (Button)findViewById(R.id.parseTestButton);
        resultsView = (WebView)findViewById(R.id.resultsView);
        searchInput = (EditText)findViewById(R.id.searchInput);
        
        searchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				resultsView.loadUrl("http://m.cnx.org/content/search?words=" + searchInput.getText().toString());
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
				
			}
		});
        
        
        parseTestButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//startActivity(new Intent(getApplicationContext(), ParsingActivity.class));
				
				ModuleToDatabaseParser parser = new ModuleToDatabaseParser();
				parser.parse(getApplicationContext());
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);

    	
		return super.onCreateOptionsMenu(menu);
    }
}