package org.cnx.flashcards;

public class SearchResult {
    private String title = "";
    private String authors = "";
    private String url = "";
    
    
    public SearchResult(String title, String authors, String url) {
        this.title = title;
        this.authors = authors;
        this.url = url;
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
        this.authors = authors;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
