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
	
	private TextView defText;
	
	private ArrayList<String> terms;
	private ArrayList<String> meanings;
	
	private SQLiteDatabase cardsdb;
	private CardDatabaseOpenHelper cards;
	
	private Context context;
	
	public ModuleToDatabaseParser(Context context) {
		this.context = context;
	}
	
	public void parse() {
		
		terms = new ArrayList<String>();
		meanings = new ArrayList<String>();
		
		NodeList nodes = getDefinitionsFromXML();
		
		cards = new CardDatabaseOpenHelper(context);
		cardsdb = cards.getWritableDatabase();
		
		if(nodes != null) {
			displayDefinitions(nodes);
			addDefinitionsToDatabase();
		}
		
		cardsdb.close();
		cardsdb = cards.getReadableDatabase();
		
		cardsdb.execSQL("CREATE TABLE IF NOT EXISTS " + "cards3" + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + "term" + " STRING, " 
				+ "meaning" + " TEXT NOT NULL);");
		
		ArrayList<String> tableNames = new ArrayList<String>();
		Cursor cursor = cardsdb.rawQuery("SELECT name " + 
										 "FROM sqlite_master " +
										 "WHERE type='table' AND NOT(name = 'android_metadata') AND NOT(name = 'sqlite_sequence') " + 
										 "ORDER BY name"
										 , null);
		
		cursor.moveToFirst();
		if(!cursor.isAfterLast()) {
			do {
				tableNames.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		cardsdb.close();
		
		/*for (String name : tableNames) {
			defText.setText(name + "\n" + defText.getText());
		}*/
	}
	

	private void addDefinitionsToDatabase() {
		Log.d(Constants.TAG, "Adding definitions to database...");
		
		ContentValues values;
		
		for (int i = 0; i < terms.size(); i++){
			values = new ContentValues();
			Log.d(Constants.TAG, "Inserting " + terms.get(i) + ": " + meanings.get(i));
			values.put("meaning", meanings.get(i));
			values.put("term", terms.get(i));
			cardsdb.insertOrThrow("cards", null, values);
		}
	}


	private NodeList getDefinitionsFromXML() {
		URL url;
		URLConnection conn;
		InputStream in = null;
		
		try {
			if(xmlsource == XMLSource.DOWNLOAD) {
				Log.d(TAG, "Downloading XML");
				url = new URL("http://cnx.org/content/m9006/2.22/module_export?format=plain");
				conn = url.openConnection();
				in = conn.getInputStream();
			}
			else if(xmlsource == XMLSource.RESOURCE){
				Log.d(TAG, "Loading XML from resource");
				in = context.getResources().openRawResource(R.raw.testmodule);
			}
			
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
				Log.d(TAG, "Succesful parse.");
			}
			catch (ParserConfigurationException pce) {
				Log.d(TAG, "Caught parser exception.");
			} 
			catch (SAXException e) {
				Log.d(TAG, "Caught SAX exception.");
			}
			
			doc.getDocumentElement().normalize();
			
			NodeList nodes = doc.getElementsByTagName("definition");
			
			return nodes;
		}
		catch (MalformedURLException mue) {} 
		catch (IOException ioe) {}
		
		return null;
	}
	
	
	private void displayDefinitions(NodeList nodes) {
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
				
				// Show terms and meanings
				//defText.setText(defText.getText() + term + ":\n" + meaning + "\n\n");
				
				// Add them to the lists
				terms.add(term);
				meanings.add(meaning);
			}
		}
	}
	
	
	private String getValue(String tagname, Node n) {
		NodeList childnodes = ((Element)n).getElementsByTagName(tagname).item(0).getChildNodes();
		Node value = (Node) childnodes.item(0);
		
		return value.getNodeValue();
	}
}
