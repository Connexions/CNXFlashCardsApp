package org.cnx.flashcards.activities;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.SEARCH_TERM;
import static org.cnx.flashcards.Constants.TAG;

import java.util.ArrayList;

import org.cnx.flashcards.ModuleToDatabaseParser;
import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;
import org.cnx.flashcards.R;
import org.cnx.flashcards.SearchResult;
import org.cnx.flashcards.SearchResultsAdapter;
import org.cnx.flashcards.SearchResultsParser;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;



public class SearchActivity extends SherlockActivity {
    
    public enum SearchDirection {
        NEXT,
        PREVIOUS
    }
    
    String searchTerm;
    ListView resultsListView;
    SearchResultsAdapter resultsAdapter;
    ArrayList<SearchResult> results; 
    SearchResultsParser resultsParser;
    
    Button nextButton;
    Button prevButton;
    Button searchButton;
    TextView pageText;
    EditText searchInput;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search);
        
        // Hide the keyboard at launch (as EditText will be focused automatically)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Get UI elements
        resultsListView = (ListView)findViewById(R.id.resultsList);
        pageText = (TextView)findViewById(R.id.pageText);
        nextButton = (Button)findViewById(R.id.nextPageButton);
        prevButton = (Button)findViewById(R.id.prevPageButton);
        searchInput = (EditText)findViewById(R.id.searchInput);
        searchButton = (Button)findViewById(R.id.searchButton);
        
        // Get a parser for the search term
        searchTerm = getIntent().getStringExtra(SEARCH_TERM);
        searchInput.setText(searchTerm);
        
        resultsParser = new SearchResultsParser(this, searchTerm);
        
        // Tie the ListView to the results
        results = new ArrayList<SearchResult>();
        resultsAdapter = new SearchResultsAdapter(this, results);
        resultsListView.setAdapter(resultsAdapter);
        
        // Get the first page of search results
        search(SearchDirection.NEXT);
        
        // When an item's clicked, try to download that module.
        resultsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long resource_id) {
                String id = ((SearchResult)resultsListView.getItemAtPosition(position)).getId();
                setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
                new DownloadDeckTask().execute(id);
            }
        });
        
        // Loads the next page when clicked
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search(SearchDirection.NEXT);
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
            }
        });
        
        // Loads the previous page when clicked
        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search(SearchDirection.PREVIOUS);
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
            }
        });
        
        // Search if the user hits enter while typing a search term
        searchInput.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchTerm = searchInput.getText().toString();
                resultsParser = new SearchResultsParser(SearchActivity.this, searchTerm);
                search(SearchDirection.NEXT);
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
                pageText.setText("");
                return true;
            }
        });
        
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTerm = searchInput.getText().toString();
                resultsParser = new SearchResultsParser(SearchActivity.this, searchTerm);
                search(SearchDirection.NEXT);
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
                pageText.setText("");                
            }
        });
    }
    
    
    private void search(SearchDirection direction) {
        results.clear();
        resultsAdapter.notifyDataSetChanged();
        
        new SearchResultsTask().execute(direction);
        
        Toast searchingToast = Toast.makeText(
                SearchActivity.this, "Searching for '" + searchTerm + "'...",
                Toast.LENGTH_SHORT);
        searchingToast.show();
        
        setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
    }

    
    private class SearchResultsTask extends AsyncTask<SearchDirection, Void, ArrayList<SearchResult>> {

        @Override
        protected ArrayList<SearchResult> doInBackground(SearchDirection... direction) {
            ArrayList<SearchResult> resultList;
            
            if(direction[0] == SearchDirection.NEXT)
                resultList = resultsParser.getNextPage();
            else
                resultList = resultsParser.getPrevPage();
            
            return resultList;
        }
        
        @Override
        protected void onPostExecute(ArrayList<SearchResult> resultList) {
            super.onPostExecute(resultList);

            setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
            
            //TODO: Handle a null result better (repeat search?)
            if(resultList != null && resultList.size()!=0) {
                
                if(resultList.size() > resultsParser.resultsPerPage) {
                    nextButton.setEnabled(true);
                    resultList.remove(resultList.size()-1);
                }
                
                results.addAll(resultList);
                resultsAdapter.notifyDataSetChanged();
    
                Toast resultsToast = Toast.makeText(SearchActivity.this,
                        "Successfully downloaded search results.", Toast.LENGTH_SHORT);
                resultsToast.show();
                
                pageText.setText(Integer.toString(resultsParser.currentPage+1));
                if(resultsParser.currentPage == 0)
                    prevButton.setEnabled(false);
                else
                    prevButton.setEnabled(true);
            }
            else if(resultList == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("Unable to search");
                builder.setMessage("Couldn't reach Connexions to search.");
                builder.create().show();
            }
            else if(resultList.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("No results");
                builder.setMessage("No results were found for the search term \"" + searchTerm + "\".");
                builder.create().show();
            }
        }
    }


    public class DownloadDeckTask extends AsyncTask<String, Void, ParseResult> {

        String id;

        @Override
        protected ParseResult doInBackground(String... idParam) {
            this.id = idParam[0];
            ParseResult result = new ModuleToDatabaseParser(SearchActivity.this).parse(id);
            return result;
        }

        @Override
        protected void onPostExecute(ParseResult result) {
            super.onPostExecute(result);

            setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);

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
                    resultText, Toast.LENGTH_SHORT);
            resultsToast.show();
            
            if(launch) {
                Intent cardIntent = new Intent(getApplicationContext(), DownloadedDeckInfoActivity.class);
                cardIntent.putExtra(MODULE_ID, id);
                startActivity(cardIntent);
            }
        }
    }
}
