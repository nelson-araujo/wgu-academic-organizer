package com.nelsonaraujo.academicorganizer.Models;

import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY;
import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Assessment database structure details and content provider URI manipulation.
 */
public class AssessmentContract {
    static final String TABLE_NAME = "assessment";

    // assessment fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START = "start";
        public static final String END = "end";
        public static final String CONTENT = "content";
        public static final String COURSE_ID = "course_id";

        private Columns(){
            // private constructor to prevent instantiation.
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Build the assessment uri with the id appended.
     * @param AssessmentId id to append
     * @return uri with id appended.
     */
    public static Uri buildAssessmentUri(long AssessmentId){
        return ContentUris.withAppendedId(CONTENT_URI, AssessmentId);
    }

    /**
     * Get the assessment id from the uri.
     * @param uri uri with id.
     * @return id
     */
    static long getAssessmentId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
