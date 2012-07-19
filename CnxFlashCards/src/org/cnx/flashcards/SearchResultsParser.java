package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.TAG;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cnx.flashcards.ModuleToDatabaseParser.ParseResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class SearchResultsParser {
    
    Context context;
    public int currentPage;
    String searchTerm;
    public int resultsPerPage = 20;
    

    public SearchResultsParser(Context context, String searchTerm) {
        this.context = context;
        this.searchTerm = searchTerm;
        currentPage = -1;
    }
    
    
    public ArrayList<SearchResult> getNextPage() {
        currentPage++;
        return parse(searchTerm);
    }
    
    
    public ArrayList<SearchResult> getPrevPage() {
        currentPage--;
        return parse(searchTerm);
    }
    

    public ArrayList<SearchResult> parse(String searchTerm) {

        Document doc = retrieveXML(searchTerm);
        
        /* Check that a valid document was returned
         * TODO: Better error handling here.
         */
        if (doc == null)
            return null;


        NodeList metadataNodes = doc.getElementsByTagName("oai_dc:dc");
        NodeList headerNodes = doc.getElementsByTagName("header");
        
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
        
        for (int i=0; i < metadataNodes.getLength(); i++) {
            
            String title = getValue("dc:title", metadataNodes.item(i));
            String authors = getValue("dc:creator", metadataNodes.item(i));            
            String url = getValue("dc:identifier", metadataNodes.item(i));
            String id = getValue("identifier", headerNodes.item(i));
            id = id.replace("oai:cnx.org:", "");
            
            searchResults.add(new SearchResult(title, authors, url, id));
            
            results.add(id);
        }

        return searchResults;
    }
    
    
    private Document retrieveXML(String searchTerm) {
        URL url;
        URLConnection conn;
        InputStream in = null;

        try {
            searchTerm = searchTerm.replace(" ", "%20");
            
            int start = resultsPerPage * currentPage;
            
            url = new URL("http://cnx.org/content/OAI?verb=SearchRecords&metadataPrefix=oai_dc&query:list=" + 
                            searchTerm + 
                            "&b_start:int=" + start + 
                            "&b_size=" + (resultsPerPage+1));
            conn = url.openConnection();
            in = conn.getInputStream();

            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            try {
                docBuilder = dbf.newDocumentBuilder();
                doc = docBuilder.parse(in);
                Log.d(TAG, "Succesful parse.");
            } catch (ParserConfigurationException pce) {
                Log.d(TAG, "Caught parser exception.");
            } catch (SAXException e) {
                Log.d(TAG, "Caught SAX exception.");
            }

            return doc;
        } catch (MalformedURLException mue) {
        } catch (IOException ioe) {
            Log.d(TAG, "IOException");
        }

        return null;
    }
    
    
    /** Get a value with a given tag from a node **/
    private String getValue(String tagname, Node n) {
        // TODO: This needs better error handling around all of it.
        NodeList childnodes = ((Element) n).getElementsByTagName(tagname)
                .item(0).getChildNodes();
        Node value = (Node) childnodes.item(0);
        
        if(value == null) return null;
        else return value.getNodeValue();
    }
}
