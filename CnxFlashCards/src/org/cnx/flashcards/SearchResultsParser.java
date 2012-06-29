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
    
    
    public SearchResultsParser(Context context) {
        this.context = context;
    }
    

    public ArrayList<String> parse(String searchTerm) {

        Document doc = retrieveXML(searchTerm);

        /* Check that a valid document was returned
         * TODO: Better error handling here.
         */
        if (doc == null)
            return null;

        Element root = doc.getDocumentElement();

        NodeList metadataNodes = doc.getElementsByTagName("oai_dc:dc");
        
        ArrayList<String> results = new ArrayList<String>();
        
        for (int i=0; i < metadataNodes.getLength(); i++) {
            String title = getValue("dc:title", metadataNodes.item(i));
            String creator = getValue("dc:creator", metadataNodes.item(i));            
            String identifier = getValue("dc:identifier", metadataNodes.item(i));
            results.add(title);
        }

        return results;
    }
    
    
    private Document retrieveXML(String searchTerm) {
        URL url;
        URLConnection conn;
        InputStream in = null;

        try {
            searchTerm = searchTerm.replace(" ", "%20");
            
            url = new URL("http://cnx.org/content/OAI?verb=SearchRecords&metadataPrefix=oai_dc&query:list=" + 
                            searchTerm + 
                            "&b_start:int=0&b_size=20");
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
