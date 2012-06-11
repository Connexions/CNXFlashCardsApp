package uk.co.withad.flashcards;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.withad.flashcards.ParsingActivity.XMLSource;
import static uk.co.withad.flashcards.Constants.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.TextView;

public class ModuleToDatabaseParser {
	
	private XMLSource xmlsource = XMLSource.RESOURCE;
	
	private ArrayList<String> terms;
	private ArrayList<String> meanings;
	
	private SQLiteDatabase cardsdb;
	private CardDatabaseOpenHelper cards;
	
	private Context context;
	
	
	/** Constructor **/
	public ModuleToDatabaseParser(Context context) {
		this.context = context;
		
		cards = new CardDatabaseOpenHelper(context);
	}
	
	
	/** Parses definitions from a CNXML file, places into database **/
	public boolean parse(String id) {
		
		terms = new ArrayList<String>();
		meanings = new ArrayList<String>();
		
		Document doc = retrieveXML(id);
		NodeList nodes = doc.getElementsByTagName("definition");
		
		// Check that there were definitions in the file
		if(nodes == null) return false;

		extractDefinitions(nodes);
		addDefinitionsToDatabase(id);
		
		cardsdb.close();
		
		
		return true;
	}
	

	/** Add the parsed definitions to the database. **/
	private void addDefinitionsToDatabase(String id) {
		cardsdb = cards.getWritableDatabase();
		
		ContentValues values;
		
		for (int i = 0; i < terms.size(); i++){
			values = new ContentValues();
			Log.d(Constants.TAG, "Inserting " + terms.get(i) + ": " + meanings.get(i));
			values.put(DECK_ID, id);
			values.put(MEANING, meanings.get(i));
			values.put(TERM, terms.get(i));
			cardsdb.insertOrThrow(CARDS_TABLE, null, values);
		}
		
		values = new ContentValues();
		values.put(TITLE, "Test Title");
		values.put(DECK_ID, id);
		cardsdb.insertOrThrow(DECKS_TABLE, null, values);
		
		cardsdb.close();
	}


	/** Retrieve the CNXML file as a list of nodes 
	 * @param id **/
	private Document retrieveXML(String id) {
		URL url;
		URLConnection conn;
		InputStream in = null;
		
		try {
			if(xmlsource == XMLSource.DOWNLOAD) {
				Log.d(TAG, "Downloading XML");
				url = new URL("http://cnx.org/content/" + id + "/module_export?format=plain"); //m9006/2.22
				conn = url.openConnection();
				in = conn.getInputStream();
			}
			else if(xmlsource == XMLSource.RESOURCE){
				Log.d(TAG, "Loading XML from resource");
				in = context.getResources().openRawResource(R.raw.testmodule);
			}
			
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			
			try {
				docBuilder = dbf.newDocumentBuilder();
				doc = docBuilder.parse(in);
				Log.d(TAG, "Succesful parse.");
			}
			catch (ParserConfigurationException pce) {
				Log.d(TAG, "Caught parser exception.");
			} 
			catch (SAXException e) {
				Log.d(TAG, "Caught SAX exception.");
			}
			
			doc.getDocumentElement().normalize();
			
			return doc;
		}
		catch (MalformedURLException mue) {} 
		catch (IOException ioe) {}
		
		return null;
	}
	
	
	/** Extract definitions from the list of nodes **/
	private void extractDefinitions(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			
			if(n.getNodeType() == Node.ELEMENT_NODE) {				
				// Get terms and meanings
				String term = getValue("term", n);
				String meaning = getValue("meaning", n);
				
				// Remove/replace whitespace and quotation marks
				term = term.replace("\n", "");
				term = term.replace("\t", "");
				term = term.replaceAll("\\s+", " ");
				term = term.replaceAll("^\\s+","");
				
				meaning = meaning.replace("\n", "");
				meaning = meaning.replace("\t", "");
				meaning = meaning.replaceAll("^\\s+","");
				meaning = meaning.replaceAll("\\s+", " ");
				meaning = meaning.replace("\"", "");
				
				// Add them to the lists
				terms.add(term);
				meanings.add(meaning);
			}
		}
	}
	
	
	/** Get a value with a given tag from a node **/
	private String getValue(String tagname, Node n) {
		NodeList childnodes = ((Element)n).getElementsByTagName(tagname).item(0).getChildNodes();
		Node value = (Node) childnodes.item(0);
		
		return value.getNodeValue();
	}
}
