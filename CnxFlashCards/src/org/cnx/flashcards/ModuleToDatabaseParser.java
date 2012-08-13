/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.*;
import static org.cnx.flashcards.Constants.MODULE_ID;
import static org.cnx.flashcards.Constants.MEANING;
import static org.cnx.flashcards.Constants.TAG;
import static org.cnx.flashcards.Constants.TERM;
import static org.cnx.flashcards.Constants.TITLE;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cnx.flashcards.database.CardProvider;
import org.cnx.flashcards.database.DeckProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ModuleToDatabaseParser {

    private ArrayList<String> terms;
    private ArrayList<String> meanings;
    private String title;
    private ArrayList<String> authorsList;
    private String authors;
    private String summary;

    private Context context;
    String id;
    
    private Document moduleDoc;
    private Document metadataDoc;
    

    public static enum ParseResult {
        SUCCESS, NO_NODES, DUPLICATE, NO_XML
    }

    
    /** Constructor **/
    public ModuleToDatabaseParser(Context context, String id) {
        this.context = context;
        this.id = id;
        
        terms = new ArrayList<String>();
        meanings = new ArrayList<String>();
    }

    
    /** Parses definitions from a CNXML file, places into database **/
    public ParseResult parse() {
        

        /* Check that a valid document was returned
         * TODO: Better error handling here.
         */
        if (moduleDoc == null || metadataDoc == null)
            return ParseResult.NO_XML;
        
        // Get module metadata
        Element metaRoot = metadataDoc.getDocumentElement();
        title = getValue("md:title", metaRoot);
        summary = getValue("md:abstract", metaRoot);
        
        // Summaries are often bullet pointed <list>s which need to be on separate lines
        if(summary == null) {
            Node summaryNode = metadataDoc.getElementsByTagName("md:abstract").item(0);
            Node summaryList = summaryNode.getFirstChild();
            if(summaryList != null) {
                summary = "";
                NodeList summaryItems = summaryList.getChildNodes();
                for (int i = 0; i < summaryItems.getLength(); i++) {
                    Node item = summaryItems.item(i);
                    if(item.getNodeName().equals("item")) {
                        summary += item.getTextContent() + "\n";
                    }
                }
            }
        }
        
        // Get the userids of the authors
        NodeList roles = metadataDoc.getElementsByTagName("md:role");
        ArrayList<String> authorIdList = new ArrayList<String>();
        for(int i = 0; i < roles.getLength(); i++) {
            Node role = roles.item(i);
            String type = ((Element)role).getAttribute("type");
            if(type.equals("author")) {
                Log.d(TAG, "Author IDs - " + role.getTextContent());
                String[] splitIds = role.getTextContent().split("\\s+");
                authorIdList.addAll(Arrays.asList(splitIds));
            }
        }
        
        authorsList = new ArrayList<String>();
        NodeList people = metadataDoc.getElementsByTagName("md:person");
        NodeList organisations = metadataDoc.getElementsByTagName("md:organization");
        getAuthorsFromNodelist(people, authorIdList);
        getAuthorsFromNodelist(organisations, authorIdList);
        
        for(String name : authorsList) {
            if(authorsList.indexOf(name) == 0)
                authors = name;
            else
                authors += ", " + name;
        }
        
        // Get the definitions
        NodeList definitionNodes = moduleDoc.getElementsByTagName("definition");
        
        if(definitionNodes.getLength() == 0)
            return ParseResult.NO_NODES;
        
        extractDefinitions(definitionNodes);
        addValuesToDatabase(id);

        return ParseResult.SUCCESS;
    }

    
    private void getAuthorsFromNodelist(NodeList actorNodes, ArrayList<String> authorIdList) {
        for(int i = 0; i < actorNodes.getLength(); i++) {
            Node org = actorNodes.item(i);
            String userid = ((Element)org).getAttribute("userid");
            if(authorIdList.contains(userid)) {
                NodeList children = org.getChildNodes();
                for(int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if(child.getNodeName().equals("md:fullname")) {
                        authorsList.add(child.getTextContent());
                        Log.d(TAG, "Author's full name - " + child.getTextContent());
                    }
                }
            }
        }
    }


    public Document retrieveMetadataXML() {
        URL url;
        URLConnection conn;
        InputStream in = null;

        try {
            url = new URL("http://cnx.org/content/" + id + "/latest/metadata");
            conn = url.openConnection();
            in = conn.getInputStream();

            metadataDoc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            try {
                docBuilder = dbf.newDocumentBuilder();
                metadataDoc = docBuilder.parse(in);
            } catch (ParserConfigurationException pce) {
                Log.d(TAG, "Caught parser exception.");
            } catch (SAXException e) {
                Log.d(TAG, "Caught SAX exception.");
            }

            return metadataDoc;
        } catch (MalformedURLException mue) {
        } catch (IOException ioe) {
        }

        return null;
    }

    
    /**
     * Retrieve the CNXML file as a list of nodes
     * 
     * @param id
     **/
    public void retrieveModuleXML() {
        URL url;
        URLConnection conn;
        InputStream in = null;

        try {
            url = new URL("http://cnx.org/content/" + id
                    + "/latest/module_export?format=plain"); // m9006/2.22
            conn = url.openConnection();
            in = conn.getInputStream();

            moduleDoc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            try {
                docBuilder = dbf.newDocumentBuilder();
                moduleDoc = docBuilder.parse(in);
            } catch (ParserConfigurationException pce) {
                Log.d(TAG, "Caught parser exception.");
            } catch (SAXException e) {
                Log.d(TAG, "Caught SAX exception.");
            }
            
        } catch (MalformedURLException mue) {
        } catch (IOException ioe) {
        }
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
                
                if(term != null && meaning != null) {
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
    }
    
    
    /** Add the parsed definitions to the database. **/
    private Uri addValuesToDatabase(String id) {
        ContentValues values;

        // Insert deck first to check for duplicates
        values = new ContentValues();
        values.put(MODULE_ID, id);
        values.put(TITLE, title);
        values.put(ABSTRACT, summary);
        values.put(AUTHOR, authors);
        Uri deckUri = context.getContentResolver().insert(
                DeckProvider.CONTENT_URI, values);

        if (deckUri == null)
            return null;

        int newDeckRowID = (int) ContentUris.parseId(deckUri);
        
        for (int i = 0; i < terms.size(); i++) {
            values = new ContentValues();
            values.put(DECK_ID, newDeckRowID);
            values.put(MEANING, meanings.get(i));
            values.put(TERM, terms.get(i));
            context.getContentResolver().insert(CardProvider.CONTENT_URI,
                    values);
        }

        return deckUri;
    }
    
    
    /** Check if this particular module has already been downloaded + added to the database */
    public boolean isDuplicate() {
        String[] projection = {MODULE_ID};
        String selection = MODULE_ID + " = '" + id + "'";
        Cursor idCursor = context.getContentResolver().query(DeckProvider.CONTENT_URI, projection, selection, null, null);
        int count = idCursor.getCount();
        idCursor.close();
        
        if(count == 0)
            return false;
        else 
            return true;
    }

    
    /** Get a value with a given tag from a node **/
    private String getValue(String tagname, Node n) {
        // TODO: This needs better error handling around all of it.
        Node value;
        
        try {
            NodeList childnodes = ((Element) n).getElementsByTagName(tagname).item(0).getChildNodes();
            value = (Node) childnodes.item(0);
            
        }
        catch (NullPointerException npe) {
            value = null;
        }
        
        if(value == null) {
            return null;
        }
        else return value.getNodeValue();
    }
}
