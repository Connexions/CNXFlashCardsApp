/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards;

import static org.cnx.quizcards.Constants.ABSTRACT;
import static org.cnx.quizcards.Constants.AUTHOR;
import static org.cnx.quizcards.Constants.DECK_ID;
import static org.cnx.quizcards.Constants.MEANING;
import static org.cnx.quizcards.Constants.MODULE_ID;
import static org.cnx.quizcards.Constants.TAG;
import static org.cnx.quizcards.Constants.TERM;
import static org.cnx.quizcards.Constants.TITLE;

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

import org.cnx.quizcards.database.CardProvider;
import org.cnx.quizcards.database.DeckProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    
    private Context context;
    String id;

    private ArrayList<String> terms;
    private ArrayList<String> meanings;
    private ArrayList<String> authorsList;
    
    private String title;
    private String authors;
    private String summary;
    
    private Document moduleDoc;
    private Document metadataDoc;
    NodeList definitionNodes;
    
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
    public void parseXML() {
        
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
                String[] splitIds = role.getTextContent().split("\\s+");
                authorIdList.addAll(Arrays.asList(splitIds));
            }
        }
        
        // Match the userids of the authors with the userids of people/organisations, to get names
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
        definitionNodes = moduleDoc.getElementsByTagName("definition");
    }

    
    /** Check if the XML documents were properly retrieved */
    public boolean gotXML() {
        /* Check that a valid document was returned
         * TODO: Better error handling here.
         */
        if (moduleDoc != null && metadataDoc != null)
            return true;
        else
            return false;
    }
   
    
    /** Check if there were any definitions in the module */
    public boolean hasDefinitions() {
        if(definitionNodes.getLength() == 0)
            return false;
        else
            return true;
    }
    
    
    /** Get author names from a NodeList of actors and the list of userids */
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
                    }
                }
            }
        }
    }


    /** Download the metadata CNXML file for this module */
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

    
    /** Download the CNXML file for this module */
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
    public void extractDefinitions() {
        for (int i = 0; i < definitionNodes.getLength(); i++) {
            Node n = definitionNodes.item(i);

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
    public Uri addValuesToDatabase() {
        ContentValues values;

        // Insert deck
        values = new ContentValues();
        values.put(MODULE_ID, id);
        values.put(TITLE, title);
        values.put(ABSTRACT, summary);
        values.put(AUTHOR, authors);
        Uri deckUri = context.getContentResolver().insert(
                DeckProvider.CONTENT_URI, values);

        // DECK_ID of the cards is the row _id of its deck
        int newDeckRowID = (int) ContentUris.parseId(deckUri);
        
        // Insert cards
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
        Node value;
        
        try {
            NodeList childnodes = ((Element) n).getElementsByTagName(tagname).item(0).getChildNodes();
            value = (Node) childnodes.item(0); 
        }
        catch (NullPointerException npe) {
            value = null;
        }
        
        if(value == null)
            return null;
        else 
            return value.getNodeValue();
    }
}
