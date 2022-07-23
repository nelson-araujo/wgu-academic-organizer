package com.nelsonaraujo.academicorganizer.Models;

/**
 * Data structure for the search items list.
 */
public class SearchItem {
    String title;
    String type;
    long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {

        return title + " (" + type + ")";
    }
}
