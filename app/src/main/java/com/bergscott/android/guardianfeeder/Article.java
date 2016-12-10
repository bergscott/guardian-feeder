package com.bergscott.android.guardianfeeder;

/**
 * Represents an article on theguardian.com website
 */

public class Article {

    /** Title of the article */
    private String mTitle;

    /** url linking to the full article */
    private String mUrl;

    /** Name of the author of the article */
    private String mAuthor;

    /** Date of the article's publication in milliseconds */
    private long mDateInMilliseconds;

    /** Section of The Guardian that the article belongs to */
    private String mSection;

    /**
     * Creates a new Article object
     * @param title title of the article
     * @param url url of the full article
     * @param author author of the article
     * @param date date of the article's publication
     * @param section section of The Guardian that the article belongs to
     */
    public Article(String title, String url, String author, long date, String section) {
        this.mTitle = title;
        this.mUrl = url;
        this.mAuthor = author;
        this.mDateInMilliseconds = date;
        this.mSection = section;
    }

    /** Returns the article's title */
    public String getTitle() {
        return mTitle;
    }

    /** Returns the article's url */
    public String getUrl() {
        return mUrl;
    }

    /** Returns the article's author */
    public String getAuthor() {
        return mAuthor;
    }

    /** Returns the article's date in milliseconds */
    public long getDateInMilliseconds() {
        return mDateInMilliseconds;
    }

    /** Returns the article's section */
    public String getSection() {
        return mSection;
    }
}
