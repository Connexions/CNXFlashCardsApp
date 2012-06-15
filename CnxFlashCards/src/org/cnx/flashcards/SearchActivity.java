package org.cnx.flashcards;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;

public class SearchActivity extends SherlockActivity {
	
	WebView searchResultsView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		String searchTerm = getIntent().getStringExtra("SEARCH_TERM");
		
		searchResultsView = (WebView)findViewById(R.id.searchResultsView);
		searchResultsView.loadUrl("http://m.cnx.org/content/search?words=" + searchTerm);
	}

}
