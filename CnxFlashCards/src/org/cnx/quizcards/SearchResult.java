/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards;

public class SearchResult {
    private String title = "";
    private String authors = "";
    private String url = "";
    private String id = "";
    
    
    public SearchResult(String title, String authors, String url, String id) {
        this.title = title;
        this.authors = authors;
        this.url = url;
        this.setId(id);
    }
    
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    
    public String getAuthors() {
        return authors;
    }
    
    public void setAuthors(String authors) {
        this.authors = "By " + authors;
    }
    
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
