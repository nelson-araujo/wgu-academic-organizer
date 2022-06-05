package com.nelsonaraujo.academicorganizer.Models;

import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY;
import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TermContract {
    static final String TABLE_NAME = "term";

    // term fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TERM_TITLE = "title";
        public static final String TERM_START = "start";
        public static final String TERM_END = "end";

        private Columns(){
            // private constructor to prevent instantiation.
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTermUri(long termId){
        return ContentUris.withAppendedId(CONTENT_URI, termId);
    }

    static long getTermId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
