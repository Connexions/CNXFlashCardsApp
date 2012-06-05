package uk.co.withad.flashcards;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class DownloadActivity extends SherlockActivity {
	
	String TAG = "CNXFlashCards";
	
	public enum XMLSource {
		DOWNLOAD,
		RESOURCE
	};
	
	private XMLSource xmlsource = XMLSource.RESOURCE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		
		TextView defText = (TextView)findViewById(R.id.parsedXML);
		
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
				in = getResources().openRawResource(R.raw.testmodule);
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
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) n;
					
					// Get terms and meanings
					String term = getValue("term", n);
					String meaning = getValue("meaning", n);
					
					// Remove/replace whitespace and quotation marks
					term = term.replace("\n", "");
					term = term.replace("\t", "");
					term = term.replaceAll("\\s+", " ");
					meaning = meaning.replaceAll("^\\s+","");
					
					meaning = meaning.replace("\n", "");
					meaning = meaning.replace("\t", "");
					meaning = meaning.replaceAll("^\\s+","");
					meaning = meaning.replaceAll("\\s+", " ");
					meaning = meaning.replace("\"", "");
					
					// Show terms and meanings
					defText.setText(defText.getText() + term + ":\n" + meaning + "\n\n");
				}
			}
		}
		catch (MalformedURLException mue) {} 
		catch (IOException ioe) {}
	}

	private String getValue(String tagname, Node n) {
		NodeList childnodes = ((Element)n).getElementsByTagName(tagname).item(0).getChildNodes();
		Node value = (Node) childnodes.item(0);
		
		return value.getNodeValue();
	}
	
}
