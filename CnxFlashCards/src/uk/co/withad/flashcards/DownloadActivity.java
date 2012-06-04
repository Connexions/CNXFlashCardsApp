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
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DownloadActivity extends Activity {
	
	String TAG = "CNXFlashCards";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		
		TextView defText = (TextView)findViewById(R.id.parsedXML);
		
		URL url;
		URLConnection conn;
		try {
			url = new URL("http://cnx.org/content/m9006/2.22/module_export?format=plain");
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			
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
			defText.setText(doc.getDocumentElement().getNodeName());
			
/*			conn = url.openConnection();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			
			boolean waitingForTerm = false;
			
			while ((str = in.readLine()) != null) {
				Log.d(TAG, str);
				if(str.contains("<definition")) {
					waitingForTerm = true;
				}
				if(waitingForTerm && str.contains("<Term"))
			}*/
			
		}
		catch (MalformedURLException mue) {} 
		catch (IOException ioe) {}
		
		//webview.loadUrl("http://google.co.uk");
	}
	
}
