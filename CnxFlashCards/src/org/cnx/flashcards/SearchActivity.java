package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.SEARCH_TERM;
import static org.cnx.flashcards.Constants.TAG;

import java.util.ArrayList;

import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class SearchActivity extends SherlockActivity {
    
    String searchTerm;
    ListView resultsList;
    ArrayAdapter<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        resultsList = (ListView)findViewById(R.id.resultsList);
        results = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        resultsList.setAdapter(results);

        searchTerm = getIntent().getStringExtra(SEARCH_TERM);
        
        Toast searchingToast = Toast.makeText(
                SearchActivity.this, "Searching for '" + searchTerm + "'...",
                Toast.LENGTH_SHORT);
        searchingToast.show();
        setProgressBarIndeterminateVisibility(true);
        
        new SearchResultsTask().execute(searchTerm);
    }

    
private class SearchResultsTask extends AsyncTask<String, Void, ArrayList<String>> {
        
        String searchTerm;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            this.searchTerm = params[0];
            Log.d(TAG, "Searching for '" + searchTerm + "'...");
            ArrayList<String> resultList = new SearchResultsParser(SearchActivity.this).parse(searchTerm);
            return resultList;
        }
        
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            setProgressBarIndeterminateVisibility(false);
            
            results.addAll(result);

            String resultText = "";
            
            if(result == null)
                resultText = "Couldn't download.";
            else
                resultText = "Successfully downloaded search results.";

            Toast resultsToast = Toast.makeText(SearchActivity.this,
                    resultText, Toast.LENGTH_LONG);
            resultsToast.show();
        }
    }
}
