package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.DECK_ID;
import static org.cnx.flashcards.Constants.SEARCH_TERM;
import static org.cnx.flashcards.Constants.TAG;

import org.cnx.flashcards.CNXFlashCardsActivity.DownloadDeckTask;
import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class SearchActivity extends SherlockActivity {
    
    String searchTerm;
    ListView resultsListView;
    SearchResultsAdapter resultsAdapter;
    ArrayList<SearchResult> results; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search);
        
        
        resultsListView = (ListView)findViewById(R.id.resultsList);
        results = new ArrayList<SearchResult>();
        resultsAdapter = new SearchResultsAdapter(this, results);
        
        resultsListView.setAdapter(resultsAdapter);

        searchTerm = getIntent().getStringExtra(SEARCH_TERM);
        
        Toast searchingToast = Toast.makeText(
                SearchActivity.this, "Searching for '" + searchTerm + "'...",
                Toast.LENGTH_SHORT);
        searchingToast.show();
        setProgressBarIndeterminateVisibility(true);
        
        new SearchResultsTask().execute(searchTerm);
        
        
        resultsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                String id = ((SearchResult)resultsListView.getItemAtPosition(position)).getId();
                setProgressBarIndeterminate(true);
                new DownloadDeckTask().execute(id);
            }
        });
    }

    
    private class SearchResultsTask extends AsyncTask<String, Void, ArrayList<SearchResult>> {
        
        String searchTerm;

        @Override
        protected ArrayList<SearchResult> doInBackground(String... params) {
            this.searchTerm = params[0];
            ArrayList<SearchResult> resultList = new SearchResultsParser(SearchActivity.this).parse(searchTerm);
            return resultList;
        }
        
        @Override
        protected void onPostExecute(ArrayList<SearchResult> result) {
            super.onPostExecute(result);

            setProgressBarIndeterminateVisibility(false);
            
            results.addAll(result);
            resultsAdapter.notifyDataSetChanged();

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


    public class DownloadDeckTask extends AsyncTask<String, Void, ParseResult> {

        String id = "Module";

        @Override
        protected ParseResult doInBackground(String... idParam) {
            this.id = idParam[0];
            ParseResult result = new ModuleToDatabaseParser(
                    SearchActivity.this).parse(id);
            return result;
        }

        @Override
        protected void onPostExecute(ParseResult result) {
            super.onPostExecute(result);

            setProgressBarIndeterminateVisibility(false);

            String resultText = "";
            boolean launch = false;

            switch (result) {
            case SUCCESS:
                resultText = "Parsing succeeded, terms in database";
                Log.d(TAG, "SUCCESS");
                launch = true;
                break;

            case DUPLICATE:
                resultText = "You have already downloaded that module. Loading it now.";
                Log.d(TAG, "DUPLICATE");
                launch = true;
                break;

            case NO_NODES:
                resultText = "That module has no definitions.";
                Log.d(TAG, "NO_NODES");
                break;
                
            case NO_XML:
                resultText = "Could not download valid XML.";
                Log.d(TAG, "NO_XML");
                break;
            }

            Toast resultsToast = Toast.makeText(SearchActivity.this,
                    resultText, Toast.LENGTH_LONG);
            resultsToast.show();
            
            if(launch) {
                Intent cardIntent = new Intent(getApplicationContext(),
                        ModeSelectActivity.class);
                cardIntent.putExtra(DECK_ID, id);
                startActivity(cardIntent);
            }
        }
    }
}
