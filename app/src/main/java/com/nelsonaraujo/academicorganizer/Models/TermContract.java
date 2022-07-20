package com.nelsonaraujo.academicorganizer.Models;

import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY;
import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Term database structure details and content provider URI manipulation.
 */
public class TermContract {
    static final String TABLE_NAME = "term";

    // term fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START = "start";
        public static final String END = "end";

        private Columns(){
            // private constructor to prevent instantiation.
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Build the term uri with the id appended.
     * @param termId term id to append.
     * @return uri with id appended.
     */
    public static Uri buildTermUri(long termId){
        return ContentUris.withAppendedId(CONTENT_URI, termId);
    }

    /**
     * Get the term id from the uri.
     * @param uri uri with the id.
     * @return id
     */
    static long getTermId(Uri uri){
        return ContentUris.parseId(uri);
    }
}