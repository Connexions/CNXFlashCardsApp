/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.TITLE;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

public class ModuleToDatabaseParser {

    private ArrayList<String> terms;
    private ArrayList<String> meanings;
    private String title;
    private String authors;
    private String summary;

    private Context context;

    public static enum ParseResult {
        SUCCESS, NO_NODES, DUPLICATE
    }

    /** Constructor **/
    public ModuleToDatabaseParser(Context context) {
        this.context = context;
    }

    
    /** Parses definitions from a CNXML file, places into database **/
    public ParseResult parse(String id) {

        terms = new ArrayList<String>();
        meanings = new ArrayList<String>();

        Document doc = retrieveXML(id);

        /* Check that a valid document was returned
         * TODO: Better error handling here.
         */
        if (doc == null)
            return ParseResult.NO_NODES;

        Element root = doc.getDocumentElement();
        title = getValue("title", root);
        
        NodeList metadataNodes = doc.getElementsByTagName("metadata");
        summary = getValue("md:abstract", metadataNodes.item(0));
        if(summary == null) summary = "This module doesn't have an abstract.";

        NodeList definitionNodes = doc.getElementsByTagName("definition");
        extractDefinitions(definitionNodes);
        Uri deckUri = addDefinitionsToDatabase(id);

        if (deckUri == null)
            return ParseResult.DUPLICATE;

        return ParseResult.SUCCESS;
    }

    
    /** Add the parsed definitions to the database. **/
    private Uri addDefinitionsToDatabase(String id) {
        ContentValues values;

        // Insert deck first to check for duplicates
        values = new ContentValues();
        values.put(DECK_ID, id);
        values.put(TITLE, title);
        values.put(ABSTRACT, summary);
        Uri deckUri = context.getContentResolver().insert(
                DeckProvider.CONTENT_URI, values);

        if (deckUri == null)
            return null;

        for (int i = 0; i < terms.size(); i++) {
            values = new ContentValues();
            values.put(DECK_ID, id);
            values.put(MEANING, meanings.get(i));
            values.put(TERM, terms.get(i));
            context.getContentResolver().insert(CardProvider.CONTENT_URI,
                    values);
        }

        return deckUri;
    }

    
    /**
     * Retrieve the CNXML file as a list of nodes
     * 
     * @param id
     **/
    private Document retrieveXML(String id) {
        URL url;
        URLConnection conn;
        InputStream in = null;

        String teststring = (String) id.subSequence(0, 4);

        try {
            if (teststring.equals("test")) {
                Log.d(TAG, "Loading XML from resource");
                in = context.getResources().openRawResource(R.raw.testmodule);
            } else {
                Log.d(TAG, "Downloading XML");
                url = new URL("http://cnx.org/content/" + id
                        + "/latest/module_export?format=plain"); // m9006/2.22
                conn = url.openConnection();
                in = conn.getInputStream();
            }

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

    
    /**
     * Extract definitions from the list of nodes, puts them in ArrayLists Might
     * make this go straight from xml to database, depends on final structure.
     **/
    private void extractDefinitions(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                // Get terms and meanings
                String term = getValue("term", n);
                String meaning = getValue("meaning", n);

                // Remove/replace whitespace and quotation marks
                term = term.replace("\n", "");
                term = term.replace("\t", "");
                term = term.replaceAll("\\s+", " ");
                term = term.replaceAll("^\\s+", "");

                meaning = meaning.replace("\n", "");
                meaning = meaning.replace("\t", "");
                meaning = meaning.replaceAll("^\\s+", "");
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
        // TODO: This needs better error handling around all of it.
        NodeList childnodes = ((Element) n).getElementsByTagName(tagname)
                .item(0).getChildNodes();
        Node value = (Node) childnodes.item(0);
        
        if(value == null) return null;
        else return value.getNodeValue();
    }
}
