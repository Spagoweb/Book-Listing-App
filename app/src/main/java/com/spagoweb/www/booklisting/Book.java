package com.spagoweb.www.booklisting;

/**
 * Created by Robe on 23/07/2017.
 */


public class Book {

    private String mTitle;
    private String mAuthor;
    private String mDescription;

    /**
     * Constructs a new {@link Book} object.
     */
    public Book(String title, String author, String description) {
        mTitle = title;
        mAuthor = author;
        mDescription = description;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getDescription(){
        return mDescription;
    }
}

